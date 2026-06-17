package fr.univ_orleans.iut45.controleur;
 
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.prefs.Preferences;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;
import javafx.scene.control.*;
import java.sql.SQLException;
import javafx.scene.control.Alert.AlertType.*;
import fr.univ_orleans.iut45.modele.ConnexionMySQL;
import fr.univ_orleans.iut45.vue.Vue;
import java.awt.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.Animation;
import javafx.util.Duration;
import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.input.KeyCode;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.awt.Toolkit;
import java.awt.event.KeyEvent;
 
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
            java.io.File file = new java.io.File("config.properties");
            if (file.exists()) {
                java.io.FileInputStream in = new java.io.FileInputStream(file);
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
        String login = champLogin.getText().toLowerCase();
        String mdp = champMotDePasse.getText().toLowerCase();
        String bd = champBaseDeDonnees.getText().toLowerCase();

        boolean modifie = false;

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
        }

        if (login.equals("isagi")) {
            btnConnexion.setText("Tirer le penalty !");
            btnConnexion.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 10 0 10 0;");
            btnConnexion.setOnAction(e -> lancerPenaltyIsagi());
            modifie = true;
        }

        if (mdp.equals("esquive")) {
            btnConnexion.setOnMouseEntered(e -> {
                btnConnexion.setTranslateX((Math.random() - 0.5) * 400);
                btnConnexion.setTranslateY((Math.random() - 0.5) * 400);
            });
            modifie = true;
        } else {
            btnConnexion.setOnMouseEntered(null);
            btnConnexion.setTranslateX(0);
            btnConnexion.setTranslateY(0);
        }

        if (login.equals("nerfthis")) {
            btnConnexion.setStyle("-fx-background-color: #ff4be6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 10 0 10 0;");
            btnConnexion.setOnAction(e -> {
                Stage stage = (Stage) btnConnexion.getScene().getWindow();
                javafx.animation.Timeline shake = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(javafx.util.Duration.millis(50), ev -> stage.setX(stage.getX() + 20)),
                    new javafx.animation.KeyFrame(javafx.util.Duration.millis(100), ev -> stage.setX(stage.getX() - 40)),
                    new javafx.animation.KeyFrame(javafx.util.Duration.millis(150), ev -> stage.setX(stage.getX() + 20))
                );
                shake.setCycleCount(15);
                shake.setOnFinished(ev -> javafx.application.Platform.exit());
                shake.play();
            });
            modifie = true;
        }

        if (mdp.equals("chute")) {
            if (btnConnexion.getScene() != null && btnConnexion.getScene().getRoot().getUserData() == null) {
                btnConnexion.getScene().getRoot().setUserData("actif");
                
                java.util.List<javafx.scene.Node> pieces = new java.util.ArrayList<>();
                recupPieces(btnConnexion.getScene().getRoot(), pieces);
                
                java.util.Map<javafx.scene.Node, double[]> physique = new java.util.HashMap<>();
                for (javafx.scene.Node n : pieces) {
                    physique.put(n, new double[]{
                        (Math.random() - 0.5) * 15,
                        -(Math.random() * 15 + 5),
                        (Math.random() - 0.5) * 20
                    });
                }
                
                javafx.animation.AnimationTimer chute = new javafx.animation.AnimationTimer() {
                    @Override
                    public void handle(long now) {
                        for (javafx.scene.Node n : pieces) {
                            double[] p = physique.get(n);
                            p[1] += 0.8;
                            n.setTranslateX(n.getTranslateX() + p[0]);
                            n.setTranslateY(n.getTranslateY() + p[1]);
                            n.setRotate(n.getRotate() + p[2]);
                        }
                    }
                };
                chute.start();
            }
            modifie = true;
        }

        if (!modifie) {
            btnConnexion.setText("Se connecter");
            btnConnexion.setStyle("-fx-background-color: #FF4D6A; -fx-text-fill: white; -fx-font-size: 13; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 10 0 10 0;");
            btnConnexion.setOnAction(this::handleConnexion);
        }
    }

    private void recupPieces(javafx.scene.Node noeud, java.util.List<javafx.scene.Node> liste) {
        if (noeud instanceof javafx.scene.control.Control || noeud instanceof javafx.scene.shape.Shape) {
            liste.add(noeud);
        } else if (noeud instanceof javafx.scene.layout.Pane) {
            for (javafx.scene.Node enfant : ((javafx.scene.layout.Pane) noeud).getChildren()) {
                recupPieces(enfant, liste);
            }
        }
    }

    private void lancerPenaltyIsagi() {
        Stage fenetrePenalty = new Stage();
        fenetrePenalty.setTitle("Blue Lock - Penalty");

        Pane zoneJeu = new Pane();
        zoneJeu.setPrefSize(600, 400);
        zoneJeu.setStyle("-fx-background-color: #2e7d32;");

        javafx.scene.shape.Rectangle cage = new javafx.scene.shape.Rectangle(400, 200, Color.TRANSPARENT);
        cage.setStroke(Color.WHITE);
        cage.setStrokeWidth(8);
        cage.setX(100);
        cage.setY(50);

        javafx.scene.shape.Rectangle gardien = new javafx.scene.shape.Rectangle(40, 80, Color.web("#1565c0"));
        gardien.setX(280);
        gardien.setY(170);

        Label ballon = new Label("⚽");
        ballon.setStyle("-fx-font-size: 40;");
        ballon.setLayoutX(270);
        ballon.setLayoutY(320);

        Label message = new Label("Choisis ta zone de tir !");
        message.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white;");
        message.setLayoutX(165);
        message.setLayoutY(10);

        zoneJeu.getChildren().addAll(cage, gardien, ballon, message);

        double[][] cibles = {
            {150, 80}, {280, 80}, {410, 80},
            {150, 180}, {280, 180}, {410, 180}
        };

        List<Button> boutonsZone = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            Button btnZone = new Button();
            btnZone.setPrefSize(130, 95);
            btnZone.setLayoutX(105 + (i % 3) * 132);
            btnZone.setLayoutY(55 + (i / 3) * 98);
            btnZone.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
            
            final int index = i;
            btnZone.setOnAction(e -> {
                for (Button b : boutonsZone) {
                    b.setDisable(true);
                }
                
                message.setText("TIR !");
                
                int choixGardien = (int) (Math.random() * 6);
                
                javafx.animation.TranslateTransition tir = new javafx.animation.TranslateTransition(javafx.util.Duration.seconds(0.4), ballon);
                tir.setToX(cibles[index][0] - 270);
                tir.setToY(cibles[index][1] - 320);
                
                javafx.animation.TranslateTransition plongeon = new javafx.animation.TranslateTransition(javafx.util.Duration.seconds(0.4), gardien);
                plongeon.setToX(cibles[choixGardien][0] - 280);
                plongeon.setToY(cibles[choixGardien][1] - 170);
                
                tir.setOnFinished(ev -> {
                    if (index == choixGardien) {
                        message.setText("ARRÊT DU GARDIEN !");
                        message.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #FF4D6A;");
                    } else {
                        message.setText("BUUUUUUT ! ÉGOÏSTE !");
                        message.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: gold;");
                    }
                });
                
                plongeon.play();
                tir.play();
            });
            boutonsZone.add(btnZone);
            zoneJeu.getChildren().add(btnZone);
        }

        Button btnRejouer = new Button("Rejouer");
        btnRejouer.setLayoutX(260);
        btnRejouer.setLayoutY(360);
        btnRejouer.setOnAction(e -> {
            fenetrePenalty.close();
            lancerPenaltyIsagi();
        });
        zoneJeu.getChildren().add(btnRejouer);

        Scene scene = new Scene(zoneJeu);
        fenetrePenalty.setScene(scene);
        fenetrePenalty.show();
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
                    Alert alert = new Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
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
    private void lancerMiniJeu(ActionEvent event) {
        Stage fenetreMenu = new Stage();
        fenetreMenu.setTitle("Arcade Briqu'IUTO");

        Label titre = new Label("Choisis ton mini-jeu !");
        titre.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");

        Button btnPFC = new Button("1. Brique - Plaque - Figurine");
        Button btnMorpion = new Button("2. Morpion (Tic-Tac-Toe)");
        Button btnJusteNombre = new Button("3. Le Juste Nombre de pièces");
        Button btnBriqueDoree = new Button("4. Trouver la Brique Dorée");
        Button btnGeoDash = new Button("5. Geo Dash (Infini)");       


        String styleBouton = "-fx-background-color: #FF4D6A; " + 
                             "-fx-text-fill: white; " +           
                             "-fx-font-weight: bold; " +         
                             "-fx-font-size: 13; " +              
                             "-fx-background-radius: 5; " +       
                             "-fx-cursor: hand; " +               
                             "-fx-padding: 10 0 10 0;";           

        Button[] lesBoutons = {btnPFC, btnMorpion, btnJusteNombre, btnBriqueDoree,btnGeoDash};
        for (Button btn : lesBoutons) {
            btn.setStyle(styleBouton);
            btn.setPrefWidth(220);
        }

        btnPFC.setOnAction(e -> lancerJeuPFC());
        btnMorpion.setOnAction(e -> lancerMorpion());
        btnJusteNombre.setOnAction(e -> lancerJusteNombre());
        btnBriqueDoree.setOnAction(e -> lancerBriqueDoree());
        btnGeoDash.setOnAction(e -> lancerGeometryDash());


        VBox racine = new VBox(15, titre, btnPFC, btnMorpion, btnJusteNombre, btnBriqueDoree,btnGeoDash);
        racine.setAlignment(Pos.CENTER);
        racine.setPadding(new Insets(30));
        racine.setStyle("-fx-background-color: #1E1E2E;"); 

        Scene scene = new Scene(racine, 320, 350);
        fenetreMenu.setScene(scene);
        fenetreMenu.show();
    }

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

    private void lancerMorpion() {
        Stage fenetreMorpion = new Stage();
        fenetreMorpion.setTitle("Morpion LEGO vs Bot");

        VBox racine = new VBox(15);
        racine.setAlignment(Pos.CENTER);
        racine.setPadding(new Insets(20));

        Label labelStatut = new Label("À toi de jouer (X) !");
        labelStatut.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        GridPane grille = new GridPane();
        grille.setAlignment(Pos.CENTER);
        grille.setHgap(5); 
        grille.setVgap(5); 

        Button[][] cases = new Button[3][3];
        final boolean[] jeuFini = {false}; 

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button btn = new Button("");
                btn.setMinSize(80, 80); 
                btn.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
                cases[i][j] = btn; 

                btn.setOnAction(e -> {
                    if (!jeuFini[0] && btn.getText().equals("")) {
                        
                        btn.setText("X");
                        btn.setStyle("-fx-text-fill: #ff4b69; -fx-font-size: 24; -fx-font-weight: bold;");
                        
                        if (verifierVictoireMorpion(cases, "X")) {
                            labelStatut.setText("🎉 GAGNÉ ! Tu as battu le bot !");
                            labelStatut.setStyle("-fx-text-fill: #28a745; -fx-font-size: 16; -fx-font-weight: bold;");
                            jeuFini[0] = true;
                        } 
                        else if (grillePleineMorpion(cases)) {
                            labelStatut.setText("Égalité ! Plus de place.");
                            labelStatut.setStyle("-fx-text-fill: #555566; -fx-font-size: 16; -fx-font-weight: bold;");
                            jeuFini[0] = true;
                        } 
                        else {
                            faireJouerBotMorpion(cases);
                            if (verifierVictoireMorpion(cases, "O")) {
                                labelStatut.setText("💀 PERDU ! Le bot a gagné.");
                                labelStatut.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 16; -fx-font-weight: bold;");
                                jeuFini[0] = true;
                            }
                        }
                    }
                });
                grille.add(btn, j, i); 
            }
        }

        Button btnRejouer = new Button("Rejouer");
        btnRejouer.setOnAction(e -> {
            fenetreMorpion.close();
            lancerMorpion();
        });

        racine.getChildren().addAll(labelStatut, grille, btnRejouer);
        fenetreMorpion.setScene(new Scene(racine, 300, 400));
        fenetreMorpion.show();
    }

    private void faireJouerBotMorpion(Button[][] cases) {
        while (true) {
            int ligne = (int) (Math.random() * 3);
            int colonne = (int) (Math.random() * 3);
            
            if (cases[ligne][colonne].getText().equals("")) {
                cases[ligne][colonne].setText("O");
                cases[ligne][colonne].setStyle("-fx-text-fill: #28a745; -fx-font-size: 24; -fx-font-weight: bold;");
                break; 
            }
        }
    }
    private boolean grillePleineMorpion(Button[][] cases) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (cases[i][j].getText().equals("")) {
                    return false; 
                }
            }
        }
        return true; 
    }

    private boolean verifierVictoireMorpion(Button[][] cases, String joueur) {
        for (int i = 0; i < 3; i++) {
            if (cases[i][0].getText().equals(joueur) && cases[i][1].getText().equals(joueur) && cases[i][2].getText().equals(joueur)) return true;
            if (cases[0][i].getText().equals(joueur) && cases[1][i].getText().equals(joueur) && cases[2][i].getText().equals(joueur)) return true;
        }
        if (cases[0][0].getText().equals(joueur) && cases[1][1].getText().equals(joueur) && cases[2][2].getText().equals(joueur)) return true;
        if (cases[0][2].getText().equals(joueur) && cases[1][1].getText().equals(joueur) && cases[2][0].getText().equals(joueur)) return true;
        
        return false;
    }

    private void lancerJusteNombre() {
        Stage fenetreJN = new Stage();
        fenetreJN.setTitle("Le Juste Nombre");

        int nombreMystere = (int)(Math.random() * 1000) + 1;
        final int[] nbEssais = {0};

        Label consigne = new Label("Devine le nombre de pièces (entre 1 et 1000) :");
        TextField champSaisie = new TextField();
        champSaisie.setMaxWidth(100);
        Button btnValider = new Button("Valider");
        Label reponse = new Label("...");
        reponse.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

        btnValider.setOnAction(e -> {
            try {
                int essaiJoueur = Integer.parseInt(champSaisie.getText());
                nbEssais[0]++;
                
                if (essaiJoueur == nombreMystere) {
                    reponse.setText("🎉 GAGNÉ en " + nbEssais[0] + " essais !");
                    reponse.setStyle("-fx-text-fill: #28a745; -fx-font-size: 14; -fx-font-weight: bold;");
                    btnValider.setDisable(true); // On bloque le bouton une fois gagné
                } else if (essaiJoueur < nombreMystere) {
                    reponse.setText("C'est PLUS ! ⬆️");
                    reponse.setStyle("-fx-text-fill: #ff4b69; -fx-font-size: 14; -fx-font-weight: bold;");
                } else {
                    reponse.setText("C'est MOINS ! ⬇️");
                    reponse.setStyle("-fx-text-fill: #ff4b69; -fx-font-size: 14; -fx-font-weight: bold;");
                }
                champSaisie.clear();
            } catch (NumberFormatException ex) {
                reponse.setText("Veuillez entrer un nombre valide.");
            }
        });

        btnValider.setDefaultButton(true);

        VBox racine = new VBox(15, consigne, champSaisie, btnValider, reponse);
        racine.setAlignment(Pos.CENTER);
        racine.setPadding(new Insets(20));

        fenetreJN.setScene(new Scene(racine, 350, 200));
        fenetreJN.show();
    }

    private void lancerBriqueDoree() {
        Stage fenetreBrique = new Stage();
        fenetreBrique.setTitle("La Brique Dorée");

        Label consigne = new Label("Trouve la boîte qui cache la Brique Dorée !");
        consigne.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        
        HBox zoneBoites = new HBox(15);
        zoneBoites.setAlignment(Pos.CENTER);
        
        int boiteGagnante = (int)(Math.random() * 3);
        
        for (int i = 0; i < 3; i++) {
            Button btnBoite = new Button("📦 Boîte " + (i + 1));
            btnBoite.setMinSize(80, 80);
            
            final int indexBoite = i; 
            
            btnBoite.setOnAction(e -> {
                if (indexBoite == boiteGagnante) {
                    consigne.setText("🏆 BINGO ! Tu as trouvé la Brique Dorée !");
                    consigne.setStyle("-fx-text-fill: #28a745; -fx-font-size: 14; -fx-font-weight: bold;");
                    btnBoite.setStyle("-fx-background-color: gold; -fx-font-weight: bold;");
                    btnBoite.setText("🧱 Dorée");
                } else {
                    consigne.setText("❌ Dommage, elle est vide...");
                    consigne.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 14; -fx-font-weight: bold;");
                    btnBoite.setDisable(true);
                }
            });
            zoneBoites.getChildren().add(btnBoite);
        }

        Button btnRejouer = new Button("Rejouer");
        btnRejouer.setOnAction(e -> {
            fenetreBrique.close();
            lancerBriqueDoree(); 
        });

        VBox racine = new VBox(20, consigne, zoneBoites, btnRejouer);
        racine.setAlignment(Pos.CENTER);
        racine.setPadding(new Insets(20));

        fenetreBrique.setScene(new Scene(racine, 350, 250));
        fenetreBrique.show();
    }

    @FXML
    private void lancerJeuDeLaVie(ActionEvent event) {
        JeuDeLaVieInfini jeu = new JeuDeLaVieInfini();
        jeu.lancer();
    }

    @FXML
    private void lancerDino(ActionEvent event) {
        Stage fenetreDino = new Stage();
        fenetreDino.setTitle("Dino Run !");

        Pane zoneJeu = new Pane();
        zoneJeu.setPrefSize(600, 300);
        zoneJeu.setStyle("-fx-background-color: #1E1E2E;"); 


        javafx.scene.shape.Line sol = new javafx.scene.shape.Line(0, 250, 600, 250);
        sol.setStroke(Color.WHITE);
        sol.setStrokeWidth(3);

        Rectangle dino = new Rectangle(40, 40, Color.web("#ff4b69"));
        dino.setX(50);
        dino.setY(210); 

        Rectangle cactus = new Rectangle(20, 50, Color.web("#28a745"));
        cactus.setX(600); 
        cactus.setY(200); 

        Label labelScore = new Label("Score: 0");
        labelScore.setStyle("-fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;");
        labelScore.setLayoutX(20);
        labelScore.setLayoutY(20);

        Label labelGameOver = new Label("GAME OVER\nAppuie sur Haut pour rejouer");
        labelGameOver.setStyle("-fx-text-fill: #FF4D6A; -fx-font-size: 30; -fx-font-weight: bold; -fx-text-alignment: center;");
        labelGameOver.setLayoutX(130);
        labelGameOver.setLayoutY(100);
        labelGameOver.setVisible(false); 

        zoneJeu.getChildren().addAll(sol, dino, cactus, labelScore, labelGameOver);
        Scene scene = new Scene(zoneJeu);

        final double[] velociteY = {0}; 
        final double gravite = 0.6;
        final double forceSaut = -12;
        final boolean[] enSaut = {false};
        
        final double[] vitesseCactus = {6};
        final int[] score = {0};
        final boolean[] jeuFini = {false};

        scene.setOnKeyPressed(e -> {
            if ((e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.UP)) {
                if (jeuFini[0]) {
                    jeuFini[0] = false;
                    labelGameOver.setVisible(false);
                    cactus.setX(600);
                    score[0] = 0;
                    vitesseCactus[0] = 6;
                } else if (!enSaut[0]) {
                    velociteY[0] = forceSaut;
                    enSaut[0] = true;
                }
            }
        });

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (jeuFini[0]) return; 

                velociteY[0] += gravite; 
                dino.setY(dino.getY() + velociteY[0]);

                if (dino.getY() >= 210) {
                    dino.setY(210);
                    enSaut[0] = false;
                    velociteY[0] = 0;
                }

                cactus.setX(cactus.getX() - vitesseCactus[0]);

                if (cactus.getX() < -20) {
                    cactus.setX(600 + Math.random() * 200); 
                    score[0]++;
                    labelScore.setText("Score: " + score[0]);
                    
                    if (score[0] % 5 == 0) {
                        vitesseCactus[0] += 1;
                    }
                }

                if (dino.getBoundsInParent().intersects(cactus.getBoundsInParent())) {
                    jeuFini[0] = true;
                    labelGameOver.setVisible(true);
                }
            }
        };

        timer.start(); 

        fenetreDino.setScene(scene);
        fenetreDino.show();

        fenetreDino.setOnCloseRequest(e -> timer.stop());
    }
    @FXML
    private void lancerGeometryDash() {
        Stage fenetreGeo = new Stage();
        fenetreGeo.setTitle("Geo Dash LEGO");

        Pane zoneJeu = new Pane();
        zoneJeu.setPrefSize(800, 400);
        zoneJeu.setStyle("-fx-background-color: #2b2b2b;");

        javafx.scene.shape.Line sol = new javafx.scene.shape.Line(0, 300, 800, 300);
        sol.setStroke(Color.WHITE);
        sol.setStrokeWidth(4);

        Rectangle joueur = new Rectangle(30, 30, Color.web("#00ffcc"));
        joueur.setX(100);
        joueur.setY(270);

        java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(ConnexionControleur.class);
        final int[] recordGeo = {prefs.getInt("record_geodash", 0)};
        final int[] score = {0};

        Label labelScore = new Label("Score: 0 | Record: " + recordGeo[0]);
        labelScore.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");
        labelScore.setLayoutX(20);
        labelScore.setLayoutY(20);

        Label labelGameOver = new Label("CRASH !\nAppuie sur Haut pour recommencer");
        labelGameOver.setStyle("-fx-text-fill: #FF4D6A; -fx-font-size: 30; -fx-font-weight: bold; -fx-text-alignment: center;");
        labelGameOver.setLayoutX(200);
        labelGameOver.setLayoutY(120);
        labelGameOver.setVisible(false);

        zoneJeu.getChildren().addAll(sol, joueur, labelScore, labelGameOver);
        Scene scene = new Scene(zoneJeu);

        final double[] velociteY = {0}; 
        final double gravite = 0.8;
        final double forceSaut = -13.5;
        final boolean[] enSaut = {false};
        final boolean[] jeuFini = {false};
        
        final double[] vitesseJeu = {7.0}; 
        
        List<Shape> obstacles = new ArrayList<>();
        final double[] distanceProchainPattern = {800}; 

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.SPACE || e.getCode() == javafx.scene.input.KeyCode.UP) {
                if (jeuFini[0]) {
                    jeuFini[0] = false;
                    labelGameOver.setVisible(false);
                    score[0] = 0;
                    vitesseJeu[0] = 7.0; 
                    joueur.setY(270);
                    joueur.setRotate(0);
                    distanceProchainPattern[0] = 800;
                    
                    zoneJeu.getChildren().removeAll(obstacles);
                    obstacles.clear();
                } else if (!enSaut[0]) {
                    velociteY[0] = forceSaut;
                    enSaut[0] = true;
                }
            }
        });

        javafx.animation.AnimationTimer timer = new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                if (jeuFini[0]) return;

                velociteY[0] += gravite;
                joueur.setY(joueur.getY() + velociteY[0]);

                if (enSaut[0]) {
                    joueur.setRotate(joueur.getRotate() + 6);
                }

                if (joueur.getY() >= 270) {
                    joueur.setY(270);
                    velociteY[0] = 0;
                    enSaut[0] = false;
                    double rotation = joueur.getRotate() % 90;
                    if (rotation != 0) {
                        joueur.setRotate(Math.round(joueur.getRotate() / 90.0) * 90);
                    }
                }

                if (score[0] > 0 && score[0] % 500 == 0) {
                    vitesseJeu[0] += 0.3; 
                }

                distanceProchainPattern[0] -= vitesseJeu[0];
                if (distanceProchainPattern[0] <= 0) {
                    double longueurPattern = genererPatternAleatoire(zoneJeu, obstacles);
                    distanceProchainPattern[0] = longueurPattern + (300 + Math.random() * 200) * (vitesseJeu[0] / 7.0); 
                }

                List<Shape> obstaclesASupprimer = new ArrayList<>();
                for (Shape obs : obstacles) {
                    obs.setLayoutX(obs.getLayoutX() - vitesseJeu[0]); 

                    if (obs.getLayoutX() < -200) {
                        obstaclesASupprimer.add(obs);
                    }

                    if (joueur.getBoundsInParent().intersects(obs.getBoundsInParent())) {
                        jeuFini[0] = true;
                        labelGameOver.setVisible(true);
                        
                        if (score[0] > recordGeo[0]) {
                            recordGeo[0] = score[0];
                            prefs.putInt("record_geodash", recordGeo[0]);
                        }
                    }
                }

                zoneJeu.getChildren().removeAll(obstaclesASupprimer);
                obstacles.removeAll(obstaclesASupprimer);

                score[0]++;
                labelScore.setText("Score: " + (score[0] / 10) + " | Record: " + (recordGeo[0] / 10));
            }
        };

        timer.start();
        fenetreGeo.setScene(scene);
        fenetreGeo.show();
        fenetreGeo.setOnCloseRequest(e -> timer.stop());
    }

    private double genererPatternAleatoire(Pane zoneJeu, List<Shape> obstacles) {
        int choix = (int) (Math.random() * 10); 
        double startX = 850; 
        double longueur = 0;

        switch (choix) {
            case 0: 
                creerPic(startX, 300, 1.0, zoneJeu, obstacles);
                longueur = 30;
                break;
            case 1: 
                creerPic(startX, 300, 1.0, zoneJeu, obstacles);
                creerPic(startX + 30, 300, 1.0, zoneJeu, obstacles);
                longueur = 60;
                break;
            case 2: 
                creerPic(startX, 300, 1.0, zoneJeu, obstacles);
                creerPic(startX + 30, 300, 1.0, zoneJeu, obstacles);
                creerPic(startX + 60, 300, 1.0, zoneJeu, obstacles);
                longueur = 90;
                break;
            case 3: 
                creerPic(startX, 300, 1.0, zoneJeu, obstacles);
                creerPic(startX + 140, 300, 1.0, zoneJeu, obstacles);
                longueur = 170;
                break;
            case 4: 
                creerMur(startX, 270, 140, 30, zoneJeu, obstacles);
                longueur = 140;
                break;
            case 5: 
                creerMur(startX, 270, 60, 30, zoneJeu, obstacles);
                creerMur(startX + 180, 270, 60, 30, zoneJeu, obstacles);
                longueur = 240;
                break;
            case 6: 
                creerMur(startX, 100, 150, 100, zoneJeu, obstacles);
                longueur = 150;
                break;
            case 7: 
                creerMur(startX, 100, 100, 100, zoneJeu, obstacles);
                creerPic(startX + 150, 300, 1.0, zoneJeu, obstacles);
                longueur = 180;
                break;
            case 8: 
                creerPic(startX, 300, 1.0, zoneJeu, obstacles);
                creerPic(startX + 180, 300, 1.0, zoneJeu, obstacles);
                creerPic(startX + 360, 300, 1.0, zoneJeu, obstacles);
                creerMur(startX + 540, 270, 120, 30, zoneJeu, obstacles);
                longueur = 660;
                break;
            case 9: 
                creerMur(startX, 270, 80, 30, zoneJeu, obstacles);
                creerMur(startX + 200, 100, 100, 100, zoneJeu, obstacles);
                creerMur(startX + 400, 270, 80, 30, zoneJeu, obstacles);
                longueur = 480;
                break;
        }
        return longueur;
    }

    private void creerPic(double x, double y, double echelle, Pane zoneJeu, List<Shape> obstacles) {
        javafx.scene.shape.Polygon pic = new javafx.scene.shape.Polygon();
        pic.getPoints().addAll(new Double[]{
            0.0, 0.0,
            15.0 * echelle, -30.0 * echelle,
            30.0 * echelle, 0.0
        });
        pic.setFill(Color.web("#ff4b69")); 
        pic.setLayoutX(x);
        pic.setLayoutY(y); 
        
        zoneJeu.getChildren().add(pic);
        obstacles.add(pic);
    }

    private void creerMur(double x, double y, double largeur, double hauteur, Pane zoneJeu, List<Shape> obstacles) {
        Rectangle mur = new Rectangle(largeur, hauteur, Color.web("#ff4b69"));
        mur.setLayoutX(x);
        mur.setLayoutY(y);
        
        zoneJeu.getChildren().add(mur);
        obstacles.add(mur);
    }

    @FXML
    private void lancerBenchmark(ActionEvent event) {
        Stage fenetreBench = new Stage();
        fenetreBench.setTitle("Human Benchmark");

        VBox racine = new VBox(15);
        racine.setPrefSize(400, 550);
        racine.setStyle("-fx-background-color: #2b2b2b; -fx-alignment: center;");

        Label titre = new Label("HUMAN BENCHMARK");
        titre.setStyle("-fx-text-fill: white; -fx-font-size: 24; -fx-font-weight: bold;");

        String styleBtn = "-fx-background-color: #2b87d1; -fx-text-fill: white; -fx-font-size: 16; -fx-pref-width: 200; -fx-cursor: hand;";
        
        Button btnReaction = new Button("Reaction Time");
        Button btnAim = new Button("Aim Trainer");
        Button btnChimp = new Button("Chimp Test");
        Button btnNumber = new Button("Number Memory");
        Button btnSequence = new Button("Sequence Memory");
        Button btnDropper = new Button("Dropper 2D");

        btnReaction.setStyle(styleBtn);
        btnAim.setStyle(styleBtn);
        btnChimp.setStyle(styleBtn);
        btnNumber.setStyle(styleBtn);
        btnSequence.setStyle(styleBtn);
        btnDropper.setStyle(styleBtn);

        btnReaction.setOnAction(e -> jouerReactionTime());
        btnAim.setOnAction(e -> jouerAimTrainer());
        btnChimp.setOnAction(e -> jouerChimpTest());
        btnNumber.setOnAction(e -> jouerNumberMemory());
        btnSequence.setOnAction(e -> jouerSequenceMemory());
        btnDropper.setOnAction(e -> lancerDropper());

        racine.getChildren().addAll(titre, btnReaction, btnAim, btnChimp, btnNumber, btnSequence, btnDropper);
        fenetreBench.setScene(new Scene(racine));
        fenetreBench.show();
    }

    private void jouerReactionTime() {
        Stage stage = new Stage();
        stage.setTitle("Reaction Time");
        StackPane racine = new StackPane();
        racine.setPrefSize(600, 400);

        Label texte = new Label("Clique pour commencer");
        texte.setStyle("-fx-text-fill: white; -fx-font-size: 30; -fx-font-weight: bold; -fx-text-alignment: center;");
        racine.getChildren().add(texte);

        final int[] etat = {0};
        final long[] tempsDebut = {0};
        javafx.animation.Timeline[] timeline = {null};

        racine.setStyle("-fx-background-color: #2b87d1;");

        racine.setOnMousePressed(e -> {
            if (etat[0] == 0) {
                etat[0] = 1;
                racine.setStyle("-fx-background-color: #ce2636;");
                texte.setText("Attends le vert...");

                timeline[0] = new javafx.animation.Timeline(new javafx.animation.KeyFrame(
                    javafx.util.Duration.millis(1000 + Math.random() * 3000),
                    ev -> {
                        etat[0] = 2;
                        racine.setStyle("-fx-background-color: #4bdb6a;");
                        texte.setText("CLIQUE !");
                        tempsDebut[0] = System.currentTimeMillis();
                    }
                ));
                timeline[0].play();
            } else if (etat[0] == 1) {
                if (timeline[0] != null) timeline[0].stop();
                racine.setStyle("-fx-background-color: #2b87d1;");
                texte.setText("Trop tôt !\nClique pour réessayer.");
                etat[0] = 0;
            } else if (etat[0] == 2) {
                long temps = System.currentTimeMillis() - tempsDebut[0];
                racine.setStyle("-fx-background-color: #2b87d1;");
                texte.setText(temps + " ms\nClique pour rejouer");
                etat[0] = 0;
            }
        });

        stage.setScene(new Scene(racine));
        stage.show();
    }

    private void jouerAimTrainer() {
        Stage stage = new Stage();
        stage.setTitle("Aim Trainer");
        Pane racine = new Pane();
        racine.setPrefSize(800, 600);
        racine.setStyle("-fx-background-color: #2b2b2b;");

        final int[] ciblesRestantes = {30};
        final long[] tempsDebut = {System.currentTimeMillis()};

        javafx.scene.shape.Circle cible = new javafx.scene.shape.Circle(25, javafx.scene.paint.Color.web("#4bdb6a"));
        cible.setCenterX(400);
        cible.setCenterY(300);

        Label score = new Label("Restant: 30");
        score.setStyle("-fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;");
        score.setLayoutX(10);
        score.setLayoutY(10);

        racine.getChildren().addAll(score, cible);

        cible.setOnMousePressed(e -> {
            ciblesRestantes[0]--;
            if (ciblesRestantes[0] > 0) {
                score.setText("Restant: " + ciblesRestantes[0]);
                cible.setCenterX(50 + Math.random() * 700);
                cible.setCenterY(50 + Math.random() * 500);
            } else {
                long tempsTotal = System.currentTimeMillis() - tempsDebut[0];
                double moyenne = (double) tempsTotal / 30;
                racine.getChildren().clear();
                Label fin = new Label("Cible moyenne : " + Math.round(moyenne) + " ms");
                fin.setStyle("-fx-text-fill: white; -fx-font-size: 35; -fx-font-weight: bold;");
                fin.setLayoutX(200);
                fin.setLayoutY(270);
                racine.getChildren().add(fin);
            }
        });

        stage.setScene(new Scene(racine));
        stage.show();
    }

    private void jouerChimpTest() {
        Stage stage = new Stage();
        stage.setTitle("Chimp Test");
        Pane racine = new Pane();
        racine.setPrefSize(800, 600);
        racine.setStyle("-fx-background-color: #2b87d1;");

        final int[] nbCases = {4};
        final int[] numActuel = {1};
        final boolean[] cache = {false};

        genererGrilleChimp(racine, nbCases, numActuel, cache, stage);

        stage.setScene(new Scene(racine));
        stage.show();
    }

    private void genererGrilleChimp(Pane racine, int[] nbCases, int[] numActuel, boolean[] cache, Stage stage) {
        racine.getChildren().clear();
        numActuel[0] = 1;
        cache[0] = false;

        java.util.List<Integer> positions = new java.util.ArrayList<>();
        for (int i = 0; i < 40; i++) positions.add(i);
        java.util.Collections.shuffle(positions);

        for (int i = 1; i <= nbCases[0]; i++) {
            Button btn = new Button(String.valueOf(i));
            btn.setPrefSize(60, 60);
            btn.setStyle("-fx-background-color: white; -fx-font-size: 20; -fx-font-weight: bold; -fx-border-color: #2b87d1; -fx-border-width: 2;");
            
            int pos = positions.get(i - 1);
            btn.setLayoutX(100 + (pos % 8) * 70);
            btn.setLayoutY(50 + (pos / 8) * 70);

            final int val = i;
            btn.setOnAction(e -> {
                if (val == numActuel[0]) {
                    btn.setVisible(false);
                    numActuel[0]++;
                    if (val == 1) {
                        cache[0] = true;
                        for (javafx.scene.Node n : racine.getChildren()) {
                            if (n instanceof Button && n.isVisible()) {
                                ((Button) n).setText("");
                            }
                        }
                    }
                    if (val == nbCases[0]) {
                        nbCases[0]++;
                        genererGrilleChimp(racine, nbCases, numActuel, cache, stage);
                    }
                } else {
                    racine.getChildren().clear();
                    Label fin = new Label("Score : " + (nbCases[0] - 1));
                    fin.setStyle("-fx-text-fill: white; -fx-font-size: 40; -fx-font-weight: bold;");
                    fin.setLayoutX(330);
                    fin.setLayoutY(250);
                    racine.getChildren().add(fin);
                }
            });
            racine.getChildren().add(btn);
        }
    }

    private void jouerNumberMemory() {
        Stage stage = new Stage();
        stage.setTitle("Number Memory");
        VBox racine = new VBox(20);
        racine.setPrefSize(600, 400);
        racine.setStyle("-fx-background-color: #2b87d1; -fx-alignment: center;");

        final int[] niveau = {1};
        final String[] nombreActuel = {""};
        
        Label affichage = new Label();
        affichage.setStyle("-fx-text-fill: white; -fx-font-size: 50; -fx-font-weight: bold;");
        
        TextField input = new TextField();
        input.setMaxWidth(300);
        input.setStyle("-fx-font-size: 20;");
        input.setVisible(false);

        Button btnSubmit = new Button("Valider");
        btnSubmit.setStyle("-fx-font-size: 20; -fx-background-color: #1e6096; -fx-text-fill: white;");
        btnSubmit.setVisible(false);

        javafx.scene.control.ProgressBar barre = new javafx.scene.control.ProgressBar(1.0);
        barre.setPrefWidth(300);
        
        racine.getChildren().addAll(affichage, barre, input, btnSubmit);

        Runnable lancerNiveau = new Runnable() {
            @Override
            public void run() {
                input.setVisible(false);
                btnSubmit.setVisible(false);
                input.clear();
                barre.setVisible(true);
                
                StringBuilder sb = new StringBuilder();
                for(int i=0; i<niveau[0]; i++) sb.append((int)(Math.random() * 10));
                nombreActuel[0] = sb.toString();
                affichage.setText(nombreActuel[0]);

                javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(javafx.util.Duration.ZERO, new javafx.animation.KeyValue(barre.progressProperty(), 1)),
                    new javafx.animation.KeyFrame(javafx.util.Duration.seconds(2 + niveau[0]*0.5), new javafx.animation.KeyValue(barre.progressProperty(), 0))
                );
                
                timeline.setOnFinished(e -> {
                    affichage.setText("Quel était le nombre ?");
                    barre.setVisible(false);
                    input.setVisible(true);
                    btnSubmit.setVisible(true);
                    input.requestFocus();
                });
                timeline.play();
            }
        };

        btnSubmit.setOnAction(e -> {
            if (input.getText().equals(nombreActuel[0])) {
                niveau[0]++;
                lancerNiveau.run();
            } else {
                affichage.setText("Perdu ! C'était " + nombreActuel[0] + "\nNiveau : " + niveau[0]);
                input.setVisible(false);
                btnSubmit.setVisible(false);
            }
        });

        lancerNiveau.run();
        stage.setScene(new Scene(racine));
        stage.show();
    }

    private void jouerSequenceMemory() {
        Stage stage = new Stage();
        stage.setTitle("Sequence Memory");
        javafx.scene.layout.GridPane racine = new javafx.scene.layout.GridPane();
        racine.setPrefSize(500, 500);
        racine.setStyle("-fx-background-color: #2b2b2b; -fx-alignment: center; -fx-hgap: 10; -fx-vgap: 10;");

        java.util.List<Button> boutons = new java.util.ArrayList<>();
        java.util.List<Integer> sequence = new java.util.ArrayList<>();
        final int[] indexJoueur = {0};
        final int[] niveau = {1};

        for (int i = 0; i < 9; i++) {
            Button btn = new Button();
            btn.setPrefSize(100, 100);
            btn.setStyle("-fx-background-color: #2b87d1; -fx-background-radius: 10;");
            final int id = i;
            btn.setOnAction(e -> {
                if (sequence.isEmpty()) return;
                
                btn.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
                javafx.animation.PauseTransition pt = new javafx.animation.PauseTransition(javafx.util.Duration.millis(200));
                pt.setOnFinished(ev -> btn.setStyle("-fx-background-color: #2b87d1; -fx-background-radius: 10;"));
                pt.play();

                if (id == sequence.get(indexJoueur[0])) {
                    indexJoueur[0]++;
                    if (indexJoueur[0] == sequence.size()) {
                        niveau[0]++;
                        indexJoueur[0] = 0;
                        sequence.add((int)(Math.random() * 9));
                        jouerSequenceAnimation(boutons, sequence);
                    }
                } else {
                    racine.getChildren().clear();
                    Label fin = new Label("Score : " + (niveau[0] - 1));
                    fin.setStyle("-fx-text-fill: white; -fx-font-size: 40; -fx-font-weight: bold;");
                    racine.add(fin, 0, 0);
                }
            });
            boutons.add(btn);
            racine.add(btn, i % 3, i / 3);
        }

        sequence.add((int)(Math.random() * 9));
        
        Scene scene = new Scene(racine);
        stage.setScene(scene);
        stage.show();
        
        javafx.animation.PauseTransition initPause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
        initPause.setOnFinished(e -> jouerSequenceAnimation(boutons, sequence));
        initPause.play();
    }

    private void jouerSequenceAnimation(java.util.List<Button> boutons, java.util.List<Integer> sequence) {
        javafx.animation.SequentialTransition st = new javafx.animation.SequentialTransition();
        for (int id : sequence) {
            javafx.animation.PauseTransition p1 = new javafx.animation.PauseTransition(javafx.util.Duration.millis(300));
            p1.setOnFinished(e -> boutons.get(id).setStyle("-fx-background-color: white; -fx-background-radius: 10;"));
            javafx.animation.PauseTransition p2 = new javafx.animation.PauseTransition(javafx.util.Duration.millis(300));
            p2.setOnFinished(e -> boutons.get(id).setStyle("-fx-background-color: #2b87d1; -fx-background-radius: 10;"));
            st.getChildren().addAll(p1, p2);
        }
        st.play();
    }

    @FXML
    private void lancerDropper() {
        Stage fenetre = new Stage();
        fenetre.setTitle("Dropper 2D");
        
        Pane zoneJeu = new Pane();
        zoneJeu.setPrefSize(400, 600);
        zoneJeu.setStyle("-fx-background-color: #1a1a2e;");

        Rectangle joueur = new Rectangle(20, 20, javafx.scene.paint.Color.web("#e94560"));
        joueur.setX(190);
        joueur.setY(100);

        Label scoreLabel = new Label("Score: 0");
        scoreLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20; -fx-font-weight: bold;");
        scoreLabel.setLayoutX(10);
        scoreLabel.setLayoutY(10);

        zoneJeu.getChildren().addAll(joueur, scoreLabel);
        Scene scene = new Scene(zoneJeu);

        final double[] vitesseX = {0};
        final double[] vitesseChute = {5.0};
        final int[] score = {0};
        final boolean[] jeuFini = {false};
        
        java.util.List<Rectangle> obstacles = new java.util.ArrayList<>();
        final double[] distanceProchainObstacle = {0};

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.LEFT || e.getCode() == javafx.scene.input.KeyCode.Q) {
                vitesseX[0] = -6;
            }
            if (e.getCode() == javafx.scene.input.KeyCode.RIGHT || e.getCode() == javafx.scene.input.KeyCode.D) {
                vitesseX[0] = 6;
            }
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.LEFT || e.getCode() == javafx.scene.input.KeyCode.Q ||
                e.getCode() == javafx.scene.input.KeyCode.RIGHT || e.getCode() == javafx.scene.input.KeyCode.D) {
                vitesseX[0] = 0;
            }
        });

        javafx.animation.AnimationTimer timer = new javafx.animation.AnimationTimer() {
            @Override
            public void handle(long now) {
                if (jeuFini[0]) return;

                joueur.setX(joueur.getX() + vitesseX[0]);

                if (joueur.getX() < 0) joueur.setX(0);
                if (joueur.getX() > 380) joueur.setX(380);

                distanceProchainObstacle[0] -= vitesseChute[0];
                if (distanceProchainObstacle[0] <= 0) {
                    genererLigneDropper(zoneJeu, obstacles);
                    distanceProchainObstacle[0] = 180;
                    score[0]++;
                    scoreLabel.setText("Score: " + score[0]);
                    
                    if (score[0] % 10 == 0) {
                        vitesseChute[0] += 0.5;
                    }
                }

                java.util.List<Rectangle> aSupprimer = new java.util.ArrayList<>();
                for (Rectangle obs : obstacles) {
                    obs.setY(obs.getY() - vitesseChute[0]);

                    if (obs.getY() < -50) {
                        aSupprimer.add(obs);
                    }

                    if (joueur.getBoundsInParent().intersects(obs.getBoundsInParent())) {
                        jeuFini[0] = true;
                        Label gameOver = new Label("SPLAT !\nScore: " + score[0]);
                        gameOver.setStyle("-fx-text-fill: #e94560; -fx-font-size: 40; -fx-font-weight: bold; -fx-text-alignment: center;");
                        gameOver.setLayoutX(110);
                        gameOver.setLayoutY(250);
                        zoneJeu.getChildren().add(gameOver);
                    }
                }

                zoneJeu.getChildren().removeAll(aSupprimer);
                obstacles.removeAll(aSupprimer);
            }
        };

        timer.start();
        fenetre.setScene(scene);
        fenetre.show();
        fenetre.setOnCloseRequest(e -> timer.stop());
    }

    private void genererLigneDropper(Pane zoneJeu, java.util.List<Rectangle> obstacles) {
        int trouX = (int)(Math.random() * 320); 
        int largeurTrou = 80;

        if (trouX > 0) {
            Rectangle murGauche = new Rectangle(trouX, 30, javafx.scene.paint.Color.web("#0f3460"));
            murGauche.setX(0);
            murGauche.setY(600);
            zoneJeu.getChildren().add(murGauche);
            obstacles.add(murGauche);
        }

        if (trouX + largeurTrou < 400) {
            Rectangle murDroite = new Rectangle(400 - (trouX + largeurTrou), 30, javafx.scene.paint.Color.web("#0f3460"));
            murDroite.setX(trouX + largeurTrou);
            murDroite.setY(600);
            zoneJeu.getChildren().add(murDroite);
            obstacles.add(murDroite);
        }
    }
}