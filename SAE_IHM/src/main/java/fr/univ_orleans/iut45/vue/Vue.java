package fr.univ_orleans.iut45.vue;
 
import fr.univ_orleans.iut45.controleur.ConnexionControleur;
import fr.univ_orleans.iut45.controleur.MainMenuControleur;
import fr.univ_orleans.iut45.modele.ConnexionMySQL;
 
import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.*;
 
 
public class Vue extends Application {
 
    private BorderPane panelCentral;
    private ConnexionMySQL connexionMySQL;
 
    public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage primaryStage) {
        try {
            connexionMySQL = new ConnexionMySQL();
 
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
 
    /**
     * Permet aux contrôleurs (Connexion, MainMenu, ...) d'accéder
     * à la connexion à la base de données.
     */
    public ConnexionMySQL getConnexionMySQL() {
        return connexionMySQL;
    }
 
    public void modeAcceuil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/Menu.fxml"));
            BorderPane root = loader.load();
 
            MainMenuControleur controleur = loader.getController();
            controleur.setVue(this);
 
            panelCentral.setCenter(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    public void modeConnexion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/Connexion.fxml"));
            BorderPane root = loader.load();
 
            ConnexionControleur controleur = loader.getController();
            controleur.setVue(this);
 
            panelCentral.setCenter(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modeCollectionneur() {
        try {
            VBox vb = FXMLLoader.load(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/CollectionneurNav.fxml"));
            this.panelCentral.setLeft(vb);
        } catch (Exception e) {
            e.printStackTrace();
    }
        
    }

}
