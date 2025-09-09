package controller;

import dao.InversionDAO;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import model.Inversion;

import javafx.event.ActionEvent;
import java.math.BigDecimal;
import java.time.LocalDate;

public class InversionController {
    @FXML private GridPane rootGrid;
    @FXML private ComboBox<String> cbTipoInversion;
    @FXML private TextField tfNombreInversion;
    @FXML private TextField tfPrecioCompra;
    @FXML private TextField tfPrecioDolar;
    @FXML private TextField tfCantidad;
    @FXML private DatePicker dpFecha;
    @FXML private TextField tfComisionCompra;

    private MainController mainController;
    private InversionDAO inversionDAO;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        this.inversionDAO = new InversionDAO();
    }

    @FXML
    private void initialize() {
        cbTipoInversion.getSelectionModel().selectFirst();
        var focusableNodes = rootGrid.getChildren().stream()
                .filter(node -> node.isFocusTraversable())
                .sorted((n1, n2) -> {
                    Integer r1 = GridPane.getRowIndex(n1) != null ? GridPane.getRowIndex(n1) : 0;
                    Integer r2 = GridPane.getRowIndex(n2) != null ? GridPane.getRowIndex(n2) : 0;
                    Integer c1 = GridPane.getColumnIndex(n1) != null ? GridPane.getColumnIndex(n1) : 0;
                    Integer c2 = GridPane.getColumnIndex(n2) != null ? GridPane.getColumnIndex(n2) : 0;
                    return r1.equals(r2) ? c1 - c2 : r1 - r2;
                })
                .toList();

        for (int i = 0; i < focusableNodes.size(); i++) {
            final int nextIndex = (i + 1) % focusableNodes.size();
            focusableNodes.get(i).setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case ENTER -> focusableNodes.get(nextIndex).requestFocus();
                }
            });
        }
        cbTipoInversion.getSelectionModel().clearSelection();
    }

    @FXML
    private void agregarInversion(ActionEvent event) {
        if (mainController.getPortafolioActual() == null) {
            System.out.println("Debe seleccionar un portafolio primero.");
            return;
        }
        try {
            String nombre = tfNombreInversion.getText();
            int cantidad = Integer.parseInt(tfCantidad.getText());
            BigDecimal precioInput = new BigDecimal(tfPrecioCompra.getText()); // precio tal como lo ingresó el usuario
            LocalDate fecha = dpFecha.getValue();
            double precioDolar = Double.parseDouble(tfPrecioDolar.getText());
            double comisionCompra = Double.parseDouble(tfComisionCompra.getText()) / 100;
            String tipo = cbTipoInversion.getValue();

            boolean esVenta = false;
            boolean esDolar = false;

            switch (tipo) {
                case "Compra":
                    break;
                case "Venta":
                    esVenta = true;
                    break;
                case "Compra en dolares":
                    esDolar = true;
                    break;
                case "Venta en dolares":
                    esVenta = true;
                    esDolar = true;
                    break;
                default:
                    System.out.println("Seleccione tipo de operación.");
                    return;
            }

            // --- calculos sin mutar el precio original ---
            BigDecimal precioCompraEnPesos;
            double precioAccionEnDolar;

            if (esDolar) {
                // Usuario ingresó el precio de la acción EN USD (ej: 30 USD)
                precioAccionEnDolar = precioInput.doubleValue();                  // guardamos el precio en USD
                precioCompraEnPesos = precioInput.multiply(BigDecimal.valueOf(precioDolar)); // convertimos a pesos
            } else {
                // Usuario ingresó el precio EN PESOS (ej: 30000 ARS)
                precioCompraEnPesos = precioInput;
                // guardamos precio de la acción expresado en USD (para referencia)
                precioAccionEnDolar = precioInput.doubleValue() / (precioDolar == 0 ? 1 : precioDolar);
            }

            Inversion nueva = new Inversion();
            nueva.setNombre(nombre);
            nueva.setFechaCompra(fecha);
            nueva.setPrecioCompra(precioCompraEnPesos);      // en pesos (si venía en USD, ya convertido)
            nueva.setPrecioDolar(precioDolar);
            nueva.setPrecioAccionEnDolar(precioAccionEnDolar); // siempre guardamos el valor por acción en USD (positivo)
            nueva.setCantidad(cantidad);
            nueva.setComisionCompra(comisionCompra);
            nueva.setEnDolares(esDolar);   // asegúrate que getter -> isEnDolares()
            nueva.setEsVenta(esVenta);     // getter -> isVenta()
            nueva.setPortafolioId(mainController.getPortafolioActual().getId());

            inversionDAO.crearInversion(nueva);
            mainController.getPortafolioActual().addInversion(nueva);
            mainController.actualizarTabla(mainController.getPortafolioActual().getInversiones());

            // Limpiar campos
            tfNombreInversion.clear();
            dpFecha.setValue(null);
            tfPrecioCompra.clear();
            tfPrecioDolar.clear();
            tfCantidad.clear();
            tfComisionCompra.clear();

        } catch (NumberFormatException e) {
            System.out.println("Error: algunos campos numéricos no son válidos.");
        }
    }
}
