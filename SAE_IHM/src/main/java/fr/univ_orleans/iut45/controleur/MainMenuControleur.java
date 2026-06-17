package fr.univ_orleans.iut45.controleur;

import fr.univ_orleans.iut45.vue.Vue;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MainMenuControleur {

    @FXML private Button btnCollectionneur;
    @FXML private Button btnAdministrateur;
    @FXML private Button btnQuitter;
    @FXML private Button btnParNumero;
    
    @FXML private Button btnDeconnexion;
    
    private Vue vue;
 
    public void setVue(Vue vue) {
        this.vue = vue;
    }

    @FXML
    private void handleDeconnexion(ActionEvent event) {
        System.out.println("Déconnexion cliquée");

        this.vue.modeConnexion(); 
    }

    @FXML
    private void handleCollectionneur(ActionEvent event) {
        System.out.println("Espace Collectionneur cliqué");
        this.vue.modeCollectionneur();
    }

    @FXML
    private void handleAdministrateur(ActionEvent event) {
        System.out.println("Espace Administrateur cliqué");
        this.vue.modeAdministrateur();
    }

    @FXML
    private void handleQuitter(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void handleParNumero(ActionEvent event) {
        System.out.println("Par numéro cliqué");
        // TODO : charger la vue Par numéro
    }
}