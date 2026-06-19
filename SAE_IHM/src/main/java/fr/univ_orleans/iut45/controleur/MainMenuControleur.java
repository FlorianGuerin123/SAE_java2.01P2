package fr.univ_orleans.iut45.controleur;

import fr.univ_orleans.iut45.vue.Vue;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MainMenuControleur {

    @FXML private BorderPane racine;
    @FXML private VBox vboxCentrale;
    @FXML private Label labelTitre;
    @FXML private Label labelSousTitre;

    @FXML private Button btnCollectionneur;
    @FXML private Button btnAdministrateur;
    @FXML private Button btnQuitter;
    @FXML private Button btnParNumero;
    @FXML private Button btnDeconnexion;
    
    private Vue vue;
    private String codeSecret = ""; 
 
    public void setVue(Vue vue) {
        this.vue = vue;
    }

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            if (racine != null && racine.getScene() != null) {
                racine.getScene().setOnKeyTyped(e -> {
                    codeSecret += e.getCharacter().toLowerCase();
                    if (codeSecret.length() > 15) {
                        codeSecret = codeSecret.substring(1); 
                    }
                    verifierAberrations();
                });
            }
        });
    }

    private void verifierAberrations() {
        
        if (codeSecret.endsWith("dvd")) {
            final double[] vitesse = {4.0, 4.0};
            
            javafx.animation.AnimationTimer dvdTimer = new javafx.animation.AnimationTimer() {
                @Override
                public void handle(long now) {
                    double maxX = (racine.getWidth() - vboxCentrale.getWidth()) / 2;
                    double maxY = (racine.getHeight() - vboxCentrale.getHeight()) / 2;
                    
                    double tx = vboxCentrale.getTranslateX() + vitesse[0];
                    double ty = vboxCentrale.getTranslateY() + vitesse[1];
                    
                    boolean rebond = false;
                    
                    if (tx >= maxX || tx <= -maxX) {
                        vitesse[0] = -vitesse[0];
                        rebond = true;
                    }
                    if (ty >= maxY || ty <= -maxY) {
                        vitesse[1] = -vitesse[1];
                        rebond = true;
                    }
                    
                    if (rebond) {
                        int r = (int)(Math.random() * 255);
                        int g = (int)(Math.random() * 255);
                        int b = (int)(Math.random() * 255);
                        vboxCentrale.setStyle("-fx-background-color: rgb("+r+","+g+","+b+"); -fx-border-color: black; -fx-border-width: 4; -fx-padding: 30 40 30 40; -fx-background-radius: 6;");
                    }
                    
                    vboxCentrale.setTranslateX(tx);
                    vboxCentrale.setTranslateY(ty);
                }
            };
            dvdTimer.start();
        }
    }


    @FXML
    private void handleDeconnexion(ActionEvent event) {
        this.vue.modeConnexion(); 
    }

    @FXML
    private void handleCollectionneur(ActionEvent event) {
        this.vue.modeCollectionneur();
    }

    @FXML
    private void handleAdministrateur(ActionEvent event) {
        this.vue.modeAdministrateur();
    }

    @FXML
    private void handleQuitter(ActionEvent event) {
        
        try {
            vue.sauvegardeCollection();
        } catch (Exception e) {
            System.out.println("Echec de la sauvegarde");
        }
        Platform.exit();
        
    }

    @FXML
    private void handleParNumero(ActionEvent event) {
        System.out.println("Par numéro cliqué");
    }
}