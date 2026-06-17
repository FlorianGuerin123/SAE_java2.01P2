package fr.univ_orleans.iut45.controleur;

import fr.univ_orleans.iut45.modele.*;
import fr.univ_orleans.iut45.vue.Vue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.List;

public class MajContenuBoiteControleur {

    @FXML private TextField champRechercheBoite;
    @FXML private VBox menuBoite;
    @FXML private VBox carteBoite;
    @FXML private Label labelBoiteNom;
    @FXML private Label labelBoiteInfo;

    @FXML private VBox sectionType;
    @FXML private Button btnTypePiece;
    @FXML private Button btnTypeFigurine;
    @FXML private Button btnTypeBoite;

    @FXML private VBox sectionElement;
    @FXML private Label labelRechercheElement;
    @FXML private TextField champRechercheElement;
    @FXML private VBox menuElement;
    @FXML private VBox carteElement;
    @FXML private Label labelElementNom;
    @FXML private Label labelElementInfo;
    @FXML private TextField champQuantite;
    @FXML private VBox sectionCouleur;
    @FXML private TextField champCouleur;
    @FXML private VBox sectionSupplement;
    @FXML private CheckBox checkSupplement;
    @FXML private Button btnAjouter;

    @FXML private Label labelMessage;
    private Vue vue;
    private BoiteSimple boiteSelectionnee  = null;
    private String typeSelectionne = null; // "PIECE", "FIGURINE", "BOITE"
    private String elementSelectionne = null; // nom utilisé pour l'ajout
    private String elementId = null; // numBoite pour le cas BOITE

    private static final String STYLE_BTN_ACTIF =
        "-fx-background-color: #5BC8F5; -fx-text-fill: white; " +
        "-fx-font-size: 13; -fx-font-weight: bold; " +
        "-fx-padding: 10 20 10 20; -fx-background-radius: 4; " +
        "-fx-cursor: hand; -fx-border-width: 0;";

    private static final String STYLE_BTN_INACTIF =
        "-fx-background-color: transparent; -fx-text-fill: #2C3E50; " +
        "-fx-font-size: 13; -fx-font-weight: bold; " +
        "-fx-padding: 10 20 10 20; -fx-background-radius: 4; " +
        "-fx-border-color: #AAAACC; -fx-border-width: 1; -fx-cursor: hand;";

    public void setVue(Vue vue) {
        this.vue = vue;
    }

    @FXML
    private void initialize() {
        champRechercheBoite.textProperty().addListener((obs, old, val) -> {
            labelMessage.setText("");
            cacherCarteBoite();
            cacherSectionType();
            cacherSectionElement();
            if (val.trim().isEmpty()) { cacherMenuBoite(); return; }
            try {
                BoiteBD bd = new BoiteBD(vue.getConnexionMySQL());
                List<BoiteSimple> res = bd.rechercherBoitesDynamique(val.trim());
                if (res.isEmpty()) cacherMenuBoite();
                else afficherMenuBoite(res);
            } catch (Exception e) { e.printStackTrace(); }
        });

        champRechercheElement.textProperty().addListener((obs, old, val) -> {
            labelMessage.setText("");
            cacherCarteElement();
            if (val.trim().isEmpty()) { cacherMenuElement(); return; }
            try {
                rechercherElement(val.trim());
            } catch (Exception e) { e.printStackTrace(); }
        });
    }

