package fr.univ_orleans.iut45.controleur;

import fr.univ_orleans.iut45.modele.BoiteBD;
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
    @FXML private VBox zoneResultats;
    @FXML private Label labelTypesPieces;
    @FXML private Label labelTotalPieces;
    @FXML private PieChart graphiqueCouleurs;

    private Vue vue;

    public void setVue(Vue vue) {
        this.vue = vue;
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