package fr.univ_orleans.iut45.controleur;

import fr.univ_orleans.iut45.vue.Vue;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.Cursor;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;

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

    // --- LA BOÎTE À FOLIES ---
    private void verifierAberrations() {
        
        // 1. LE GLITCH VISUEL ABSOLU (Tape "glitch")
        if (codeSecret.endsWith("glitch")) {
            labelTitre.setText("E R R O R  4 0 4");
            labelTitre.setStyle("-fx-font-size: 40; -fx-text-fill: black; -fx-font-weight: bold;");
            
            racine.setScaleX(-1);
            racine.setScaleY(-1);

            javafx.animation.AnimationTimer glitchTimer = new javafx.animation.AnimationTimer() {
                String[] couleurs = {"#FF0000", "#00FF00", "#0000FF", "#FFFF00", "#FF00FF", "#00FFFF"};
                @Override
                public void handle(long now) {
                    String couleurActuelle = couleurs[(int)(Math.random() * couleurs.length)];
                    racine.setStyle("-fx-background-color: " + couleurActuelle + ";");
                    
                    btnCollectionneur.setTranslateX((Math.random() - 0.5) * 40);
                    btnCollectionneur.setTranslateY((Math.random() - 0.5) * 40);
                    btnCollectionneur.setScaleX(0.5 + Math.random() * 1.5);
                    btnCollectionneur.setScaleY(0.5 + Math.random() * 1.5);
                    
                    btnAdministrateur.setTranslateX((Math.random() - 0.5) * 60);
                    btnAdministrateur.setTranslateY((Math.random() - 0.5) * 60);
                }
            };
            glitchTimer.start();
        }
        
        // 2. LE DVD ABSOLU (Tape "dvd")
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

        // 3. LE PANNEAU DE STYLE DES CURSEURS FOU (Tape "style")
        if (codeSecret.endsWith("style")) {
            
            VBox panneauStyle = new VBox(20);
            panneauStyle.setAlignment(Pos.CENTER);
            panneauStyle.setMaxSize(550, 400);
            panneauStyle.setStyle("-fx-background-color: #2b2b36; -fx-padding: 30; -fx-background-radius: 15; -fx-border-color: #FF4D6A; -fx-border-width: 3; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 20, 0, 0, 10);");
            
            Label titre = new Label("✨ STYLE DE LA SOURIS ✨");
            titre.setStyle("-fx-text-fill: white; -fx-font-size: 26; -fx-font-weight: bold;");
            
            // --- Curseurs de base ---
            Label lblBase = new Label("Curseurs natifs improbables :");
            lblBase.setStyle("-fx-text-fill: #AAAACC; -fx-font-size: 16;");
            
            FlowPane boxBase = new FlowPane(10, 10);
            boxBase.setAlignment(Pos.CENTER);
            ajouterBoutonCurseur(boxBase, "Défaut", Cursor.DEFAULT);
            ajouterBoutonCurseur(boxBase, "Viseur (Sniper)", Cursor.CROSSHAIR);
            ajouterBoutonCurseur(boxBase, "Invisible (Hardcore)", Cursor.NONE);
            ajouterBoutonCurseur(boxBase, "Agrippe", Cursor.CLOSED_HAND);
            ajouterBoutonCurseur(boxBase, "Sablier Infini", Cursor.WAIT);
            ajouterBoutonCurseur(boxBase, "Texte Partout", Cursor.TEXT);

            // --- Curseurs Magiques ---
            Label lblMagique = new Label("Curseurs UNIQUES (Générés en direct) :");
            lblMagique.setStyle("-fx-text-fill: #AAAACC; -fx-font-size: 16;");
            
            FlowPane boxMagique = new FlowPane(10, 10);
            boxMagique.setAlignment(Pos.CENTER);
            ajouterBoutonEmoji(boxMagique, "🍕 Pizza", "🍕");
            ajouterBoutonEmoji(boxMagique, "🗡️ Épée", "🗡️");
            ajouterBoutonEmoji(boxMagique, "🪄 Magie", "🪄");
            ajouterBoutonEmoji(boxMagique, "🍌 Banane", "🍌");
            ajouterBoutonEmoji(boxMagique, "🤡 Clown", "🤡");
            ajouterBoutonEmoji(boxMagique, "👽 Alien", "👽");

            Button btnFermer = new Button("Fermer le panneau");
            btnFermer.setStyle("-fx-background-color: transparent; -fx-border-color: white; -fx-text-fill: white; -fx-padding: 8 20; -fx-cursor: hand;");
            btnFermer.setOnAction(e -> {
                if (racine.getCenter() instanceof StackPane) {
                    ((StackPane) racine.getCenter()).getChildren().remove(panneauStyle);
                }
            });

            panneauStyle.getChildren().addAll(titre, lblBase, boxBase, lblMagique, boxMagique, btnFermer);
            
            if (racine.getCenter() instanceof StackPane) {
                ((StackPane) racine.getCenter()).getChildren().add(panneauStyle);
            }
        }
    }

    // --- OUTILS POUR CRÉER LES CURSEURS PERSONNALISÉS ---

    private void ajouterBoutonCurseur(FlowPane panneau, String nom, Cursor curseur) {
        Button btn = new Button(nom);
        btn.setStyle("-fx-background-color: #4bdb6a; -fx-text-fill: #1e1e2e; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15;");
        btn.setOnAction(e -> racine.getScene().setCursor(curseur));
        panneau.getChildren().add(btn);
    }

    private void ajouterBoutonEmoji(FlowPane panneau, String nom, String emoji) {
        Button btn = new Button(nom);
        btn.setStyle("-fx-background-color: #FF4D6A; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15;");
        
        btn.setOnAction(e -> {
            Label lblEmoji = new Label(emoji);
            lblEmoji.setStyle("-fx-font-size: 32; -fx-background-color: transparent; -fx-text-fill: white;");
            javafx.scene.Scene s = new javafx.scene.Scene(lblEmoji, Color.TRANSPARENT);
            
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT); 
            
            WritableImage imageCurseur = lblEmoji.snapshot(params, null);
            racine.getScene().setCursor(new javafx.scene.ImageCursor(imageCurseur, imageCurseur.getWidth() / 2, 0)); 
        });
        
        panneau.getChildren().add(btn);
    }

    // --- NAVIGATION CLASSIQUE ---

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
        Platform.exit();
    }

    @FXML
    private void handleParNumero(ActionEvent event) {
        System.out.println("Par numéro cliqué");
    }
}