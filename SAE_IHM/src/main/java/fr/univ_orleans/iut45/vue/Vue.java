package fr.univ_orleans.iut45.vue;
 
import fr.univ_orleans.iut45.controleur.AdministrateurControleur;
import fr.univ_orleans.iut45.controleur.CollectionneurControleur;
import fr.univ_orleans.iut45.controleur.ConnexionControleur;
import fr.univ_orleans.iut45.controleur.DetailBoiteControleur;
import fr.univ_orleans.iut45.controleur.MainMenuControleur;
import fr.univ_orleans.iut45.controleur.RechercherBoiteControleur;
import fr.univ_orleans.iut45.controleur.AjouterBoiteControleur;
import fr.univ_orleans.iut45.controleur.AjouterPieceControleur;
import fr.univ_orleans.iut45.controleur.CollectionControleur;
import fr.univ_orleans.iut45.controleur.CreerThemeControleur;
import fr.univ_orleans.iut45.modele.ConnexionMySQL;
import fr.univ_orleans.iut45.controleur.RechercherBoiteParNomControleur;
import fr.univ_orleans.iut45.controleur.RechercherBoiteParThemeControleur;
import fr.univ_orleans.iut45.controleur.BoitesContenantPieceControleur;
import fr.univ_orleans.iut45.modele.CollectionPersonnelle;
import fr.univ_orleans.iut45.controleur.SupprimerDeCollectionControleur;
 
import javafx.fxml.FXMLLoader;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import fr.univ_orleans.iut45.controleur.SupprimerBoiteControleur;
import fr.univ_orleans.iut45.controleur.SupprimerPieceControleur;
import fr.univ_orleans.iut45.controleur.PiecesManquantesControleur;
 
 
public class Vue extends Application {
 