    private void afficherMenuBoite(List<BoiteSimple> resultats) {
        menuBoite.getChildren().clear();
        for (BoiteSimple b : resultats) {
            Label item = new Label(" " + b.getNumBoite() + " — " + b.getNomBoite());
            item.setMaxWidth(Double.MAX_VALUE);
            item.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: white;");
            item.setOnMouseEntered(e -> item.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: #E8F0F8;"));
            item.setOnMouseExited(e -> item.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: white;"));
            item.setOnMouseClicked(e -> {
                champRechercheBoite.setText(b.getNumBoite());
                cacherMenuBoite();
                selectionnerBoite(b);
            });
            menuBoite.getChildren().add(item);
        }
        menuBoite.setVisible(true);
        menuBoite.setManaged(true);
    }

    private void cacherMenuBoite() {
        menuBoite.setVisible(false);
        menuBoite.setManaged(false);
        menuBoite.getChildren().clear();
    }

    private void selectionnerBoite(BoiteSimple b) {
        boiteSelectionnee = b;
        labelBoiteNom.setText(b.getNumBoite() + " — " + b.getNomBoite());
        labelBoiteInfo.setText("Année : " + b.getAnnee() + "  |  Pièces : " + b.getNbPieces() + "  |  Thème : " + b.getTheme().getNomTheme());
        carteBoite.setVisible(true);
        carteBoite.setManaged(true);
        afficherSectionType();
    }

    private void cacherCarteBoite() {
        boiteSelectionnee = null;
        carteBoite.setVisible(false);
        carteBoite.setManaged(false);
    }


    

    private void afficherSectionType() {
        sectionType.setVisible(true);
        sectionType.setManaged(true);
    }

    private void cacherSectionType() {
        typeSelectionne = null;
        sectionType.setVisible(false);
        sectionType.setManaged(false);
        reinitialiserBoutonType();
    }

    private void reinitialiserBoutonType() {
        btnTypePiece.setStyle(STYLE_BTN_INACTIF);
        btnTypeFigurine.setStyle(STYLE_BTN_INACTIF);
        btnTypeBoite.setStyle(STYLE_BTN_INACTIF);
    }

    @FXML
    private void handleTypePiece(ActionEvent event) {
        typeSelectionne = "PIECE";
        btnTypePiece.setStyle(STYLE_BTN_ACTIF);
        btnTypeFigurine.setStyle(STYLE_BTN_INACTIF);
        btnTypeBoite.setStyle(STYLE_BTN_INACTIF);
        labelRechercheElement.setText("3. Rechercher la pièce à ajouter :");
        champRechercheElement.setPromptText("Numéro ou nom de la pièce...");
        sectionCouleur.setVisible(true);  sectionCouleur.setManaged(true);
        sectionSupplement.setVisible(true); sectionSupplement.setManaged(true);
        afficherSectionElement();
    }

    @FXML
    private void handleTypeFigurine(ActionEvent event) {
        typeSelectionne = "FIGURINE";
        btnTypePiece.setStyle(STYLE_BTN_INACTIF);
        btnTypeFigurine.setStyle(STYLE_BTN_ACTIF);
        btnTypeBoite.setStyle(STYLE_BTN_INACTIF);
        labelRechercheElement.setText("3. Rechercher la figurine à ajouter :");
        champRechercheElement.setPromptText("Nom de la figurine...");
        sectionCouleur.setVisible(false);  sectionCouleur.setManaged(false);
        sectionSupplement.setVisible(false); sectionSupplement.setManaged(false);
        afficherSectionElement();
    }

    @FXML
    private void handleTypeBoite(ActionEvent event) {
        typeSelectionne = "BOITE";
        btnTypePiece.setStyle(STYLE_BTN_INACTIF);
        btnTypeFigurine.setStyle(STYLE_BTN_INACTIF);
        btnTypeBoite.setStyle(STYLE_BTN_ACTIF);
        labelRechercheElement.setText("3. Rechercher la boîte à inclure :");
        champRechercheElement.setPromptText("Numéro ou nom de la boîte...");
        sectionCouleur.setVisible(false);  sectionCouleur.setManaged(false);
        sectionSupplement.setVisible(false); sectionSupplement.setManaged(false);
        afficherSectionElement();
    }

  

    private void afficherSectionElement() {
        cacherCarteElement();
        champRechercheElement.clear();
        sectionElement.setVisible(true);
        sectionElement.setManaged(true);
    }

    private void cacherSectionElement() {
        typeSelectionne = null;
        elementSelectionne = null;
        champRechercheElement.clear();
        cacherMenuElement();
        cacherCarteElement();
        sectionElement.setVisible(false);
        sectionElement.setManaged(false);
    }

    private void rechercherElement(String val) throws Exception {
        switch (typeSelectionne) {
            case "PIECE" -> {
                PieceBD bd = new PieceBD(vue.getConnexionMySQL());
                List<Piece> res = bd.rechercherPiecesDynamique(val);
                if (res.isEmpty()){
                    cacherMenuElement();
                }
                else {
                    afficherMenuElement(res.stream().map(p -> new String[]{p.obtenirNumPiece(), p.obtenirNomPiece(), p.obtenirCategorie().getNomCat()}).toList());
                }
            }
            case "FIGURINE" -> {
                FigurineBD bd = new FigurineBD(vue.getConnexionMySQL());
                List<Figurine> res = bd.getFigurinesParNom(val);
                if (res.isEmpty()) {
                    cacherMenuElement();
                }
                else {
                    afficherMenuElement(res.stream().map(f -> new String[]{f.getIdFigurine(), f.getNomFigurine(), "Parties : " + f.getNombrePartie()}).toList());
                }
            }
            case "BOITE" -> {
                BoiteBD bd = new BoiteBD(vue.getConnexionMySQL());
                List<BoiteSimple> res = bd.rechercherBoitesDynamique(val);
                if (res.isEmpty()) cacherMenuElement();
                else afficherMenuElement(res.stream()
                    .map(b -> new String[]{b.getNumBoite(), b.getNomBoite(), "Thème : " + b.getTheme().getNomTheme()})
                    .toList());
            }
        }
    }

    private void afficherMenuElement(List<String[]> items) {
        menuElement.getChildren().clear();
        for (String[] item : items) {
            // item[0] = id/num, item[1] = nom, item[2] = info
            Label lbl = new Label(" " + item[0] + " — " + item[1]);
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: white;");
            lbl.setOnMouseEntered(e -> lbl.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: #E8F0F8;"));
            lbl.setOnMouseExited(e -> lbl.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: white;"));
            lbl.setOnMouseClicked(e -> {
                champRechercheElement.setText(item[1]);
                cacherMenuElement();
                selectionnerElement(item[0], item[1], item[2]);
            });
            menuElement.getChildren().add(lbl);
        }
        menuElement.setVisible(true);
        menuElement.setManaged(true);
    }

    private void cacherMenuElement() {
        menuElement.setVisible(false);
        menuElement.setManaged(false);
        menuElement.getChildren().clear();
    }

    private void selectionnerElement(String id, String nom, String info) {
        elementSelectionne = nom; // on garde le nom (utilisé par ContenirXxxBD)
        elementId = id;
        labelElementNom.setText(id + " — " + nom);
        labelElementInfo.setText(info);
        champQuantite.clear();
        champCouleur.clear();
        checkSupplement.setSelected(false);
        carteElement.setVisible(true);
        carteElement.setManaged(true);
    }

    private void cacherCarteElement() {
        elementSelectionne = null;
        carteElement.setVisible(false);
        carteElement.setManaged(false);
    }

    @FXML
    private void handleAjouter(ActionEvent event) {
        labelMessage.setText("");
        if (boiteSelectionnee == null || typeSelectionne == null || elementSelectionne == null) {
            setMessage("Veuillez sélectionner une boîte et un élément.", false);
            return;
        }
        String qStr = champQuantite.getText().trim();
        if (qStr.isBlank()){ 
            setMessage("Veuillez indiquer une quantité.", false); 
            return; 
        }
        int quantite;
        try {
            quantite = Integer.parseInt(qStr);
            if (quantite <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            setMessage("La quantité doit être un entier positif.", false);
            return;
        }
        try {
            String numBoite = boiteSelectionnee.getNumBoite();
            switch (typeSelectionne) {
                case "PIECE" -> {
                    String couleur = champCouleur.getText().trim();
                    if (couleur.isBlank()) { 
                        setMessage("Veuillez indiquer le nom de la couleur.", false); 
                        return; 
                    }
                    String enSupp = checkSupplement.isSelected() ? "T" : "F";
                    new ContenirPieceBD(vue.getConnexionMySQL()).ajouterPieceDansBoite(elementSelectionne, numBoite, couleur, enSupp, quantite);
                    setMessage("Pièce \"" + elementSelectionne + "\" ajoutée dans la boîte " + numBoite + ".", true);
                }
                case "FIGURINE" -> {
                    new ContenirFigurineBD(vue.getConnexionMySQL()).ajouterFigurineDansBoite(elementSelectionne, numBoite, quantite);
                    setMessage("Figurine \"" + elementSelectionne + "\" ajoutée dans la boîte " + numBoite + ".", true);
                }
                case "BOITE" -> {
                    new ContenirBoiteBD(vue.getConnexionMySQL()).ajouterBoiteDansBoite(numBoite, elementId);
                    setMessage("Boîte \"" + elementSelectionne + "\" ajoutée dans la boîte " + numBoite + ".", true);
                }
            }
            cacherCarteElement();
            champRechercheElement.clear();
            elementSelectionne = null;

        } catch (Exception e) {
            setMessage("Erreur : " + e.getMessage(), false);
            e.printStackTrace();
        }
    }

    private void setMessage(String texte, boolean succes) {
        labelMessage.setText(texte);
        String couleur = succes ? "#1A6B3C" : "#FF4D6A";
        labelMessage.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: " + couleur + "; -fx-padding: 16 0 0 0;");
    }
}
