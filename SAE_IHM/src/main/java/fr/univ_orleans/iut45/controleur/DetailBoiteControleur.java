package fr.univ_orleans.iut45.controleur;

import fr.univ_orleans.iut45.vue.Vue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

import fr.univ_orleans.iut45.modele.*;

public class DetailBoiteControleur {

    @FXML private TextField champNumero;
    @FXML private Label labelMessage;
    @FXML private VBox menuDeroulant;

    @FXML private ScrollPane scrollResultats;
    @FXML private VBox contenuDetail;
    @FXML private VBox carteInfo;
    @FXML private Label labelNomBoite;
    @FXML private Label labelNumBoite;
    @FXML private Label labelAnnee;
    @FXML private Label labelTheme;
    @FXML private Label labelNbPieces;
    @FXML private VBox sectionPieces;
    @FXML private FlowPane flowPieces;
    @FXML private VBox sectionFigurines;
    @FXML private FlowPane flowFigurines;
    @FXML private VBox sectionSousBoites;
    @FXML private FlowPane flowSousBoites;

    private Vue vue;

    public void setVue(Vue vue) {
        this.vue = vue;
    }
    
    @FXML
    private void initialize() {
        champNumero.textProperty().addListener((observable, ancienneValeur, nouvelleValeur) -> {
            labelMessage.setText("");
            cacherContenu();
            
            if (nouvelleValeur.trim().isEmpty()) {
                cacherMenu();
                return;
            }

            try {
                if (vue != null && vue.getConnexionMySQL() != null) {
                    BoiteBD boiteBD = new BoiteBD(vue.getConnexionMySQL());
                    List<BoiteSimple> resultats = boiteBD.rechercherBoitesDynamique(nouvelleValeur.trim());

                    if (resultats.isEmpty()) {
                        cacherMenu();
                    } else {
                        afficherMenu(resultats);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void afficherMenu(List<BoiteSimple> resultats) {
        menuDeroulant.getChildren().clear(); 
        
        for (BoiteSimple boite : resultats) {
            Label item = new Label("" + boite.getNumBoite() + " - " + boite.getNomBoite());
            item.setMaxWidth(Double.MAX_VALUE);
            item.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: white;");
            
            item.setOnMouseEntered(e -> item.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: #E8F0F8;"));
            item.setOnMouseExited(e -> item.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: white;"));
            
            item.setOnMouseClicked(e -> {
                champNumero.setText(boite.getNumBoite()); 
                cacherMenu();
                handleAfficher(null);
            });
            
            
           
            menuDeroulant.getChildren().add(item);
        }
        
        menuDeroulant.setVisible(true);
        menuDeroulant.setManaged(true);
    }

    private void cacherMenu() {
        menuDeroulant.setVisible(false);
        menuDeroulant.setManaged(false);
        menuDeroulant.getChildren().clear();
    }

    @FXML
    private void handleAfficher(ActionEvent event) {
        afficherDetail(champNumero.getText().trim());
    }

    public void chargerBoite(String numero) {
        champNumero.setText(numero);
        afficherDetail(numero);
    }

    private void afficherDetail(String numero) {
        if (numero.isEmpty()) {
            labelMessage.setStyle("-fx-text-fill: #FF4D6A; -fx-font-weight: bold;");
            labelMessage.setText("Veuillez saisir un numéro de boîte.");
            cacherContenu();
            return;
        }

        try {
            BoiteBD boiteBD = new BoiteBD(vue.getConnexionMySQL());
            BoiteSimple boite = boiteBD.rechercherBoite(numero);
            
            if (boite == null) {
                labelMessage.setStyle("-fx-text-fill: #FF4D6A; -fx-font-weight: bold;");
                labelMessage.setText("Aucune boîte trouvée avec le numéro « " + numero + " ».");
                cacherContenu();
                return;
            }

            labelNomBoite.setText(boite.getNomBoite());
            labelNumBoite.setText("N° " + boite.getNumBoite());
            labelAnnee.setText("Année : " + boite.getAnnee());
            labelTheme.setText("Thème : " + (boite.getTheme() != null ? boite.getTheme().getNomTheme() : "—"));
            labelNbPieces.setText("Pièces : " + boite.getNbPieces());

            flowPieces.getChildren().clear();
            flowFigurines.getChildren().clear();
            flowSousBoites.getChildren().clear();

            PieceBD pieceBD = new PieceBD(vue.getConnexionMySQL());
            List<Piece> pieces = pieceBD.getPiecesBoite(numero);
            
            FigurineBD figurineBD = new FigurineBD(vue.getConnexionMySQL());
            List<Figurine> figurines = figurineBD.getFigurinesDansBoite(numero);
            
            List<BoiteSimple> boitesIncluses = boiteBD.getBoitesIncluses(numero);
            
            VBox colImage = new VBox(4);
            colImage.setAlignment(Pos.CENTER);
            HBox.setHgrow(colImage, Priority.ALWAYS);
            Image imageLego = new Image("https://cdn.rebrickable.com/media/sets/" + boite.getNumBoite() + ".jpg");
            ImageView imgvLego = new ImageView(imageLego);
            imgvLego.setFitWidth(175);
            imgvLego.setFitHeight(120);
            colImage.getChildren().add(imgvLego);
            carteInfo.getChildren().removeIf(node -> node instanceof VBox);
            carteInfo.getChildren().add(colImage);
            if (pieces == null || pieces.isEmpty()) {
                
                flowPieces.getChildren().add(new Label("Aucune pièce enregistrée pour cette boîte."));
            } else {
                for (Piece p : pieces)  {
                    String textePiece = "Nom : " + p.obtenirNomPiece() +  " - Couleur : "  + p.obtenirCouleur().getNomCouleur();
                    flowPieces.getChildren().add(creerText(textePiece, "#F0F4F8", "#1E1E2E"));
                }
            }

            if (figurines == null || figurines.isEmpty()) {
                flowFigurines.getChildren().add(new Label("Aucune figurine dans cette boîte."));
            } else {
                for (Figurine f : figurines) {
                    String texteFig = f.getNomFigurine() + " (x" + f.getNombrePartie() + ")";
                    flowFigurines.getChildren().add(creerText(texteFig, "#EBF3FE", "#1E1E2E"));
                }
            }

            if (boitesIncluses == null || boitesIncluses.isEmpty()) {
                flowSousBoites.getChildren().add(new Label("Cette boîte ne contient aucune sous-boîte."));
            } else {
                for (BoiteSimple bs : boitesIncluses) {
                    String texteBs = "N° " + bs.getNumBoite() + " - " + bs.getNomBoite();
                    flowSousBoites.getChildren().add(creerText(texteBs, "#FFF0F2", "#FF4D6A"));
                    
                }
            }
            

            labelMessage.setText("");
            afficherContenu();
            cacherMenu();

        } catch (Exception e) {
            labelMessage.setStyle("-fx-text-fill: #FF4D6A; -fx-font-weight: bold;");
            labelMessage.setText("Erreur lors de l'affichage du détail : " + e.getMessage());
            cacherContenu();
            e.printStackTrace();
        }
        
    }

    private Label creerText(String texte, String couleurFond, String couleurTexte) {
        Label text = new Label(texte);
        text.setStyle(
            "-fx-background-color: " + couleurFond + "; " +
            "-fx-text-fill: " + couleurTexte + "; " +
            "-fx-font-size: 12px; " +
            "-fx-padding: 6px 12px 6px 12px; " +
            "-fx-background-radius: 20px; " +
            "-fx-border-color: #AAAACC; " +
            "-fx-border-radius: 20px; " +
            "-fx-border-width: 1px;"
        );
        return text;
    }

    private void afficherContenu() {
        contenuDetail.setVisible(true);
        contenuDetail.setManaged(true);
    }

    private void cacherContenu() {
        contenuDetail.setVisible(false);
        contenuDetail.setManaged(false);
    }
}