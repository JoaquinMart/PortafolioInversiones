package controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.skin.ComboBoxBaseSkin;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import service.*;
import util.ConexionSQL;
import model.*;
import dao.*;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static java.math.BigDecimal.valueOf;

public class MainController {
    private IOLAuthService authService;
    private IOLApiService apiService;

    @FXML private ComboBox<String> cbTipoInversion;

    @FXML private TableView<InversionAgrupada> tablaInversiones;
    @FXML private TableColumn<InversionAgrupada, String> colInversionNombre;
    @FXML private TableColumn<InversionAgrupada, Integer> colInversionCantidad;
    @FXML private TableColumn<InversionAgrupada, BigDecimal> colPrecioAccion;
    @FXML private TableColumn<InversionAgrupada, Double> colTotalIncial;
    @FXML private TableColumn<InversionAgrupada, Double> colPrecioAccionHoy;
    @FXML private TableColumn<InversionAgrupada, Double> colRendimientoPesos;
    @FXML private TableColumn<InversionAgrupada, Double> colTotalEnDolarInicial;
    @FXML private TableColumn<InversionAgrupada, Double> colPrecioDolar;
    @FXML private TableColumn<InversionAgrupada, Double> colRendimientoEnDolares;

    @FXML private Label totalPesosLabel;
    @FXML private Label totalDolaresLabel;
    @FXML private Label gananciaPorcentajePesosLabel;
    @FXML private Label gananciaPorcentajeDolaresLabel;

    @FXML private GridPane formularioInversion;

    @FXML private Portafolio portafolioActual;
    @FXML private ComboBox<Portafolio> portafolioSelector;
    @FXML private TextField nombrePortafolio;
    @FXML private InversionController formularioInversionController;


    private PortafolioService portafolioService = new PortafolioService();
    private PortafolioDAO portafolioDAO = new PortafolioDAO();
    private InversionDAO inversionDAO = new InversionDAO();
    private InversionSimpleDAO inversionSimpleDAO = new InversionSimpleDAO();


    @FXML
    public void initialize() {
        try {
            ConexionSQL.crearTablas();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (formularioInversionController != null) {
            formularioInversionController.setMainController(this);
        }

        colInversionNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecioAccionHoy.setCellValueFactory(new PropertyValueFactory<>("ultimoValor"));
        configurarFormatoMoneda(colPrecioAccionHoy);

        // Configuración de columnas de Inversion Agrupada
        colInversionNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colInversionCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioAccion.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrecioCompraPromedio()));
        colPrecioAccion.setCellFactory(column -> new TableCell<InversionAgrupada, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    InversionAgrupada inv = getTableView().getItems().get(getIndex());
                    String valorFormateado = item.setScale(2, RoundingMode.HALF_UP).toString();
                    if (inv.isEnDolares()) {
                        setText("U$D " + valorFormateado);
                    } else {
                        setText("$ " + valorFormateado);
                    }
                }
            }
        });

        colPrecioAccionHoy.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrecioAccionHoy()));
        colTotalIncial.setCellValueFactory(new PropertyValueFactory<>("totalInicial"));
        colRendimientoPesos.setCellValueFactory(new PropertyValueFactory<>("rendimiento"));
        colPrecioDolar.setCellValueFactory(new PropertyValueFactory<>("precioDolar"));
//        colTotalEnDolarInicial.setCellValueFactory(new PropertyValueFactory<>("totalEnDolaresInicial"));
//        colRendimientoEnDolares.setCellValueFactory(new PropertyValueFactory<>("rendimientoEnDolares"));

        // Corregir formatos
//        configurarFormatoMonedaBD(colPrecioAccion);
        configurarFormatoMoneda(colPrecioAccionHoy);
        configurarFormatoMoneda(colTotalIncial);
        configurarFormatoRendimiento(colRendimientoPesos);
        configurarFormatoMoneda(colPrecioDolar);
