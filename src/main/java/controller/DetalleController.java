package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Inversion;

import javafx.scene.control.TableView;
import java.time.LocalDate;
import java.util.List;

public class DetalleController {
    @FXML private TableView<Inversion> tablaDetalle;
    @FXML private TableColumn<Inversion, String> colNombre;
    @FXML private TableColumn<Inversion, Integer> colPrecioCompra;
    @FXML private TableColumn<Inversion, LocalDate> colFechaCompra;
    @FXML private TableColumn<Inversion, Integer> colCantidad;
    @FXML private TableColumn<Inversion, Double> colPrecioDolar;
    @FXML private TableColumn<Inversion, Double> getColPrecioAccionDolar;
    @FXML private TableColumn<Inversion, Double> colComisionCompra;

    private ObservableList<Inversion> inversiones = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecioCompra.setCellValueFactory(new PropertyValueFactory<>("precioCompra"));
        colFechaCompra.setCellValueFactory(new PropertyValueFactory<>("fechaCompra"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioDolar.setCellValueFactory(new PropertyValueFactory<>("precioDolar"));
        getColPrecioAccionDolar.setCellValueFactory(new PropertyValueFactory<>("precioAccionEnDolar"));
        colComisionCompra.setCellValueFactory(new PropertyValueFactory<>("comisionCompra"));
        tablaDetalle.setItems(inversiones);
        tablaDetalle.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void setInversion(List<Inversion> lista) { inversiones.setAll(lista); }

}
