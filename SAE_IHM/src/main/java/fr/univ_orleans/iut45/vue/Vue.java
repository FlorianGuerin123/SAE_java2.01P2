package fr.univ_orleans.iut45.vue;
import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.*;



public class Vue extends Application {
    
    private BorderPane panelCentral;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            panelCentral = new BorderPane();
            this.modeConnexion();
            Scene scene = new Scene(panelCentral, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Application JavaFX");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void modeAcceuil() {
        try {
            BorderPane root = FXMLLoader.load(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/Menu.fxml"));
            panelCentral.setCenter(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modeConnexion() {
        try {
            BorderPane root = FXMLLoader.load(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/Connexion.fxml"));
            panelCentral.setCenter(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modeCollectionneur() {
        try {
            BorderPane root = FXMLLoader.load(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/Collectionneur.fxml"));
            panelCentral.setLeft(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}