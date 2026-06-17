package fr.univ_orleans.iut45.controleur;

import fr.univ_orleans.iut45.vue.Vue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class CollectionneurControleur {

    @FXML private StackPane centerPane;

    @FXML private Button btnRechercher;
    @FXML private Button btnRechercherNom;
    @FXML private Button btnDetail;
    @FXML private Button btnTheme;
    @FXML private Button btnStatistiques;
    @FXML private Button btnBoitesPiece;
    @FXML private Button btnMaCollection;
    @FXML private Button btnAjouterCollection;
    @FXML private Button btnRetirerCollection;
    @FXML private Button btnPiecesManquantes;
    @FXML private Button btnComposer;
    @FXML private Button btnRetour;

    private Vue vue;

    private static final String STYLE_ACTIF =
        "-fx-background-color: #FF4D6A; -fx-text-fill: white; " +
        "-fx-font-size: 12; -fx-font-weight: bold; " +
        "-fx-alignment: CENTER_LEFT; -fx-padding: 10 16 10 16; " +
        "-fx-background-radius: 0; -fx-cursor: hand; -fx-border-width: 0;";

    private static final String STYLE_INACTIF =
        "-fx-background-color: transparent; -fx-text-fill: #B0C8E0; " +
        "-fx-font-size: 12; -fx-alignment: CENTER_LEFT; " +
        "-fx-padding: 10 16 10 16; -fx-background-radius: 0; " +
        "-fx-cursor: hand; -fx-border-width: 0;";

    private static final String STYLE_HOVER =
        "-fx-background-color: #1E3050; -fx-text-fill: white; " +
        "-fx-font-size: 12; -fx-alignment: CENTER_LEFT; " +
        "-fx-padding: 10 16 10 16; -fx-background-radius: 0; " +
        "-fx-cursor: hand; -fx-border-width: 0;";

    private Button boutonActif = null;

    @FXML
    private void initialize() {
        boutonActif = btnRechercher;
    }

    @FXML
    private void onNavHover(javafx.scene.input.MouseEvent event) {
        Button btn = (Button) event.getSource();
        if (btn != boutonActif) {
            btn.setStyle(STYLE_HOVER);
        }
    }

    @FXML
    private void onNavExit(javafx.scene.input.MouseEvent event) {
        Button btn = (Button) event.getSource();
        if (btn != boutonActif) {
            btn.setStyle(STYLE_INACTIF);
        }
    }

    @FXML
    private void onRetourHover(javafx.scene.input.MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setStyle("-fx-background-color: #1E3050; -fx-text-fill: #B0C8E0; " +
                     "-fx-font-size: 11; -fx-alignment: CENTER_LEFT; " +
                     "-fx-padding: 12 16 12 16; -fx-background-radius: 0; " +
                     "-fx-cursor: hand; -fx-border-width: 0;");
    }

    @FXML
    private void onRetourExit(javafx.scene.input.MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #5A7A9A; " +
                     "-fx-font-size: 11; -fx-alignment: CENTER_LEFT; " +
                     "-fx-padding: 12 16 12 16; -fx-background-radius: 0; " +
                     "-fx-cursor: hand; -fx-border-width: 0;");
    }

    public void setActif(Button btn) {
        if (boutonActif != null) {
            boutonActif.setStyle(STYLE_INACTIF);
        }
        btn.setStyle(STYLE_ACTIF);
        boutonActif = btn;
    }

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    @FXML
    private void handleRechercher(ActionEvent event) {
        setActif(btnRechercher);
        vue.setTitrePage("Espace Collectionneur  |  Rechercher par numéro");
        vue.modeRechercherBoite();
    }

    @FXML
    private void handleRechercherNom(ActionEvent event) {
        setActif(btnRechercherNom);
        vue.setTitrePage("Espace Collectionneur  |  Rechercher par nom");
        vue.modeRechercherBoiteParNom();   
    }

    @FXML
    private void handleStatistiques(ActionEvent event) {
        setActif(btnStatistiques);
        vue.modeStatistiquesBoite(); 
    }

    @FXML
    private void handleDetail(ActionEvent event) {
        setActif(btnDetail);
        vue.setTitrePage("Espace Collectionneur  |  Détail d'une boîte");
        vue.modeDetail();
    }

    @FXML
    private void handleTheme(ActionEvent event) {
        setActif(btnTheme);
        vue.setTitrePage("Espace Collectionneur  |  Boîtes par thème");
        vue.modeRechercherTheme();
    }

    @FXML
    private void handleBoitesPiece(ActionEvent event) {
        setActif(btnBoitesPiece);
        vue.setTitrePage("Espace Collectionneur  |  Boîtes contenant une pièce");
        vue.modeBoitesContenantPiece();
    }

    @FXML
    private void handleMaCollection(ActionEvent event) {
        setActif(btnMaCollection);
        vue.setTitrePage("Espace Collectionneur  |  Ma collection");
        vue.modeCollection();
    }

    @FXML
    private void handleAjouterCollection(ActionEvent event) {
        setActif(btnAjouterCollection);
        vue.setTitrePage("Espace Collectionneur  |  Ajouter à ma collection");
        vue.modeAjoutsimple();
    }

    @FXML
    private void handleRetirerCollection(ActionEvent event) {
        setActif(btnRetirerCollection);
        vue.setTitrePage("Espace Collectionneur  |  Retirer de ma collection");
        vue.modeSupprimerDeCollection();
    }

    @FXML
    private void handlePiecesManquantes(ActionEvent event) {
        setActif(btnPiecesManquantes);
        vue.setTitrePage("Espace Collectionneur  |  Pièces manquantes");
        vue.modePiecesManquantes();   
    }


    @FXML
    private void handleComposer(ActionEvent event) {
        setActif(btnComposer);
        vue.setTitrePage("Espace Collectionneur  |  Composer une boîte");
        vue.modeComposerBoitePerso();
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        vue.modeAcceuil();
    }
}