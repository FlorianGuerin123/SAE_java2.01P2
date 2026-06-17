package fr.univ_orleans.iut45.controleur;

import fr.univ_orleans.iut45.modele.BoiteBD;
import fr.univ_orleans.iut45.modele.BoiteSimple;
import fr.univ_orleans.iut45.vue.Vue;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class RechercherBoiteParThemeControleur {

    @FXML private TextField champTheme;
    @FXML private Label     labelMessage;
    @FXML private VBox      menuDeroulant;
    @FXML private FlowPane  conteneurCartes;

    private Vue vue;

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    @FXML
    private void initialize() {
        champTheme.textProperty().addListener((observable, ancienneValeur, nouvelleValeur) -> {
            labelMessage.setText("");

            if (nouvelleValeur.trim().isEmpty()) {
                cacherMenu();
                return;
            }

            try {
                if (vue != null && vue.getConnexionMySQL() != null) {
                    BoiteBD boiteBD = new BoiteBD(vue.getConnexionMySQL());
                    List<String> themes = boiteBD.rechercherThemesDynamique(nouvelleValeur.trim());

                    if (themes.isEmpty()) {
                        cacherMenu();
                    } else {
                        afficherMenuThemes(themes);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void afficherMenuThemes(List<String> themes) {
        menuDeroulant.getChildren().clear();

        for (String theme : themes) {
            Label item = new Label("🏷  " + theme);
            item.setMaxWidth(Double.MAX_VALUE);
            item.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: white;");

            item.setOnMouseEntered(e -> item.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: #E8F0F8;"));
            item.setOnMouseExited(e -> item.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: white;"));

            item.setOnMouseClicked(e -> {
                champTheme.setText(theme);
                cacherMenu();
                handleRechercher(null);
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

    @FXML
    private void handleRechercher(ActionEvent event) {
        String theme = champTheme.getText().trim();

        if (theme.isEmpty()) {
            afficherErreur("Veuillez saisir un nom de thème.");
            viderCartes();
            return;
        }

        try {
            BoiteBD boiteBD = new BoiteBD(vue.getConnexionMySQL());
            List<BoiteSimple> boites = boiteBD.getBoitesSelonTheme(theme);

            if (boites.isEmpty()) {
                afficherErreur("Aucune boîte trouvée pour le thème « " + theme + " ».");
                viderCartes();
            } else {
                labelMessage.setText("");
                afficherCartes(boites);
                cacherMenu();
            }

        } catch (Exception e) {
            afficherErreur("Erreur lors de la recherche : " + e.getMessage());
            viderCartes();
            e.printStackTrace();
        }
    }

    private void afficherCartes(List<BoiteSimple> boites) {
        conteneurCartes.getChildren().clear();

        for (BoiteSimple boite : boites) {
            conteneurCartes.getChildren().add(creerCarte(boite));
        }

        conteneurCartes.setVisible(true);
        conteneurCartes.setManaged(true);
    }

    private void viderCartes() {
        conteneurCartes.getChildren().clear();
        conteneurCartes.setVisible(false);
        conteneurCartes.setManaged(false);
    }

    private VBox creerCarte(BoiteSimple boite) {
        VBox carte = new VBox();
        carte.setPrefWidth(480);
        carte.setStyle("-fx-background-color: #F0F4F8; -fx-border-color: #AAAACC; " +
                "-fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6;");

        // En-tête : numéro, nom, statut
        HBox entete = new HBox(12);
        entete.setAlignment(Pos.CENTER_LEFT);
        entete.setStyle("-fx-background-color: #DDE6F0; -fx-background-radius: 6 6 0 0; -fx-padding: 14 18 14 18;");

        Label labelNum = new Label("N° " + boite.getNumBoite());
        labelNum.setStyle("-fx-text-fill: #FF4D6A; -fx-font-size: 13; -fx-font-weight: bold;");

        Label labelNom = new Label(boite.getNomBoite());
        labelNom.setStyle("-fx-text-fill: #1E1E2E; -fx-font-size: 15; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label labelStatut = new Label();
        if (boite.estComplete()) {
            labelStatut.setText(" Complète");
            labelStatut.setStyle("-fx-background-color: #1A6B3C; -fx-text-fill: #6EFFA8; " +
                    "-fx-font-size: 11; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 3 10 3 10;");
        } else {
            labelStatut.setText(" Incomplète");
            labelStatut.setStyle("-fx-background-color: #5A1A2A; -fx-text-fill: #FF8099; " +
                    "-fx-font-size: 11; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 3 10 3 10;");
        }

        entete.getChildren().addAll(labelNum, labelNom, spacer, labelStatut);

        // Corps : année / pièces / thème
        HBox corps = new HBox();
        corps.setStyle("-fx-padding: 18 18 18 18;");

        VBox blocAnnee = creerBlocInfo("Année de sortie", String.valueOf(boite.getAnnee()), "#FF4D6A");

        Separator sep1 = new Separator(Orientation.VERTICAL);
        sep1.setStyle("-fx-background-color: #AAAACC;");

        VBox blocPieces = creerBlocInfo("Nombre de pièces", String.valueOf(boite.getNbPieces()), "#1E1E2E");

        Separator sep2 = new Separator(Orientation.VERTICAL);
        sep2.setStyle("-fx-background-color: #AAAACC;");

        String nomTheme = boite.getTheme() != null ? boite.getTheme().getNomTheme() : "—";
        VBox blocTheme = creerBlocInfo("Thème", nomTheme, "#333344");

        corps.getChildren().addAll(blocAnnee, sep1, blocPieces, sep2, blocTheme);

        Separator sepHoriz = new Separator();
        sepHoriz.setStyle("-fx-background-color: #AAAACC;");

        // Pied : bouton détail
        HBox pied = new HBox();
        pied.setAlignment(Pos.CENTER_RIGHT);
        pied.setStyle("-fx-padding: 12 18 12 18;");

        Button btnDetail = new Button("Voir le détail →");
        btnDetail.setStyle("-fx-background-color: #FF4D6A; -fx-text-fill: white; " +
                "-fx-font-size: 12; -fx-font-weight: bold; -fx-background-radius: 5; " +
                "-fx-cursor: hand; -fx-padding: 8 18 8 18;");
        btnDetail.setOnAction(e -> handleVoirDetail(boite));

        pied.getChildren().add(btnDetail);

        carte.getChildren().addAll(entete, corps, sepHoriz, pied);
        return carte;
    }

    private VBox creerBlocInfo(String titre, String valeur, String couleurValeur) {
        VBox bloc = new VBox(4);
        bloc.setAlignment(Pos.CENTER);
        HBox.setHgrow(bloc, Priority.ALWAYS);

        Label labelTitre = new Label(titre);
        labelTitre.setStyle("-fx-text-fill: #555566; -fx-font-size: 11; -fx-font-weight: bold;");

        Label labelValeur = new Label(valeur);
        labelValeur.setStyle("-fx-text-fill: " + couleurValeur + "; -fx-font-size: 20; -fx-font-weight: bold;");

        bloc.getChildren().addAll(labelTitre, labelValeur);
        return bloc;
    }

    private void afficherErreur(String msg) {
        labelMessage.setText(msg);
    }

    private void handleVoirDetail(BoiteSimple boite) {
        System.out.println("Voir détail de : " + boite.getNumBoite());
    }
}