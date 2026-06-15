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
        btnConnexion.setDefaultButton(true);
        champLogin.setOnKeyReleased(event -> verifierMajuscule());
        champMotDePasse.setOnKeyReleased(event -> verifierMajuscule());
        champBaseDeDonnees.setOnKeyReleased(event -> verifierMajuscule());
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

    private void verifierMajuscule() {
        boolean majActive = java.awt.Toolkit.getDefaultToolkit().getLockingKeyState(java.awt.event.KeyEvent.VK_CAPS_LOCK);
        
        if (majActive) {
            labelMessage.setText("⚠ Attention : Majuscule activée !");
            labelMessage.setStyle("-fx-text-fill: #FFA500; -fx-font-weight: bold;"); 
        } else if (labelMessage.getText().contains("Majuscule")) {
            labelMessage.setText(""); 
        }
    }

    @FXML
    private void lancerMiniJeu(ActionEvent event) {
        javafx.stage.Stage fenetreJeu = new javafx.stage.Stage();
        fenetreJeu.setTitle("🎮 Mini-Jeu");

        Label labelTitre = new Label("Brique - Plaque - Figurine !");
        labelTitre.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        
        Label labelResultat = new Label("Fais ton choix pour lancer la partie.");
        Label labelScore = new Label("Score : 0");

        Button btnBrique = new Button("Brique");
        Button btnPlaque = new Button("Plaque");
        Button btnFigurine = new Button("Figurine");

        String[] choixPossibles = {"Brique", "Plaque", "Figurine"};
        final int[] score = {0}; 

        javafx.event.EventHandler<ActionEvent> actionJeu = e -> {
            Button btnClique = (Button) e.getSource();
            String choixJoueur = btnClique.getText();
            
            int indexAleatoire = (int) (Math.random() * 3);
            String choixOrdi = choixPossibles[indexAleatoire];

            if (choixJoueur.equals(choixOrdi)) {
                labelResultat.setText("Égalité ! L'ordi a aussi choisi " + choixOrdi);
                labelResultat.setStyle("-fx-text-fill: #555566;"); // Gris
            } else if (
                (choixJoueur.equals("Brique") && choixOrdi.equals("Figurine")) ||
                (choixJoueur.equals("Figurine") && choixOrdi.equals("Plaque")) ||
                (choixJoueur.equals("Plaque") && choixOrdi.equals("Brique"))
            ) {
                labelResultat.setText("Gagné ! " + choixJoueur + " bat " + choixOrdi);
                labelResultat.setStyle("-fx-text-fill: #28a745;"); // Vert
                score[0]++;
            } else {
                labelResultat.setText("Perdu... " + choixOrdi + " bat " + choixJoueur);
                labelResultat.setStyle("-fx-text-fill: #dc3545;"); // Rouge
                score[0]--;
            }
            labelScore.setText("Score : " + score[0]);
        };

        btnBrique.setOnAction(actionJeu);
        btnPlaque.setOnAction(actionJeu);
        btnFigurine.setOnAction(actionJeu);

        javafx.scene.layout.HBox zoneBoutons = new javafx.scene.layout.HBox(10, btnBrique, btnPlaque, btnFigurine);
        zoneBoutons.setAlignment(javafx.geometry.Pos.CENTER);

        javafx.scene.layout.VBox racine = new javafx.scene.layout.VBox(15, labelTitre, zoneBoutons, labelResultat, labelScore);
        racine.setAlignment(javafx.geometry.Pos.CENTER);
        racine.setPadding(new javafx.geometry.Insets(20));
        racine.setStyle("-fx-background-color: #D8E8F8;");

        javafx.scene.Scene scene = new javafx.scene.Scene(racine, 350, 200);
        fenetreJeu.setScene(scene);
        fenetreJeu.show();
    }
}
 
