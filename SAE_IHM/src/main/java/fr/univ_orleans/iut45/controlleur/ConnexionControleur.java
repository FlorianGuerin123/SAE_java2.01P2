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

        String styleBouton = "-fx-background-color: #FF4D6A; " + 
                             "-fx-text-fill: white; " +           
                             "-fx-font-weight: bold; " +         
                             "-fx-font-size: 13; " +              
                             "-fx-background-radius: 5; " +       
                             "-fx-cursor: hand; " +               
                             "-fx-padding: 10 0 10 0;";           

        Button[] lesBoutons = {btnPFC, btnMorpion, btnJusteNombre, btnBriqueDoree};
        for (Button btn : lesBoutons) {
            btn.setStyle(styleBouton);
            btn.setPrefWidth(220);
        }

        btnPFC.setOnAction(e -> lancerJeuPFC());
        btnMorpion.setOnAction(e -> lancerMorpion());
        btnJusteNombre.setOnAction(e -> lancerJusteNombre());
        btnBriqueDoree.setOnAction(e -> lancerBriqueDoree());

        VBox racine = new VBox(15, titre, btnPFC, btnMorpion, btnJusteNombre, btnBriqueDoree);
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
}