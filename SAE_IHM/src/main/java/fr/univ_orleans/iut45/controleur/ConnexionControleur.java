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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;
 
import java.sql.SQLException;
 
import fr.univ_orleans.iut45.modele.ConnexionMySQL;
import fr.univ_orleans.iut45.vue.Vue;

import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.Animation;
import javafx.util.Duration;
import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import javafx.scene.input.KeyCode;

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
            java.util.Properties props = new java.util.Properties();
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
                    java.util.Properties props = new java.util.Properties();
                    java.io.File file = new java.io.File("config.properties");
                    if (file.exists()) {
                        java.io.FileInputStream in = new java.io.FileInputStream(file);
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
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Sauvegarde locale");
                    alert.setHeaderText("Connexion réussie !");
                    alert.setContentText("Voulez-vous sauvegarder ces identifiants ?");

                    java.util.Optional<javafx.scene.control.ButtonType> resultat = alert.showAndWait();
                    if (resultat.isPresent() && resultat.get() == javafx.scene.control.ButtonType.OK) {
                        try {
                            java.util.Properties props = new java.util.Properties();
                            props.setProperty("login", login);
                            props.setProperty("mdp", java.util.Base64.getEncoder().encodeToString(motDePasse.getBytes()));
                            props.setProperty("bd", baseDeDonnees);

                            java.io.FileOutputStream out = new java.io.FileOutputStream("config.properties");
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
        Stage fenetreJV = new Stage();
        fenetreJV.setTitle("Le Jeu de la Vie");

        int tailleGrille = 25; // Grille de 25x25
        boolean[][] etat = new boolean[tailleGrille][tailleGrille];
        javafx.scene.shape.Rectangle[][] rectangles = new javafx.scene.shape.Rectangle[tailleGrille][tailleGrille];

        GridPane grille = new GridPane();
        grille.setHgap(1); 
        grille.setVgap(1);
        grille.setAlignment(Pos.CENTER);

        // Création de la grille cliquable
        for (int i = 0; i < tailleGrille; i++) {
            for (int j = 0; j < tailleGrille; j++) {
                javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(15, 15, javafx.scene.paint.Color.web("#24294a"));
                rect.setStroke(javafx.scene.paint.Color.BLACK);
                
                final int x = i;
                final int y = j;
                
                // Quand on clique, la case s'allume ou s'éteint
                rect.setOnMouseClicked(e -> {
                    etat[x][y] = !etat[x][y];
                    rect.setFill(etat[x][y] ? javafx.scene.paint.Color.web("#ff4b69") : javafx.scene.paint.Color.web("#24294a"));
                });
                
                rectangles[i][j] = rect;
                grille.add(rect, j, i);
            }
        }

        // L'animation qui va appeler la méthode de calcul toutes les 200 millisecondes
        javafx.animation.Timeline animation = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.millis(200), e -> etapeJeuDeLaVie(etat, rectangles, tailleGrille))
        );
        animation.setCycleCount(javafx.animation.Animation.INDEFINITE); // Répéter à l'infini

        Button btnStartPause = new Button("▶ Démarrer / ⏸ Pause");
        btnStartPause.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        
        btnStartPause.setOnAction(e -> {
            if (animation.getStatus() == javafx.animation.Animation.Status.RUNNING) {
                animation.pause();
                btnStartPause.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
            } else {
                animation.play();
                btnStartPause.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold;");
            }
        });

        Label consigne = new Label("Dessine une forme puis clique sur Démarrer !");
        consigne.setStyle("-fx-text-fill: white; -fx-font-size: 14;");

        VBox racine = new VBox(15, consigne, btnStartPause, grille);
        racine.setAlignment(Pos.CENTER);
        racine.setPadding(new Insets(20));
        racine.setStyle("-fx-background-color: #1E1E2E;");

        fenetreJV.setScene(new Scene(racine, 450, 550));
        fenetreJV.show();
    }

    /**
     * Calcule la génération suivante selon les règles du Jeu de la Vie.
     */
    private void etapeJeuDeLaVie(boolean[][] etat, javafx.scene.shape.Rectangle[][] rectangles, int taille) {
        boolean[][] nouvelEtat = new boolean[taille][taille];
        
        // On parcourt chaque case pour compter ses voisins
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                int voisins = compterVoisins(etat, i, j, taille);
                
                if (etat[i][j]) {
                    // Une cellule survit si elle a 2 ou 3 voisins
                    nouvelEtat[i][j] = (voisins == 2 || voisins == 3);
                } else {
                    // Une cellule naît si elle a exactement 3 voisins
                    nouvelEtat[i][j] = (voisins == 3);
                }
            }
        }
        
        // On met à jour l'affichage et l'état principal
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                etat[i][j] = nouvelEtat[i][j];
                rectangles[i][j].setFill(etat[i][j] ? javafx.scene.paint.Color.web("#ff4b69") : javafx.scene.paint.Color.web("#24294a"));
            }
        }
    }

    /**
     * Compte le nombre de cellules vivantes autour d'une case.
     */
    private int compterVoisins(boolean[][] etat, int x, int y, int taille) {
        int nb = 0;
        // On regarde les 8 cases autour (de -1 à +1 en x et en y)
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                
                int nx = x + i;
                int ny = y + j;
                
                if (nx >= 0 && nx < taille && ny >= 0 && ny < taille) {
                    if (etat[nx][ny]) nb++;
                }
            }
        }
        return nb;
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
}