//        configurarFormatoMoneda(colTotalEnDolarInicial);
//        configurarFormatoRendimiento(colRendimientoEnDolares);

        // Cargar portafolios
        List<Portafolio> portafolios = portafolioDAO.getPortafolios();
        ObservableList<Portafolio> portafoliosList = FXCollections.observableArrayList(portafolios);
        portafolioSelector.setItems(portafoliosList);

        portafolioSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            portafolioActual = newSel;
            if (newSel != null) {
                CompletableFuture.runAsync(() -> {
                    try {
                        // Traer inversiones desde DB
                        List<Inversion> inversiones = inversionDAO.getInversionesPorPortafolio(newSel.getId());

                        // Traemos dólar MEP también
                        DolarApiService dolarService = new DolarApiService();
                        double mep = dolarService.getDolarMep();
                        InversionAgrupada.setDolarMepActual(mep);

                        // Traer todos los valores cacheados en una sola consulta
                        InversionSimpleDAO simpleDAO = new InversionSimpleDAO();
                        Map<String, BigDecimal> cacheMap = simpleDAO.getUltimosValores();

                        // Aplicar valores cacheados a las inversiones
                        for (Inversion inv : inversiones) {
                            if (cacheMap.containsKey(inv.getNombre())) {
                                inv.setPrecioAccion(cacheMap.get(inv.getNombre()));
                            } else {
                                inv.setPrecioAccion(BigDecimal.ZERO);
                            }
                        }

                        // Mostrar tabla inmediatamente con valores cacheados
                        Platform.runLater(() -> actualizarTabla(inversiones));

                        // Inicializar servicio de API
                        IOLAuthService authService = new IOLAuthService();
                        IOLApiService apiService = new IOLApiService(authService);

                        // Actualizar precios
                        for (Inversion inv : inversiones) {
                            CompletableFuture.runAsync(() -> {
                                try {
                                    String respuesta = apiService.getCotizacion(inv.getNombre());
                                    JsonNode node = new ObjectMapper().readTree(respuesta);
                                    JsonNode precioNode = node.path("ultimoPrecio");
                                    if (!precioNode.isMissingNode() && !precioNode.isNull()) {
                                        BigDecimal precio = precioNode.decimalValue();
                                        simpleDAO.actualizarValor(inv.getNombre(), precio);
                                        inv.setPrecioAccion(precio);
                                        Platform.runLater(() -> actualizarTabla(inversiones));
                                    }
                                } catch (Exception ex) { ex.printStackTrace(); }
                            });
                        }
                    } catch (SQLException e) { e.printStackTrace(); }
                });
            } else { tablaInversiones.getItems().clear(); }
        });

        CompletableFuture.runAsync(() -> {
            try {
                DolarApiService dolarService = new DolarApiService();
                double mep = dolarService.getDolarMep();
                InversionAgrupada.setDolarMepActual(mep);
                System.out.println("Dolar MEP actual: " + mep);

                Platform.runLater(() -> {
                    tablaInversiones.refresh();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        tablaInversiones.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tablaInversiones.getSelectionModel().isEmpty()) {
                InversionAgrupada seleccionada = tablaInversiones.getSelectionModel().getSelectedItem();
                abrirDetalle(seleccionada.getNombre());
            }
        });
    }

    @FXML
    private void abrirDetalle(String nombre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/detalle.fxml"));
            Parent root = loader.load();

            DetalleController controller = loader.getController();
            List<Inversion> inversionConNombre = inversionDAO.getInversionesPorNombre(nombre);
            controller.setInversion(inversionConNombre);

            Stage stage = new Stage();
            stage.setTitle("Detalle de: " + nombre);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void crearPortafolio() {
        String nombre = nombrePortafolio.getText().trim();
        if (nombre.isEmpty()) {
            System.out.println("El nombre del portafolio no puede estar vacío.");
            return;
        }
        Portafolio nuevoPortafolio = new Portafolio();
        nuevoPortafolio.setNombre(nombre);
        nuevoPortafolio.setFechaCreacion(LocalDate.now());
        portafolioDAO.crearPortafolio(nuevoPortafolio);
        portafolioSelector.getItems().add(nuevoPortafolio);
        portafolioSelector.getSelectionModel().select(nuevoPortafolio);
        nombrePortafolio.clear();
    }

    @FXML
    private void eliminarPortafolio() {
        Portafolio portafolioSeleccionado = portafolioSelector.getSelectionModel().getSelectedItem();
        if (portafolioSeleccionado == null) {
            System.out.println("Debe seleccionar un portafolio para eliminar.");
            return;
        }

         if (!mostrarConfirmacion("¿Está seguro que desea eliminar este portafolio y todas sus inversiones?")) {
             System.out.println("Eliminación cancelada por el usuario.");
             return;
         }

        try {
            inversionDAO.eliminarInversionesPorPortafolio(portafolioSeleccionado.getId());
            portafolioDAO.eliminarPortafolio(portafolioSeleccionado.getId());

            portafolioSelector.getItems().remove(portafolioSeleccionado);
            tablaInversiones.getItems().clear();
            System.out.println("Portafolio '" + portafolioSeleccionado.getNombre() + "' eliminado.");

        } catch (SQLException e) {
            System.err.println("Error al eliminar el portafolio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void actualizarTabla(List<Inversion> inversiones) {
        Map<String, InversionAgrupada> mapa = new HashMap<>();

        BigDecimal totalInvertidoPesos = BigDecimal.ZERO;
        BigDecimal totalPesos = BigDecimal.ZERO;
        BigDecimal totalInvertidoDolares = BigDecimal.ZERO;
        BigDecimal totalDolares = BigDecimal.ZERO;

        BigDecimal dolarMep = BigDecimal.valueOf(InversionAgrupada.getPrecioDolar());

        for (Inversion inv : inversiones) {
            String key = inv.getNombre().toLowerCase();
            mapa.putIfAbsent(key, new InversionAgrupada(inv.getNombre()));
            mapa.get(key).acumular(inv);

            BigDecimal cantidadBD = BigDecimal.valueOf(Math.abs(inv.getCantidad()));

            if (!inv.isEsDolares()) {
                BigDecimal invertidoPesos = inv.getPrecioCompra().multiply(cantidadBD);
                BigDecimal actualPesos = inv.getPrecioAccion().multiply(cantidadBD);

                totalInvertidoPesos = totalInvertidoPesos.add(invertidoPesos);
                totalPesos = totalPesos.add(actualPesos);

                if (dolarMep.compareTo(BigDecimal.ZERO) > 0) {
                    totalInvertidoDolares = totalInvertidoDolares.add(
                            invertidoPesos.divide(dolarMep, 6, RoundingMode.HALF_UP)
                    );
                    totalDolares = totalDolares.add(
                            actualPesos.divide(dolarMep, 6, RoundingMode.HALF_UP)
                    );
                }
            } else {
                BigDecimal invertidoDolares = inv.getPrecioCompra().multiply(cantidadBD);
                BigDecimal actualDolares = inv.getPrecioAccion().multiply(cantidadBD);

                totalInvertidoDolares = totalInvertidoDolares.add(invertidoDolares);
                totalDolares = totalDolares.add(actualDolares);

                if (dolarMep.compareTo(BigDecimal.ZERO) > 0) {
                    totalInvertidoPesos = totalInvertidoPesos.add(invertidoDolares.multiply(dolarMep));
                    totalPesos = totalPesos.add(actualDolares.multiply(dolarMep));
                }
            }
        }

        // --- Ganancias ---
        BigDecimal gananciaPesos = totalPesos.subtract(totalInvertidoPesos);
        BigDecimal gananciaDolares = totalDolares.subtract(totalInvertidoDolares);

        BigDecimal porcentajeGananciaPesos = BigDecimal.ZERO;
        if (totalInvertidoPesos.compareTo(BigDecimal.ZERO) != 0) {
            porcentajeGananciaPesos = gananciaPesos.divide(totalInvertidoPesos, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        BigDecimal porcentajeGananciaDolares = BigDecimal.ZERO;
        if (totalInvertidoDolares.compareTo(BigDecimal.ZERO) != 0) {
            porcentajeGananciaDolares = gananciaDolares.divide(totalInvertidoDolares, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        // --- Mostrar en labels ---
        totalPesosLabel.setText(String.format("$ %,.2f", totalPesos));
        gananciaPorcentajePesosLabel.setText(String.format("%,.2f %%", porcentajeGananciaPesos));
        totalDolaresLabel.setText(String.format("$ %,.2f", totalDolares));
        gananciaPorcentajeDolaresLabel.setText(String.format("%,.2f %%", porcentajeGananciaDolares));

        // Colores condicionales
        gananciaPorcentajePesosLabel.getStyleClass().add(gananciaPesos.compareTo(BigDecimal.ZERO) >= 0
                ? "rendimiento-positivo" : "rendimiento-negativo");
        gananciaPorcentajeDolaresLabel.getStyleClass().add(gananciaDolares.compareTo(BigDecimal.ZERO) >= 0
                ? "rendimiento-positivo" : "rendimiento-negativo");

        tablaInversiones.setItems(FXCollections.observableArrayList(mapa.values()));
    }


    private boolean mostrarConfirmacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }
    public Portafolio getPortafolioActual() { return portafolioActual; }
    private void configurarFormatoMoneda(TableColumn<InversionAgrupada, Double> columna) {
        columna.setCellFactory(column -> new TableCell<InversionAgrupada, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$ %,.2f", item));
                }
            }
        });
    }
    private void configurarFormatoMonedaBD(TableColumn<InversionAgrupada, BigDecimal> columna) {
        columna.setCellFactory(column -> new TableCell<InversionAgrupada, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("$ " + String.format("%,.2f", item));
                }
            }
        });
    }
    private void configurarFormatoRendimiento(TableColumn<InversionAgrupada, Double> columna) {
        columna.setCellFactory(column -> new TableCell<InversionAgrupada, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);

                // Elimina las clases anteriores para evitar conflictos
                getStyleClass().removeAll("rendimiento-positivo", "rendimiento-negativo");

                if (empty || item == null) {
                    setText(null);
                } else {
                    // Aplica la clase CSS y el formato según el valor
                    if (item > 0) {
                        getStyleClass().add("rendimiento-positivo");
                    } else if (item < 0) {
                        getStyleClass().add("rendimiento-negativo");
                    }

                    // Formatea el valor con porcentaje
                    setText(String.format("%,.2f %%", item));
                }
            }
        });
    }
}