package fr.univ_orleans.iut45.controleur;
 
import fr.univ_orleans.iut45.vue.Vue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
 
public class AdministrateurControleur {
 
    @FXML private Button btnAjouterBoite;
    @FXML private Button btnSupprimerBoite;
    @FXML private Button btnMajContenuBoite;
    @FXML private Button btnAjouterPiece;
    @FXML private Button btnSupprimerPiece;
    @FXML private Button btnCreerTheme;
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
        boutonActif = btnAjouterBoite; 
    }
 
    @FXML
    private void onNavHover(MouseEvent event) {
        Button btn = (Button) event.getSource();
        if (btn != boutonActif) {
            btn.setStyle(STYLE_HOVER);
        }
    }
 
    @FXML
    private void onNavExit(MouseEvent event) {
        Button btn = (Button) event.getSource();
        if (btn != boutonActif) {
            btn.setStyle(STYLE_INACTIF);
        }
    }
 
    @FXML
    private void onRetourHover(MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setStyle("-fx-background-color: #1E3050; -fx-text-fill: #B0C8E0; " +
                     "-fx-font-size: 11; -fx-alignment: CENTER_LEFT; " +
                     "-fx-padding: 12 16 12 16; -fx-background-radius: 0; " +
                     "-fx-cursor: hand; -fx-border-width: 0;");
    }
 
    @FXML
    private void onRetourExit(MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #5A7A9A; " +
                     "-fx-font-size: 11; -fx-alignment: CENTER_LEFT; " +
                     "-fx-padding: 12 16 12 16; -fx-background-radius: 0; " +
                     "-fx-cursor: hand; -fx-border-width: 0;");
    }

    private void setActif(Button btn) {
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
    private void handleAjouterBoite(ActionEvent event) {
        setActif(btnAjouterBoite);
        vue.modeAjouterBoite();
    }
 
    @FXML
    private void handleSupprimerBoite(ActionEvent event) {
        System.out.println("Supprimer une boîte cliqué");
        vue.setTitrePage("Espace Administrateur  |  Supprimer une boîte");
        setActif(btnSupprimerBoite);
        vue.modeSupprimerBoite(); 
    }
 
    @FXML
    private void handleMajContenuBoite(ActionEvent event) {
        System.out.println("Modifier le contenu d'une boîte cliqué");
        vue.setTitrePage("Espace Administrateur  |  Modifier le contenu d'une boîte");
        // TODO : charger la vue maj contenu boîte (équivalent partieAdmin.majContenuBoite())
        setActif(btnMajContenuBoite);
        // TODO : charger la vue maj contenu boîte
    }
 
    @FXML
    private void handleAjouterPiece(ActionEvent event) {
        setActif(btnAjouterPiece);
        vue.modeAjouterPiece();

    }
 
    @FXML
    private void handleSupprimerPiece(ActionEvent event) {
        System.out.println("Supprimer une pièce cliqué");
        vue.setTitrePage("Espace Administrateur  |  Supprimer une pièce");
        // TODO : charger la vue supprimer une pièce (équivalent partieAdmin.supprimerPiece())
        setActif(btnSupprimerPiece);
        // TODO : charger la vue supprimer une pièce
    }
 
    @FXML
    private void handleCreerTheme(ActionEvent event) {
        setActif(btnCreerTheme);
        vue.modeCreerTheme();
    }
 
    @FXML
    private void handleRetour(ActionEvent event) {
        vue.modeAcceuil();
    }

    
}
