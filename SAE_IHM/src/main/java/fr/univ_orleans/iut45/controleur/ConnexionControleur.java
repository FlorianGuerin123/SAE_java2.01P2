package fr.univ_orleans.iut45.controleur;
 
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Properties;
import java.util.Optional;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.awt.Toolkit;

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
 
    @FXML
    private void initialize() {
        labelMessage.setText("");
        btnConnexion.setDefaultButton(true);
        
        champLogin.setOnKeyReleased(event -> {
            verifierMajuscule();
            verifierEasterEggs();
        });
        champMotDePasse.setOnKeyReleased(event -> {
            verifierMajuscule();
            verifierEasterEggs();
        });
        champBaseDeDonnees.setOnKeyReleased(event -> {
            verifierMajuscule();
            verifierEasterEggs();
        });

        try {
            Properties props = new Properties();
            File file = new File("config.properties");
            if (file.exists()) {
                FileInputStream in = new FileInputStream(file);
                props.load(in);
                in.close();

                champLogin.setText(props.getProperty("login", ""));
                String mdpBase64 = props.getProperty("mdp", "");
                if (!mdpBase64.isEmpty()) {
                    champMotDePasse.setText(new String(java.util.Base64.getDecoder().decode(mdpBase64)));
                }
                champBaseDeDonnees.setText(props.getProperty("bd", ""));
            }
        } catch (Exception e) {
        }
    }

    private void verifierEasterEggs() {
        String bd = champBaseDeDonnees.getText().toLowerCase();

        if (bd.equals("rgb")) {
            if (btnConnexion.getScene() != null && btnConnexion.getScene().getRoot().getEffect() == null) {
                javafx.scene.effect.ColorAdjust colorAdjust = new javafx.scene.effect.ColorAdjust();
                btnConnexion.getScene().getRoot().setEffect(colorAdjust);
                
                javafx.animation.Timeline rgb = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(javafx.util.Duration.millis(50), e -> {
                        double hue = colorAdjust.getHue() + 0.05;
                        if (hue > 1.0) hue = -1.0;
                        colorAdjust.setHue(hue);
                    })
                );
                rgb.setCycleCount(javafx.animation.Animation.INDEFINITE);
                rgb.play();
            }
        } else {
            if (btnConnexion.getScene() != null && btnConnexion.getScene().getRoot().getEffect() != null) {
                btnConnexion.getScene().getRoot().setEffect(null);
            }
        }

        btnConnexion.setText("Se connecter");
        btnConnexion.setStyle("-fx-background-color: #FF4D6A; -fx-text-fill: white; -fx-font-size: 13; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 10 0 10 0;");
        btnConnexion.setOnAction(this::handleConnexion);
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
                
                boolean aChange = true; 
                try {
                    Properties props = new Properties();
                    File file = new File("config.properties");
                    if (file.exists()) {
                        FileInputStream in = new FileInputStream(file);
                        props.load(in);
                        in.close();
                        
                        String mdpSauve = "";
                        String mdpBase64 = props.getProperty("mdp", "");
                        if (!mdpBase64.isEmpty()) {
                            mdpSauve = new String(java.util.Base64.getDecoder().decode(mdpBase64));
                        }

                        if (login.equals(props.getProperty("login", "")) && motDePasse.equals(mdpSauve) && baseDeDonnees.equals(props.getProperty("bd", ""))) {
                            aChange = false;
                        }
                    }
                } catch (Exception e) {
                }

                if (aChange) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Sauvegarde locale");
                    alert.setHeaderText("Connexion réussie !");
                    alert.setContentText("Voulez-vous sauvegarder ces identifiants ?");

                    Optional<ButtonType> resultat = alert.showAndWait();
                    if (resultat.isPresent() && resultat.get() == ButtonType.OK) {
                        try {
                            Properties props = new Properties();
                            props.setProperty("login", login);
                            props.setProperty("mdp", java.util.Base64.getEncoder().encodeToString(motDePasse.getBytes()));
                            props.setProperty("bd", baseDeDonnees);

                            FileOutputStream out = new FileOutputStream("config.properties");
                            props.store(out, null);
                            out.close();
                        } catch (Exception e) {
                        }
                    }
                }
                
                vue.modeAcceuil(); 
            } else {
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
        boolean majActive = Toolkit.getDefaultToolkit().getLockingKeyState(java.awt.event.KeyEvent.VK_CAPS_LOCK);
        
        if (majActive) {
            labelMessage.setText("⚠ Attention : Majuscule activée !");
            labelMessage.setStyle("-fx-text-fill: #FFA500; -fx-font-weight: bold;"); 
        } else if (labelMessage.getText().contains("Majuscule")) {
            labelMessage.setText(""); 
        }
    }

    @FXML
    private void lancerJeuPFC() {
        Stage fenetreJeu = new Stage();
        fenetreJeu.setTitle("Brique - Plaque - Figurine");

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
                labelResultat.setStyle("-fx-text-fill: #555566;"); 
            } else if (
                (choixJoueur.equals("Brique") && choixOrdi.equals("Figurine")) ||
                (choixJoueur.equals("Figurine") && choixOrdi.equals("Plaque")) ||
                (choixJoueur.equals("Plaque") && choixOrdi.equals("Brique"))
            ) {
                labelResultat.setText("Gagné ! " + choixJoueur + " bat " + choixOrdi);
                labelResultat.setStyle("-fx-text-fill: #28a745;"); 
                score[0]++;
            } else {
                labelResultat.setText("Perdu... " + choixOrdi + " bat " + choixJoueur);
                labelResultat.setStyle("-fx-text-fill: #dc3545;"); 
                score[0]--;
            }
            labelScore.setText("Score : " + score[0]);
        };

        btnBrique.setOnAction(actionJeu);
        btnPlaque.setOnAction(actionJeu);
        btnFigurine.setOnAction(actionJeu);

        HBox zoneBoutons = new HBox(10, btnBrique, btnPlaque, btnFigurine);
        zoneBoutons.setAlignment(Pos.CENTER);

        VBox racine = new VBox(15, labelTitre, zoneBoutons, labelResultat, labelScore);
        racine.setAlignment(Pos.CENTER);
        racine.setPadding(new Insets(20));
        racine.setStyle("-fx-background-color: #D8E8F8;");

        fenetreJeu.setScene(new Scene(racine, 350, 200));
        fenetreJeu.show();
    }
}