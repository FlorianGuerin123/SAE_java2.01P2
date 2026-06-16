package fr.univ_orleans.iut45.controleur;

import fr.univ_orleans.iut45.modele.PieceBD;
import fr.univ_orleans.iut45.vue.Vue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AjouterPieceControleur {

    @FXML private TextField champNum;
    @FXML private TextField champNom;
    @FXML private TextField champNomCat;
    @FXML private Label labelMessage;

    private Vue vue;

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    @FXML
    private void handleAjouter(ActionEvent event) {
        String num = champNum.getText().trim();
        String nom = champNom.getText().trim();
        String nomCat = champNomCat.getText().trim();

        if (num.isBlank() || nom.isBlank() || nomCat.isBlank()) {
            setMessage("Veuillez remplir tous les champs.", false);
            return;
        }
        try {
            PieceBD pieceBD = new PieceBD(vue.getConnexionMySQL());
            pieceBD.ajouterPiece(num, nom, nomCat);
            setMessage(" Pièce \"" + nom + "\" ajoutée avec succès.", true);
            handleReset(null);
        } catch (Exception e) {
            setMessage("Erreur : " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleReset(ActionEvent event) {
        champNum.clear();
        champNom.clear();
        champNomCat.clear();
    }

    private void setMessage(String texte, boolean succes) {
        labelMessage.setText(texte);
        String couleur = succes ? "#2E7D32" : "#FF4D6A";
        labelMessage.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: " + couleur + ";");
    }
}
