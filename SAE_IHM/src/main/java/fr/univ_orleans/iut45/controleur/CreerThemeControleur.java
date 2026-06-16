package fr.univ_orleans.iut45.controleur;

import fr.univ_orleans.iut45.modele.ThemeBD;
import fr.univ_orleans.iut45.vue.Vue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CreerThemeControleur {

    @FXML private TextField champIdTheme;
    @FXML private TextField champNomTheme;
    @FXML private TextField champThemeParent;
    @FXML private Label labelMessage;

    private Vue vue;

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    @FXML
    private void handleCreer(ActionEvent event) {
        String idStr      = champIdTheme.getText().trim();
        String nomTheme   = champNomTheme.getText().trim();
        String parent     = champThemeParent.getText().trim();

        if (idStr.isBlank() || nomTheme.isBlank()) {
            setMessage("Veuillez remplir les champs obligatoires (ID et nom).", false);
            return;
        }

        int idTheme;
        try {
            idTheme = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            setMessage("L'ID du thème doit être un nombre entier.", false);
            return;
        }

        try {
            ThemeBD themeBD = new ThemeBD(vue.getConnexionMySQL());
            // themeParent vide → null (pas de parent)
            themeBD.ajouterTheme(idTheme, nomTheme, parent.isEmpty() ? null : parent);
            setMessage("✔  Thème \"" + nomTheme + "\" créé avec succès.", true);
            handleReset(null);
        } catch (Exception e) {
            setMessage("Erreur : " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleReset(ActionEvent event) {
        champIdTheme.clear();
        champNomTheme.clear();
        champThemeParent.clear();
    }

    private void setMessage(String texte, boolean succes) {
        labelMessage.setText(texte);
        String couleur = succes ? "#2E7D32" : "#FF4D6A";
        labelMessage.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: " + couleur + ";");
    }
}
