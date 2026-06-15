package fr.univ_orleans.iut45.vue;
import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.*;



public class Vue extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            BorderPane root = FXMLLoader.load(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/Menu.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Application BRIQU'IUTO");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 