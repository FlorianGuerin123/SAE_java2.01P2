package fr.univ_orleans.iut45.controleur;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import fr.univ_orleans.iut45.modele.ConnexionMySQL;

public class ConnexionControleur {

    @FXML private TextField champLogin;
    @FXML private PasswordField champMotDePasse;
    @FXML private TextField champBaseDeDonnees;
    @FXML private Label labelMessage;
    @FXML private Button btnConnexion;
    @FXML private Button btnQuitter;
    private ConnexionMySQL connexion;

    /**
     * Méthode appelée automatiquement après le chargement du FXML.
     */
    @FXML
    private void initialize() {
        labelMessage.setText("");
    }

    @FXML
    private void handleConnexion(ActionEvent event) {
        String login = champLogin.getText();
        String motDePasse = champMotDePasse.getText();
        String baseDeDonnees = champBaseDeDonnees.getText();
    }

    @FXML
    private void handleQuitter(ActionEvent event) {
        Platform.exit();
    }
}
