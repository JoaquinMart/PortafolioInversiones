package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Portafolio;

public class PortafolioController {

    @FXML private TableView<Portafolio> tablaPortafolios;
    @FXML private TableColumn<Portafolio, Integer> colPortafolioId;
    @FXML private TableColumn<Portafolio, String> colPortafolioNombre;

    @FXML
    public void initialize() {
        colPortafolioId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPortafolioNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
    }

    public Portafolio getPortafolioSeleccionado() {
        return tablaPortafolios.getSelectionModel().getSelectedItem();
    }
}
