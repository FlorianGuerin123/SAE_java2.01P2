package fr.univ_orleans.iut45.controleur;

import fr.univ_orleans.iut45.modele.BoiteBD;
import fr.univ_orleans.iut45.modele.BoiteSimple;
import fr.univ_orleans.iut45.vue.Vue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import java.util.List;

public class SupprimerBoiteControleur {

    @FXML private TextField champRecherche;
    @FXML private VBox menuDeroulant;
    @FXML private Label labelMessage;
    
    @FXML private VBox carteResultat;
    @FXML private Label labelNumNom;
    @FXML private Label labelAnneePieces;

    private Vue vue;
    private BoiteSimple boiteSelectionnee;

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    @FXML
    private void initialize() {
        champRecherche.textProperty().addListener((observable, ancienneValeur, nouvelleValeur) -> {
            labelMessage.setText("");
            cacherCarte();
            
            if (nouvelleValeur.trim().isEmpty()) {
                cacherMenu();
                return;
            }

            try {
                BoiteBD boiteBD = new BoiteBD(vue.getConnexionMySQL());
                List<BoiteSimple> resultats = boiteBD.rechercherBoitesDynamique(nouvelleValeur.trim());

                if (resultats.isEmpty()) {
                    cacherMenu();
                } else {
                    afficherMenu(resultats);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void afficherMenu(List<BoiteSimple> resultats) {
        menuDeroulant.getChildren().clear(); 
        
        for (BoiteSimple boite : resultats) {
            Label item = new Label("📦 " + boite.getNumBoite() + " - " + boite.getNomBoite());
            item.setMaxWidth(Double.MAX_VALUE);
            item.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: white;");
            
            item.setOnMouseEntered(e -> item.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: #E8F0F8;"));
            item.setOnMouseExited(e -> item.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: white;"));
            
            item.setOnMouseClicked(e -> {
                champRecherche.setText(boite.getNumBoite()); 
                cacherMenu();
                afficherCarte(boite);
            });
            
            menuDeroulant.getChildren().add(item);
        }
        
        menuDeroulant.setVisible(true);
        menuDeroulant.setManaged(true);
    }

    private void cacherMenu() {
        menuDeroulant.setVisible(false);
        menuDeroulant.setManaged(false);
        menuDeroulant.getChildren().clear();
    }

    private void afficherCarte(BoiteSimple boite) {
        boiteSelectionnee = boite;
        labelNumNom.setText(boite.getNumBoite() + " - " + boite.getNomBoite());
        labelAnneePieces.setText("Année : " + boite.getAnnee() + "  |  Pièces : " + boite.getNbPieces());
        
        carteResultat.setVisible(true);
        carteResultat.setManaged(true);
    }

    private void cacherCarte() {
        carteResultat.setVisible(false);
        carteResultat.setManaged(false);
        boiteSelectionnee = null;
    }

    @FXML
    private void handleSupprimer(ActionEvent event) {
        if (boiteSelectionnee != null) {
            try {
                BoiteBD boiteBD = new BoiteBD(vue.getConnexionMySQL());
                boiteBD.supprimerBoitePartout(boiteSelectionnee.getNumBoite());
                
                labelMessage.setText("La boîte " + boiteSelectionnee.getNumBoite() + " a été supprimée avec succès !");
                labelMessage.setStyle("-fx-text-fill: #1A6B3C; -fx-font-weight: bold;"); 
                
                champRecherche.clear();
                cacherCarte();
                
            } catch (Exception e) {
                labelMessage.setText("Erreur lors de la suppression.");
                labelMessage.setStyle("-fx-text-fill: #FF4D6A; -fx-font-weight: bold;"); 
                e.printStackTrace();
            }
        }
    }
}