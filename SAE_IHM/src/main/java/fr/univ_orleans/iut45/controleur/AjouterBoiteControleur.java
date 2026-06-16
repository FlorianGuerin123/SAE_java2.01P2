package fr.univ_orleans.iut45.controleur;

import fr.univ_orleans.iut45.modele.BoiteBD;
import fr.univ_orleans.iut45.vue.Vue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class AjouterBoiteControleur {

    @FXML private TextField champNum;
    @FXML private TextField champNom;
    @FXML private TextField champAnnee;
    @FXML private TextField champNbPieces;
    @FXML private TextField champIdTheme;
    @FXML private Label labelMessage;

    private Vue vue;

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    @FXML
    private void handleAjouter(ActionEvent event) {
        String num = champNum.getText().trim();
        String nom = champNom.getText().trim();
        String anneeStr = champAnnee.getText().trim();
        String nbPStr = champNbPieces.getText().trim();
        String idThStr = champIdTheme.getText().trim();

        if (num.isBlank() || nom.isBlank() || anneeStr.isBlank() || nbPStr.isBlank() || idThStr.isBlank()) {
            setMessage("Veuillez remplir tous les champs.", false);
            return;
        }
        int annee, nbPieces, idTheme;
        try {
            annee = Integer.parseInt(anneeStr);
            nbPieces = Integer.parseInt(nbPStr);
            idTheme = Integer.parseInt(idThStr);
        } catch (NumberFormatException e) {
            setMessage("L'année, le nombre de pièces et l'ID thème doivent être des nombres entiers.", false);
            return;
        }
        try {
            BoiteBD boiteBD = new BoiteBD(vue.getConnexionMySQL());
            boiteBD.ajouterBoite(num, nom, annee, nbPieces, idTheme);
            setMessage(" Boîte \"" + nom + "\" ajoutée avec succès.", true);
            handleReset(null);
        } catch (Exception e) {
            setMessage("Erreur : " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleReset(ActionEvent event) {
        champNum.clear();
        champNom.clear();
        champAnnee.clear();
        champNbPieces.clear();
        champIdTheme.clear();
    }

    private void setMessage(String texte, boolean succes) {
        labelMessage.setText(texte);
        String couleur = succes ? "#2E7D32" : "#FF4D6A";
        labelMessage.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: " + couleur + ";");
    }
}
