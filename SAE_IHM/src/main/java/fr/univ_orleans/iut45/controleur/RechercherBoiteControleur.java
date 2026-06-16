package fr.univ_orleans.iut45.controleur;

import fr.univ_orleans.iut45.modele.BoiteBD;
import fr.univ_orleans.iut45.modele.BoiteSimple;
import fr.univ_orleans.iut45.vue.Vue;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class RechercherBoiteControleur {

    
    @FXML private TextField champNumero;
    @FXML private Label     labelMessage;

    @FXML private VBox  carteResultat;
    @FXML private Label labelNumBoite;
    @FXML private Label labelNomBoite;
    @FXML private Label labelStatut;
    @FXML private Label labelAnnee;
    @FXML private Label labelNbPieces;
    @FXML private Label labelTheme;
    @FXML private Button btnVoirDetail;

    
    private Vue vue;

    
    private BoiteSimple boiteTrouvee;

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    
    @FXML
    private void handleRechercher(ActionEvent event) {
        String num = champNumero.getText().trim();

        
        if (num.isEmpty()) {
            afficherErreur("Veuillez saisir un numéro de boîte.");
            cacherCarte();
            return;
        }

        try {
            BoiteBD boiteBD = new BoiteBD(vue.getConnexionMySQL());
            BoiteSimple boite = boiteBD.rechercherBoite(num);

            if (boite == null) {
                afficherErreur("Aucune boîte trouvée avec le numéro « " + num + " ».");
                cacherCarte();
            } else {
                boiteTrouvee = boite;
                labelMessage.setText("");
                afficherCarte(boite);
            }

        } catch (Exception e) {
            afficherErreur("Erreur lors de la recherche : " + e.getMessage());
            cacherCarte();
            e.printStackTrace();
        }
    }

    
    private void afficherCarte(BoiteSimple boite) {
        labelNumBoite.setText("N° " + boite.getNumBoite());
        labelNomBoite.setText(boite.getNomBoite());
        labelAnnee.setText(String.valueOf(boite.getAnnee()));
        labelNbPieces.setText(String.valueOf(boite.getNbPieces()));
        labelTheme.setText(
            boite.getTheme() != null ? boite.getTheme().getNomTheme() : "—"
        );

        if (boite.estComplete()) {
            labelStatut.setText(" Complète");
            labelStatut.setStyle(
                "-fx-background-color: #1A6B3C; -fx-text-fill: #6EFFA8; " +
                "-fx-font-size: 11; -fx-font-weight: bold; " +
                "-fx-background-radius: 4; -fx-padding: 3 10 3 10;"
            );
        } else {
            labelStatut.setText(" Incomplète");
            labelStatut.setStyle(
                "-fx-background-color: #5A1A2A; -fx-text-fill: #FF8099; " +
                "-fx-font-size: 11; -fx-font-weight: bold; " +
                "-fx-background-radius: 4; -fx-padding: 3 10 3 10;"
            );
        }

        carteResultat.setVisible(true);
        carteResultat.setManaged(true);
    }

    private void cacherCarte() {
        carteResultat.setVisible(false);
        carteResultat.setManaged(false);
        boiteTrouvee = null;
    }

    private void afficherErreur(String msg) {
        labelMessage.setText(msg);
    }

    
    @FXML
    private void handleVoirDetail(ActionEvent event) {
        if (boiteTrouvee != null) {
            // TODO : vue.modeDetail(boiteTrouvee.getNumBoite());
            System.out.println("Voir détail de : " + boiteTrouvee.getNumBoite());
        }
    }
}