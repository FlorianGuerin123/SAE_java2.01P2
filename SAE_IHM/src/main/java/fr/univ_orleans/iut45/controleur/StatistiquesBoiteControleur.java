package fr.univ_orleans.iut45.controleur;

import fr.univ_orleans.iut45.modele.BoiteBD;
import fr.univ_orleans.iut45.modele.BoiteSimple;
import fr.univ_orleans.iut45.modele.CouleurBD;
import fr.univ_orleans.iut45.modele.PieceBD;
import fr.univ_orleans.iut45.vue.Vue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class StatistiquesBoiteControleur {

    @FXML private TextField champNumero;
    @FXML private Label labelMessage;
    @FXML private VBox menuDeroulant;

    @FXML private VBox zoneResultats;
    @FXML private Label labelTypesPieces;
    @FXML private Label labelTotalPieces;
    @FXML private PieChart graphiqueCouleurs;

    private Vue vue;

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    @FXML
    private void initialize() {
        champNumero.textProperty().addListener((observable, ancienneValeur, nouvelleValeur) -> {
            labelMessage.setText("");
            cacherResultats();
            
            if (nouvelleValeur.trim().isEmpty()) {
                cacherMenu();
                return;
            }

            try {
                if (vue != null && vue.getConnexionMySQL() != null) {
                    BoiteBD boiteBD = new BoiteBD(vue.getConnexionMySQL());
                    List<BoiteSimple> resultats = boiteBD.rechercherBoitesDynamique(nouvelleValeur.trim());

                    if (resultats.isEmpty()) {
                        cacherMenu();
                    } else {
                        afficherMenu(resultats);
                    }
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
                champNumero.setText(boite.getNumBoite()); 
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
        String num = champNumero.getText().trim();
        labelMessage.setText("");

        if (num.isEmpty()) {
            labelMessage.setText("Veuillez saisir un numéro de boîte.");
            cacherResultats();
            return;
        }

        try {
            PieceBD pieceBD = new PieceBD(vue.getConnexionMySQL());
            CouleurBD couleurBD = new CouleurBD(vue.getConnexionMySQL());

            List<Integer> stats = pieceBD.statsPieces(num);

            if (stats == null || stats.isEmpty() || stats.get(0) == 0) {
                labelMessage.setText("Aucune statistique ou boîte trouvée avec le numéro " + num);
                cacherResultats();
                return;
            }

            labelTypesPieces.setText(String.valueOf(stats.get(0)));
            labelTotalPieces.setText(String.valueOf(stats.get(1)));

            List<CouleurBD.StatCouleur> statsCouleurs = couleurBD.getStatsCouleurs(num);
            ObservableList<PieChart.Data> donneesGraphique = FXCollections.observableArrayList();

            for (CouleurBD.StatCouleur statCoul : statsCouleurs) {
                double pourcentage = ((double) statCoul.quantite() / statCoul.totalPiecesBoite()) * 100;
                if (pourcentage >= 1.0) {
                    donneesGraphique.add(new PieChart.Data(statCoul.nomCouleur() + " (" + Math.round(pourcentage) + "%)", statCoul.quantite()));
                }
            }

            graphiqueCouleurs.setData(donneesGraphique);
            graphiqueCouleurs.setLegendVisible(false);

            zoneResultats.setVisible(true);
            zoneResultats.setManaged(true);
            cacherMenu();

        } catch (SQLException e) {
            labelMessage.setText("Erreur lors de la récupération des données : " + e.getMessage());
            cacherResultats();
        }
    }

    private void cacherResultats() {
        zoneResultats.setVisible(false);
        zoneResultats.setManaged(false);
    }
}