    private BorderPane panelCentral;
    private ConnexionMySQL connexionMySQL;
    private Label labelSection;
    private CollectionPersonnelle collection;

 
    public static void main(String[] args) {
        launch(args);
    }
 @Override
public void start(Stage primaryStage) {
    try {
        connexionMySQL = new ConnexionMySQL();
        panelCentral = new BorderPane();
        
        // --- BARRE TOP GLOBALE ---
        HBox topBar = new HBox(12);
        topBar.setStyle("-fx-background-color: #1E1E2E; -fx-padding: 6 12 6 12;");
        topBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Button btnAccueil = new Button("🧱 BRIQU'IUTO");
        btnAccueil.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #FF4D6A; " +
            "-fx-font-weight: bold; -fx-font-size: 13; -fx-cursor: hand; " +
            "-fx-border-width: 0; -fx-padding: 0;"
        );
        btnAccueil.setOnAction(e -> this.modeAcceuil());

        Label labelAppli = new Label("Collection Manager");
        labelAppli.setStyle("-fx-text-fill: #AAAACC; -fx-font-size: 11;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        labelSection = new Label("");
        labelSection.setStyle(
            "-fx-text-fill: #B0C8E0; -fx-font-size: 12; " +
            "-fx-font-weight: bold; -fx-padding: 0 8 0 0;"
        );

        topBar.getChildren().addAll(btnAccueil, labelAppli, spacer, labelSection);
        panelCentral.setTop(topBar);
        

        this.modeConnexion();

        Scene scene = new Scene(panelCentral, 1000, 650);
        primaryStage.setScene(scene);
        primaryStage.setTitle("BRIQU'IUTO");
        primaryStage.show();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    public ConnexionMySQL getConnexionMySQL() {
        return connexionMySQL;
    }

    public void modeAcceuil() {
        setTitrePage("");
        this.panelCentral.setLeft(null);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/Menu.fxml"));
            BorderPane root = loader.load();
            
            MainMenuControleur controleur = loader.getController();
            controleur.setVue(this);
            if (connexionMySQL.isConnecte()) {
                panelCentral.setCenter(root);
                this.collection = new CollectionPersonnelle(connexionMySQL);
                collection.charger();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    public void modeConnexion() {
        setTitrePage(""); 
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/Connexion.fxml"));
            BorderPane root = loader.load();
 
            ConnexionControleur controleur = loader.getController();
            controleur.setVue(this);
 
            panelCentral.setCenter(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modeCollectionneur() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/fr/univ_orleans/iut45/vue/FXML/CollectionneurNav.fxml"));
            VBox vb = loader.load();

            CollectionneurControleur controleur = loader.getController();
            controleur.setVue(this);

            this.panelCentral.setLeft(vb);
            this.modeRechercherBoite(); 
            this.setTitrePage("Espace Collectionneur  |  Rechercher une boîte");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modeAdministrateur() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/AdministrateurNav.fxml"));
            VBox root = loader.load();
            AdministrateurControleur controleur = loader.getController();
            controleur.setVue(this);

            this.panelCentral.setCenter(null); 
            panelCentral.setLeft(root);
            this.modeAjouterBoite();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTitrePage(String titre) {
        if (labelSection != null) {
            labelSection.setText(titre);
        }
    }

    public void modeRechercherBoite() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/RechercherBoite.fxml"));
            VBox contenu = loader.load();

            RechercherBoiteControleur ctrl = loader.getController();
            ctrl.setVue(this);

            this.panelCentral.setCenter(contenu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modeAjouterBoite() {
        setTitrePage("Administrateur  |  Ajouter une boîte");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/AjouterBoite.fxml"));
            VBox contenu = loader.load();
            AjouterBoiteControleur ctrl = loader.getController();
            ctrl.setVue(this);
            this.panelCentral.setCenter(contenu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void modeRechercherBoiteParNom() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/RechercherBoiteParNom.fxml"));
            VBox contenu = loader.load();
    
            RechercherBoiteParNomControleur ctrl = loader.getController();
            ctrl.setVue(this);
    
            this.panelCentral.setCenter(contenu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modeDetail() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/DetailBoite.fxml"));
            VBox contenu = loader.load();
    
            DetailBoiteControleur ctrl = loader.getController();
            ctrl.setVue(this);
    
            this.panelCentral.setCenter(contenu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modeDetail(String numBoite) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/DetailBoite.fxml"));
            VBox contenu = loader.load();

            DetailBoiteControleur ctrl = loader.getController();
            ctrl.setVue(this);
            if (numBoite != null) {
                ctrl.chargerBoite(numBoite);
            }

            this.panelCentral.setCenter(contenu);
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void modeAjouterPiece() {
        setTitrePage("Administrateur  |  Ajouter une pièce");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/AjouterPiece.fxml"));
            VBox contenu = loader.load();
            AjouterPieceControleur ctrl = loader.getController();
            ctrl.setVue(this);
            this.panelCentral.setCenter(contenu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modeCreerTheme() {
        setTitrePage("Administrateur  |  Créer un thème");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/CreerTheme.fxml"));
            VBox contenu = loader.load();
            CreerThemeControleur ctrl = loader.getController();
            ctrl.setVue(this);
            this.panelCentral.setCenter(contenu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void modeSupprimerBoite() {
        setTitrePage("Administrateur  |  Supprimer une boîte");
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/SupprimerBoite.fxml"));
            VBox contenu = loader.load();
            
            SupprimerBoiteControleur ctrl = loader.getController();
            ctrl.setVue(this);
            
            this.panelCentral.setCenter(contenu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modeCollection() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/collection.fxml"));
            VBox contenu = loader.load();
            
            CollectionControleur ctrl = loader.getController();
            ctrl.setVue(this);
            
            this.panelCentral.setCenter(contenu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public CollectionPersonnelle getCollectionPersonnelle(){
        return this.collection;
    }

    public void modeStatistiquesBoite() {
        setTitrePage("Espace Collectionneur  |  Statistiques d'une boîte");
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/StatistiquesBoite.fxml"));
            javafx.scene.layout.VBox contenu = loader.load();
            
            fr.univ_orleans.iut45.controleur.StatistiquesBoiteControleur ctrl = loader.getController();
            ctrl.setVue(this);
            
            this.panelCentral.setCenter(contenu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     
    public void modeBoitesContenantPiece() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/BoitesContenantPiece.fxml"));
            VBox contenu = loader.load();
 
            BoitesContenantPieceControleur ctrl = loader.getController();
            ctrl.setVue(this);
            this.panelCentral.setCenter(contenu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modeSupprimerPiece() {
        setTitrePage("Administrateur  |  Supprimer une pièce");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/SupprimerPiece.fxml"));
            VBox contenu = loader.load();
            SupprimerPieceControleur ctrl = loader.getController();
            ctrl.setVue(this);
            this.panelCentral.setCenter(contenu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void modeSupprimerDeCollection() {
        setTitrePage("Espace Collectionneur  |  Retirer une boîte de ma collection");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/fr/univ_orleans/iut45/vue/FXML/SupprimerDeCollection.fxml"));
            VBox contenu = loader.load();
    
            SupprimerDeCollectionControleur ctrl = loader.getController();
            ctrl.setVue(this);  
    
            this.panelCentral.setCenter(contenu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modePiecesManquantes() {
        setTitrePage("Espace Collectionneur  |  Pièces manquantes");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/PiecesManquantes.fxml"));
            VBox contenu = loader.load();
    
            PiecesManquantesControleur ctrl = loader.getController();
            ctrl.setVue(this);   
    
            this.panelCentral.setCenter(contenu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void modeRechercherTheme() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fr/univ_orleans/iut45/vue/FXML/RechercherBoiteParTheme.fxml"));
            VBox contenu = loader.load();
    
            RechercherBoiteParThemeControleur ctrl = loader.getController();
            ctrl.setVue(this);   
    
            this.panelCentral.setCenter(contenu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

        

