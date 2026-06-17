package fr.univ_orleans.iut45.controleur;

import fr.univ_orleans.iut45.modele.BoiteComposee;
import fr.univ_orleans.iut45.modele.CollectionPersonnelle;
import fr.univ_orleans.iut45.vue.Vue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class SupprimerDeCollectionControleur {

    @FXML private TextField champRecherche;
    @FXML private VBox menuDeroulant;
    @FXML private Label labelMessage;

    @FXML private VBox carteResultat;
    @FXML private Label labelNumNom;
    @FXML private Label labelAnneePieces;
    @FXML private Label labelStatut;

    private Vue vue;
    private BoiteComposee boiteSelectionnee;

    public void setVue(Vue vue) {
        this.vue = vue;
        // L'initialisation du listener est faite ici car on a besoin de vue
        // pour accéder à la collection
        champRecherche.textProperty().addListener((observable, ancienneValeur, nouvelleValeur) -> {
            labelMessage.setText("");
            cacherCarte();

            if (nouvelleValeur.trim().isEmpty()) {
                cacherMenu();
                return;
            }

            CollectionPersonnelle collection = vue.getCollectionPersonnelle();
            if (collection == null) {
                cacherMenu();
                return;
            }

            String recherche = nouvelleValeur.trim().toLowerCase();

            List<BoiteComposee> resultats = collection.getBoites().stream()
                .filter(b ->
                    b.getNumBoite().toLowerCase().contains(recherche) ||
                    b.getNomBoite().toLowerCase().contains(recherche)
                )
                .collect(Collectors.toList());

            if (resultats.isEmpty()) {
                cacherMenu();
            } else {
                afficherMenu(resultats);
            }
        });
    }

    @FXML
    private void initialize() {
        // Le listener est ajouté dans setVue() car on a besoin de la collection
    }

    private void afficherMenu(List<BoiteComposee> resultats) {
        menuDeroulant.getChildren().clear();

        for (BoiteComposee boite : resultats) {
            Label item = new Label("  " + boite.getNumBoite() + " - " + boite.getNomBoite());
            item.setMaxWidth(Double.MAX_VALUE);
            item.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: white;");
            item.setOnMouseEntered(e -> item.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: #E8F0F8;"));
            item.setOnMouseExited(e  -> item.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: white;"));
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

    private void afficherCarte(BoiteComposee boite) {
        boiteSelectionnee = boite;
        labelNumNom.setText(boite.getNumBoite() + " - " + boite.getNomBoite());
        labelAnneePieces.setText("Année : " + boite.getAnnee() + "  |  Pièces : " + boite.getNbPieces());
        labelStatut.setText("Statut : " + (boite.estComplete() ? "✅ Complète" : "❌ Incomplète"));
        carteResultat.setVisible(true);
        carteResultat.setManaged(true);
    }

    private void cacherCarte() {
        carteResultat.setVisible(false);
        carteResultat.setManaged(false);
        boiteSelectionnee = null;
    }

    @FXML
    private void handleRetirer(ActionEvent event) {
        if (boiteSelectionnee == null) return;

        CollectionPersonnelle collection = vue.getCollectionPersonnelle();
        if (collection == null) {
            labelMessage.setText("Erreur : collection introuvable.");
            labelMessage.setStyle("-fx-text-fill: #FF4D6A; -fx-font-weight: bold;");
            return;
        }

        String numBoite = boiteSelectionnee.getNumBoite();
        collection.retirerBoite(numBoite);
        collection.sauvegarder();

        labelMessage.setText("La boîte " + numBoite + " a été retirée de votre collection !");
        labelMessage.setStyle("-fx-text-fill: #1A6B3C; -fx-font-weight: bold;");

        champRecherche.clear();
        cacherCarte();
    }
}