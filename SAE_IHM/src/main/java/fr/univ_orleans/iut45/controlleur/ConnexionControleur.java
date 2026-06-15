package fr.univ_orleans.iut45.controleur;
 
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
 
import java.sql.SQLException;
 
import fr.univ_orleans.iut45.modele.ConnexionMySQL;
import fr.univ_orleans.iut45.vue.Vue;
 
public class ConnexionControleur {
 
    @FXML private TextField champLogin;
    @FXML private PasswordField champMotDePasse;
    @FXML private TextField champBaseDeDonnees;
    @FXML private Label labelMessage;
    @FXML private Button btnConnexion;
    @FXML private Button btnQuitter;
 
    private static final String SERVEUR = "servinfo-maria";
    private Vue vue;
 
    /**
     * Méthode appelée automatiquement après le chargement du FXML.
     */
    @FXML
    private void initialize() {
        labelMessage.setText("");
    }

    public void setVue(Vue vue) {
        this.vue = vue;
    }
 
    @FXML
    private void handleConnexion(ActionEvent event) {
        String login = champLogin.getText();
        String motDePasse = champMotDePasse.getText();
        String baseDeDonnees = champBaseDeDonnees.getText();
        labelMessage.setStyle("-fx-text-fill: #FF4D6A; -fx-font-size: 10; -fx-font-weight: bold;");
        if (login.isBlank() || motDePasse.isBlank() || baseDeDonnees.isBlank()) {
            labelMessage.setText("Veuillez remplir tous les champs.");
            return;
        }
        ConnexionMySQL connexion = vue.getConnexionMySQL();
        try {
            connexion.connecter(SERVEUR, baseDeDonnees, login, motDePasse);
            if (connexion.isConnecte()) {
                vue.modeAcceuil();
            } 
            else {
                labelMessage.setText("Connexion échouée.");
            }
 
        } catch (SQLException e) {
            labelMessage.setText("Connexion échouée : identifiants ou base de données incorrects.");
        }
    }
 
    @FXML
    private void handleQuitter(ActionEvent event) {
        Platform.exit();
    }
}
 
