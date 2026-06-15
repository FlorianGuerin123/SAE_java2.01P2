package fr.univ_orleans.iut45.controleur;

import fr.univ_orleans.iut45.vue.Vue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class AdministrateurControleur {

    @FXML private Button btnAjouterBoite;
    @FXML private Button btnSupprimerBoite;
    @FXML private Button btnMajContenuBoite;
    @FXML private Button btnAjouterPiece;
    @FXML private Button btnSupprimerPiece;
    @FXML private Button btnCreerTheme;
    @FXML private Button btnRetour;

    private Vue vue;

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    @FXML
    private void onNavHover(MouseEvent event) {
        Button btn = (Button) event.getSource();
        if (!btn.getStyle().contains("#FF4D6A")) {
            btn.setStyle(btn.getStyle() + "-fx-background-color: #2A3F58;");
        }
    }

    @FXML
    private void onNavExit(MouseEvent event) {
        Button btn = (Button) event.getSource();
        if (!btn.getStyle().contains("#FF4D6A")) {
            btn.setStyle(btn.getStyle().replace("-fx-background-color: #2A3F58;", "-fx-background-color: transparent;"));
        }
    }

    @FXML
    private void onRetourHover(MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setStyle(btn.getStyle() + "-fx-text-fill: #B0C8E0;");
    }

    @FXML
    private void onRetourExit(MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setStyle(btn.getStyle().replace("-fx-text-fill: #B0C8E0;", "-fx-text-fill: #5A7A9A;"));
    }

    @FXML
    private void handleAjouterBoite(ActionEvent event) {
        System.out.println("Ajouter une boîte cliqué");
        // TODO : charger la vue ajouter une boîte (équivalent partieAdmin.ajouterBoite())
    }

    @FXML
    private void handleSupprimerBoite(ActionEvent event) {
        System.out.println("Supprimer une boîte cliqué");
        // TODO : charger la vue supprimer une boîte (équivalent partieAdmin.supprimerBoite())
    }

    @FXML
    private void handleMajContenuBoite(ActionEvent event) {
        System.out.println("Modifier le contenu d'une boîte cliqué");
        // TODO : charger la vue maj contenu boîte (équivalent partieAdmin.majContenuBoite())
    }

    @FXML
    private void handleAjouterPiece(ActionEvent event) {
        System.out.println("Ajouter une pièce cliqué");
        // TODO : charger la vue ajouter une pièce (équivalent partieAdmin.ajouterPiece())
    }

    @FXML
    private void handleSupprimerPiece(ActionEvent event) {
        System.out.println("Supprimer une pièce cliqué");
        // TODO : charger la vue supprimer une pièce (équivalent partieAdmin.supprimerPiece())
    }

    @FXML
    private void handleCreerTheme(ActionEvent event) {
        System.out.println("Créer un thème cliqué");
        // TODO : charger la vue créer un thème (équivalent partieAdmin.creerTheme())
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        vue.modeAcceuil();
    }
}
