package fr.univ_orleans.iut45.controleur;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class CollectionneurControleur {

    @FXML private StackPane centerPane;

    // Boutons de navigation
    @FXML private Button btnRechercher;
    @FXML private Button btnDetail;
    @FXML private Button btnTheme;
    @FXML private Button btnStatistiques;
    @FXML private Button btnBoitesPiece;
    @FXML private Button btnMaCollection;
    @FXML private Button btnPiecesManquantes;
    @FXML private Button btnComposer;
    @FXML private Button btnRetour;

    private static final String STYLE_ACTIF =
        "-fx-background-color: #FF4D6A; -fx-text-fill: white; " +
        "-fx-font-size: 12; -fx-font-weight: bold; " +
        "-fx-alignment: CENTER_LEFT; -fx-padding: 10 16 10 16; " +
        "-fx-background-radius: 0; -fx-cursor: hand; -fx-border-width: 0;";

   
    private static final String STYLE_INACTIF =
        "-fx-background-color: transparent; -fx-text-fill: #B0C8E0; " +
        "-fx-font-size: 12; -fx-alignment: CENTER_LEFT; " +
        "-fx-padding: 10 16 10 16; -fx-background-radius: 0; " +
        "-fx-cursor: hand; -fx-border-width: 0;";

    
    private static final String STYLE_HOVER =
        "-fx-background-color: #1E3050; -fx-text-fill: white; " +
        "-fx-font-size: 12; -fx-alignment: CENTER_LEFT; " +
        "-fx-padding: 10 16 10 16; -fx-background-radius: 0; " +
        "-fx-cursor: hand; -fx-border-width: 0;";

    private Button boutonActif = null;

   

    @FXML
    private void onNavHover(javafx.scene.input.MouseEvent event) {
        Button btn = (Button) event.getSource();
        if (btn != boutonActif) {
            btn.setStyle(STYLE_HOVER);
        }
    }

    @FXML
    private void onNavExit(javafx.scene.input.MouseEvent event) {
        Button btn = (Button) event.getSource();
        if (btn != boutonActif) {
            btn.setStyle(STYLE_INACTIF);
        }
    }

    @FXML
    private void onRetourHover(javafx.scene.input.MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setStyle("-fx-background-color: #1E3050; -fx-text-fill: #B0C8E0; " +
                     "-fx-font-size: 11; -fx-alignment: CENTER_LEFT; " +
                     "-fx-padding: 12 16 12 16; -fx-background-radius: 0; " +
                     "-fx-cursor: hand; -fx-border-width: 0;");
    }

    @FXML
    private void onRetourExit(javafx.scene.input.MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #5A7A9A; " +
                     "-fx-font-size: 11; -fx-alignment: CENTER_LEFT; " +
                     "-fx-padding: 12 16 12 16; -fx-background-radius: 0; " +
                     "-fx-cursor: hand; -fx-border-width: 0;");
    }


    private void setActif(Button btn) {
        if (boutonActif != null) {
            boutonActif.setStyle(STYLE_INACTIF);
        }
        btn.setStyle(STYLE_ACTIF);
        boutonActif = btn;
    }


    @FXML
    private void handleRechercher(ActionEvent event) {
        setActif(btnRechercher);
        // TODO : charger la vue Rechercher dans centerPane
    }

    @FXML
    private void handleDetail(ActionEvent event) {
        setActif(btnDetail);
        // TODO : charger la vue Détail
    }

    @FXML
    private void handleTheme(ActionEvent event) {
        setActif(btnTheme);
        // TODO : charger la vue Thème
    }

    @FXML
    private void handleStatistiques(ActionEvent event) {
        setActif(btnStatistiques);
        // TODO : charger la vue Statistiques
    }

    @FXML
    private void handleBoitesPiece(ActionEvent event) {
        setActif(btnBoitesPiece);
        // TODO : charger la vue Boîtes par pièce
    }

    @FXML
    private void handleMaCollection(ActionEvent event) {
        setActif(btnMaCollection);
        // TODO : charger la vue Ma collection
    }

    @FXML
    private void handlePiecesManquantes(ActionEvent event) {
        setActif(btnPiecesManquantes);
        // TODO : charger la vue Pièces manquantes
    }

    @FXML
    private void handleComposer(ActionEvent event) {
        setActif(btnComposer);
        // TODO : charger la vue Composer une boîte
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/MainMenu.fxml")
            );
            Scene scene = new Scene(loader.load(), 700, 400);
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void chargerVue(String cheminFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(cheminFxml));
            Node vue = loader.load();
            centerPane.getChildren().setAll(vue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
