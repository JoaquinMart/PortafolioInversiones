package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.text.Font;
import service.IOLApiService;
import service.IOLAuthService;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-Light.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-Medium.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-SemiBold.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-Bold.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Roboto-Thin.ttf"), 14);

        primaryStage.setTitle("Gestor de Inversiones");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}