package fr.univ_orleans.iut45.controleur;

import fr.univ_orleans.iut45.modele.*;
import fr.univ_orleans.iut45.vue.Vue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ComposerBoitePersoControleur {

    
    @FXML private TextField champNomBoite;
    @FXML private TextField champAnnee;
    @FXML private TextField champIdTheme;
    @FXML private TextField champNomTheme;
    @FXML private VBox  carteBoiteCreee;
    @FXML private Label labelBoiteCreeeNom;
    @FXML private Label labelBoiteCreeeInfo;
    @FXML private Label labelBoiteCreeeContenu;
    @FXML private VBox sectionType;
    @FXML private Button btnTypePiece;
    @FXML private Button btnTypeFigurine;
    @FXML private VBox sectionElement;
    @FXML private Label labelRechercheElement;
    @FXML private TextField champRechercheElement;
    @FXML private VBox menuElement;
    @FXML private VBox carteElement;
    @FXML private Label labelElementNom;
    @FXML private Label labelElementInfo;
    @FXML private TextField champQuantitePrise;
    @FXML private Button btnTerminer;
    @FXML private Label labelMessage;

    private Vue vue;
    private BoiteComposee boiteEnCours = null;
    private String typeSelectionne  = null;  

    
    private BoiteComposee boiteSourceSelectionnee = null;
    private ContenuPiece  pieceSourceSelectionnee = null;
    private ContenuFigurine figurineSourceSelectionnee = null;

    
    private static final String STYLE_ACTIF =
        "-fx-background-color: #5BC8F5; -fx-text-fill: white; " +
        "-fx-font-size: 13; -fx-font-weight: bold; " +
        "-fx-padding: 10 20 10 20; -fx-background-radius: 4; " +
        "-fx-cursor: hand; -fx-border-width: 0;";

    private static final String STYLE_INACTIF =
        "-fx-background-color: transparent; -fx-text-fill: #2C3E50; " +
        "-fx-font-size: 13; -fx-font-weight: bold; " +
        "-fx-padding: 10 20 10 20; -fx-background-radius: 4; " +
        "-fx-border-color: #AAAACC; -fx-border-width: 1; -fx-cursor: hand;";

   
    public void setVue(Vue vue) {
        this.vue = vue;
    }

    @FXML
    private void initialize() {
        champRechercheElement.textProperty().addListener((obs, old, val) -> {
            labelMessage.setText("");
            cacherCarteElement();
            if (val.trim().isEmpty()) { cacherMenuElement(); 
                return; 
            }
            afficherMenuDepuisCollection(val.trim());
        });
    }

    @FXML
    private void handleCreerBoite(ActionEvent event) {
        labelMessage.setText("");

        String nom = champNomBoite.getText().trim();
        String anneeStr = champAnnee.getText().trim();
        String idThemeStr = champIdTheme.getText().trim();
        String nomTheme = champNomTheme.getText().trim();

        if (nom.isBlank() || anneeStr.isBlank() || idThemeStr.isBlank() || nomTheme.isBlank()) {
            setMessage("Veuillez remplir tous les champs.", false);
            return;
        }

        int annee, idTheme;
        try {
            annee = Integer.parseInt(anneeStr);
            idTheme = Integer.parseInt(idThemeStr);
        } catch (NumberFormatException e) {
            setMessage("Année et ID du thème doivent être des entiers.", false);
            return;
        }
        String numBoite = "PERSO-" + new Random().nextInt(100000);

        Theme theme = new Theme(idTheme, nomTheme);
        boiteEnCours = new BoiteComposee(numBoite, nom, annee, 0, theme, true, true, true);
        labelBoiteCreeeNom.setText(numBoite + " — " + nom);
        labelBoiteCreeeInfo.setText("Année : " + annee + "  |  Thème : " + nomTheme + " (id " + idTheme + ")");
        rafraichirContenuCarte();

        carteBoiteCreee.setVisible(true);
        carteBoiteCreee.setManaged(true);

        afficherSectionType();
        btnTerminer.setVisible(true);
        btnTerminer.setManaged(true);

        setMessage("Boîte créée. Ajoutez maintenant des pièces ou figurines depuis votre collection", true);
    }

    @FXML
    private void handleTypePiece(ActionEvent event) {
        typeSelectionne = "PIECE";
        btnTypePiece.setStyle(STYLE_ACTIF);
        btnTypeFigurine.setStyle(STYLE_INACTIF);
        labelRechercheElement.setText("3. Rechercher la pièce à prendre dans votre collection :");
        champRechercheElement.setPromptText("Numéro ou nom de la pièce...");
        afficherSectionElement();
    }

    @FXML
    private void handleTypeFigurine(ActionEvent event) {
        typeSelectionne = "FIGURINE";
        btnTypePiece.setStyle(STYLE_INACTIF);
        btnTypeFigurine.setStyle(STYLE_ACTIF);
        labelRechercheElement.setText("3. Rechercher la figurine à prendre dans votre collection :");
        champRechercheElement.setPromptText("Nom ou ID de la figurine...");
        afficherSectionElement();
    }

    
    
    private void afficherMenuDepuisCollection(String val) {
        if (boiteEnCours == null || typeSelectionne == null) return;

        List<String[]> items = new ArrayList<>();
        // item[0] = label affiché      item[1] = numBoite    item[2] = numPiece/idFig    item[3] = info

        String valLower = val.toLowerCase();

        for (BoiteComposee b : vue.getCollectionPersonnelle().getBoites()) {
            if (b.getNumBoite().equals(boiteEnCours.getNumBoite())) continue;

            if ("PIECE".equals(typeSelectionne)) {
                for (ContenuPiece cp : b.getPieces()) {
                    String num  = cp.getPiece().obtenirNumPiece();
                    String nomP = cp.getPiece().obtenirNomPiece();
                    if ((num.toLowerCase().contains(valLower) || nomP.toLowerCase().contains(valLower))
                            && cp.getQuantite() > 0) {
                        items.add(new String[]{
                            num + " — " + nomP + "  [" + b.getNomBoite() + "]",
                            b.getNumBoite(),
                            num,
                            "Couleur : " + cp.getPiece().obtenirCouleur().getNomCouleur()+ "  |  Dispo : " + cp.getQuantite() + "  |  Source : " + b.getNomBoite()
                        });
                    }
                }
            } else { 
                for (ContenuFigurine cf : b.getFigurines()) {
                    String id   = cf.getFigurine().getIdFigurine();
                    String nomF = cf.getFigurine().getNomFigurine();
                    if ((id.toLowerCase().contains(valLower) || nomF.toLowerCase().contains(valLower))
                            && cf.getQuantite() > 0) {
                        items.add(new String[]{
                            id + " — " + nomF + "  [" + b.getNomBoite() + "]",
                            b.getNumBoite(),
                            id,
                            "Parties : " + cf.getFigurine().getNombrePartie() + "  |  Dispo : " + cf.getQuantite()+ "  |  Source : " + b.getNomBoite()
                        });
                    }
                }
            }
        }

        if (items.isEmpty()) { cacherMenuElement(); return; }
        afficherMenuElement(items);
    }

    private void afficherMenuElement(List<String[]> items) {
        menuElement.getChildren().clear();
        for (String[] item : items) {
            Label lbl = new Label(" " + item[0]);
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: white;");
            lbl.setOnMouseEntered(e -> lbl.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: #E8F0F8;"));
            lbl.setOnMouseExited (e -> lbl.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: white;"));
            lbl.setOnMouseClicked(e -> {
                champRechercheElement.setText(item[0]);
                cacherMenuElement();
                selectionnerElementDepuisCollection(item[1], item[2], item[0], item[3]);
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

    
    private void selectionnerElementDepuisCollection(String numBoiteSource,String elementId,String labelAffiche,String info) {
        boiteSourceSelectionnee = null;
        pieceSourceSelectionnee  = null;
        figurineSourceSelectionnee = null;

        for (BoiteComposee b : vue.getCollectionPersonnelle().getBoites()) {
            if (b.getNumBoite().equals(numBoiteSource)) {
                boiteSourceSelectionnee = b;
                break;
            }
        }
        if (boiteSourceSelectionnee == null) return;

        if ("PIECE".equals(typeSelectionne)) {
            for (ContenuPiece cp : boiteSourceSelectionnee.getPieces()) {
                if (cp.getPiece().obtenirNumPiece().equals(elementId)) {
                    pieceSourceSelectionnee = cp;
                    break;
                }
            }
        } else {
            for (ContenuFigurine cf : boiteSourceSelectionnee.getFigurines()) {
                if (cf.getFigurine().getIdFigurine().equals(elementId)) {
                    figurineSourceSelectionnee = cf;
                    break;
                }
            }
        }

        labelElementNom.setText(labelAffiche);
        labelElementInfo.setText(info);
        champQuantitePrise.clear();
        carteElement.setVisible(true);
        carteElement.setManaged(true);
    }

    private void cacherCarteElement() {
        pieceSourceSelectionnee     = null;
        figurineSourceSelectionnee  = null;
        boiteSourceSelectionnee     = null;
        carteElement.setVisible(false);
        carteElement.setManaged(false);
    }

    
    @FXML
    private void handlePrendre(ActionEvent event) {
        labelMessage.setText("");

        if (boiteEnCours == null) { setMessage("Créez d'abord votre boîte.", false); 
            return;
         }
        if (boiteSourceSelectionnee == null) { setMessage("Sélectionnez un élément.", false); 
            return; 
        }

        String qStr = champQuantitePrise.getText().trim();
        if (qStr.isBlank()) { setMessage("Indiquez une quantité.", false); 
            return; 
        }

        int qte;
        try {
            qte = Integer.parseInt(qStr);
            if (qte <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            setMessage("La quantité doit être un entier positif.", false);
            return;
        }

        if ("PIECE".equals(typeSelectionne)) {
            if (pieceSourceSelectionnee == null) { setMessage("Sélectionnez une pièce.", false); 
                return; 
            }
            if (qte > pieceSourceSelectionnee.getQuantite()) {
                setMessage("Quantité demandée supérieure à la disponibilité ("
                        + pieceSourceSelectionnee.getQuantite() + ").", false);
                return;
            }
            transfererPiece(qte);
        } else {
            if (figurineSourceSelectionnee == null) { setMessage("Sélectionnez une figurine.", false); 
                return;
            }
            if (qte > figurineSourceSelectionnee.getQuantite()) {
                setMessage("Quantité demandée supérieure à la disponibilité ("
                        + figurineSourceSelectionnee.getQuantite() + ").", false);
                return;
            }
            transfererFigurine(qte);
        }

        rafraichirContenuCarte();
        cacherCarteElement();
        champRechercheElement.clear();
    }

    private void transfererPiece(int qte) {
        ContenuPiece cpSource = pieceSourceSelectionnee;
        String numPiece = cpSource.getPiece().obtenirNumPiece();
        int idCoul= cpSource.getPiece().obtenirCouleur().getIdCouleur();

        int resteSource = cpSource.getQuantite() - qte;
        boiteSourceSelectionnee.getPieces().remove(cpSource);
        if (resteSource > 0) {
            boiteSourceSelectionnee.getPieces()
                .add(new ContenuPiece(cpSource.getPiece(), resteSource, cpSource.estEnSupplement()));
        }
        if (!boiteSourceSelectionnee.estPersonnalisee()) {
            for (int i = 0; i < qte; i++) {
                boiteSourceSelectionnee.enregistrerPieceRetiree(numPiece, idCoul);
            }
        }
        boiteSourceSelectionnee.setComplete(false);

        
        boolean dejaPresente = false;
        for (ContenuPiece cp : boiteEnCours.getPieces()) {
            if (cp.getPiece().obtenirNumPiece().equals(numPiece)
                    && cp.getPiece().obtenirCouleur().getIdCouleur() == idCoul) {
                boiteEnCours.getPieces().remove(cp);
                boiteEnCours.getPieces()
                    .add(new ContenuPiece(cp.getPiece(), cp.getQuantite() + qte, cp.estEnSupplement()));
                dejaPresente = true;
                break;
            }
        }
        if (!dejaPresente) {
            boiteEnCours.ajouterPiece(new ContenuPiece(cpSource.getPiece(), qte, false));
            for (int i = 0; i < qte; i++) boiteEnCours.incrementerNbPieces();
        } else {
            for (int i = 0; i < qte; i++) boiteEnCours.incrementerNbPieces();
        }

        setMessage(qte + " pièce(s) « " + cpSource.getPiece().obtenirNomPiece()+ " » ajoutée(s) depuis " + boiteSourceSelectionnee.getNomBoite() + ".", true);
    }

    private void transfererFigurine(int qte) {
        ContenuFigurine cfSource = figurineSourceSelectionnee;
        String idFig = cfSource.getFigurine().getIdFigurine();

        int resteSource = cfSource.getQuantite() - qte;
        boiteSourceSelectionnee.getFigurines().remove(cfSource);
        if (resteSource > 0) {
            boiteSourceSelectionnee.getFigurines()
                .add(new ContenuFigurine(cfSource.getFigurine(), resteSource));
        }
        if (!boiteSourceSelectionnee.estPersonnalisee()) {
            for (int i = 0; i < qte; i++) {
                boiteSourceSelectionnee.enregistrerFigurineRetiree(idFig);
            }
        }
        boiteSourceSelectionnee.setComplete(false);

        boolean dejaPresente = false;
        for (ContenuFigurine cf : boiteEnCours.getFigurines()) {
            if (cf.getFigurine().getIdFigurine().equals(idFig)) {
                boiteEnCours.getFigurines().remove(cf);
                boiteEnCours.getFigurines()
                    .add(new ContenuFigurine(cf.getFigurine(), cf.getQuantite() + qte));
                dejaPresente = true;
                break;
            }
        }
        if (!dejaPresente) {
            boiteEnCours.ajouterFigurine(new ContenuFigurine(cfSource.getFigurine(), qte));
        }

        setMessage(qte + " figurine(s) « " + cfSource.getFigurine().getNomFigurine()+ " » ajoutée(s) depuis " + boiteSourceSelectionnee.getNomBoite() + ".", true);
    }


    @FXML
    private void handleTerminer(ActionEvent event) {
        if (boiteEnCours == null) { setMessage("Aucune boîte à ajouter.", false); return; }

        boolean ok = vue.getCollectionPersonnelle().ajouterBoite(boiteEnCours);
        if (ok) {
            setMessage(" Boîte « " + boiteEnCours.getNomBoite()+ " » ajoutée à votre collection !", true);

            resetEcran();
        } else {
            setMessage("Cette boîte est déjà dans la collection.", false);
        }
    }


    private void afficherSectionType() {
        sectionType.setVisible(true);
        sectionType.setManaged(true);
    }

    private void afficherSectionElement() {
        cacherCarteElement();
        champRechercheElement.clear();
        sectionElement.setVisible(true);
        sectionElement.setManaged(true);
    }

    
    private void rafraichirContenuCarte() {
        if (boiteEnCours == null) return;
        int nbTypePieces = boiteEnCours.getPieces().size();
        int nbTypeFig    = boiteEnCours.getFigurines().size();
        labelBoiteCreeeContenu.setText(
            "Pièces : " + nbTypePieces + " type(s)  |  Figurines : " + nbTypeFig + " type(s)"+ "  |  Total pièces : " + boiteEnCours.getNbPieces()
        );
    }

    
    private void resetEcran() {
        boiteEnCours = null;
        typeSelectionne = null;

        champNomBoite.clear();
        champAnnee.clear();
        champIdTheme.clear();
        champNomTheme.clear();

        carteBoiteCreee.setVisible(false);
        carteBoiteCreee.setManaged(false);
        sectionType.setVisible(false);
        sectionType.setManaged(false);
        sectionElement.setVisible(false);
        sectionElement.setManaged(false);
        btnTerminer.setVisible(false);
        btnTerminer.setManaged(false);

        cacherMenuElement();
        cacherCarteElement();

        btnTypePiece.setStyle(STYLE_INACTIF);
        btnTypeFigurine.setStyle(STYLE_INACTIF);
    }

    private void setMessage(String texte, boolean succes) {
        labelMessage.setText(texte);
        String couleur = succes ? "#1A6B3C" : "#FF4D6A";
        labelMessage.setStyle(
            "-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: " + couleur + "; -fx-padding: 16 0 0 0;"
        );
    }
}