package fr.univ_orleans.iut45.controleur;

import fr.univ_orleans.iut45.modele.BoiteComposee;
import fr.univ_orleans.iut45.modele.CollectionPersonnelle;
import fr.univ_orleans.iut45.modele.ContenuFigurine;
import fr.univ_orleans.iut45.modele.ContenuPiece;
import fr.univ_orleans.iut45.vue.Vue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PiecesManquantesControleur {

    @FXML private TextField champRecherche;
    @FXML private VBox menuDeroulant;
    @FXML private Label labelResume;
    @FXML private Label labelAucune;
    @FXML private VBox zoneResultats;

    private Vue vue;

    public void setVue(Vue vue) {
        this.vue = vue;

        champRecherche.textProperty().addListener((obs, ancien, nouveau) -> {
            if (nouveau.trim().isEmpty()) {
                cacherMenu();
                afficherTout();
                return;
            }

            CollectionPersonnelle collection = vue.getCollectionPersonnelle();
            if (collection == null) return;

            String recherche = nouveau.trim().toLowerCase();
            List<BoiteComposee> resultats = collection.getBoites().stream()
                .filter(b ->
                    b.getNumBoite().toLowerCase().contains(recherche) ||
                    b.getNomBoite().toLowerCase().contains(recherche)
                )
                .collect(Collectors.toList());

            if (resultats.isEmpty()) {
                cacherMenu();
            } else {
                afficherMenu(resultats);
            }
        });

        afficherTout();
    }

    @FXML
    private void initialize() {}

    

    private void afficherMenu(List<BoiteComposee> resultats) {
        menuDeroulant.getChildren().clear();

        for (BoiteComposee boite : resultats) {
            Label item = new Label("  " + boite.getNumBoite() + " - " + boite.getNomBoite());
            item.setMaxWidth(Double.MAX_VALUE);
            item.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: white;");
            item.setOnMouseEntered(e -> item.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: #E8F0F8;"));
            item.setOnMouseExited(e  -> item.setStyle("-fx-padding: 10; -fx-font-size: 13; -fx-cursor: hand; -fx-background-color: white;"));
            item.setOnMouseClicked(e -> {
                champRecherche.setText(boite.getNumBoite());
                cacherMenu();
                afficherBoitesFiltrees(List.of(boite));
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
    private void handleAfficherTout(ActionEvent event) {
        champRecherche.clear();
        afficherTout();
    }

    private void afficherTout() {
        CollectionPersonnelle collection = vue.getCollectionPersonnelle();
        if (collection == null || collection.getBoites().isEmpty()) {
            zoneResultats.getChildren().clear();
            labelResume.setText("Votre collection est vide.");
            labelAucune.setText("");
            return;
        }
        afficherBoitesFiltrees(collection.getBoites());
    }

    private void afficherBoitesFiltrees(List<BoiteComposee> boites) {
        zoneResultats.getChildren().clear();
        labelAucune.setText("");
        labelResume.setText("");

        int totalBoitesAvecManque = 0;
        int totalManquants = 0;

        for (BoiteComposee boite : boites) {
            List<ContenuPiece> manquantes = calculerPiecesManquantes(boite);
            List<ContenuFigurine> figurinesManquantes = calculerFigurinesManquantes(boite);

            int nb = manquantes.stream().mapToInt(ContenuPiece::getQuantite).sum()
                   + figurinesManquantes.stream().mapToInt(ContenuFigurine::getQuantite).sum();

            if (nb > 0) {
                totalBoitesAvecManque++;
                totalManquants += nb;
                zoneResultats.getChildren().add(
                    construireCarteBoite(boite, manquantes, figurinesManquantes, nb)
                );
            }
        }

        if (totalBoitesAvecManque == 0) {
            labelAucune.setText("✅ Aucune pièce manquante dans votre sélection !");
        } else {
            labelResume.setText(
                totalBoitesAvecManque + " boîte(s) incomplète(s)  •  " +
                totalManquants + " pièce(s) / figurine(s) manquante(s) au total"
            );
        }
    }

    private List<ContenuPiece> calculerPiecesManquantes(BoiteComposee boite) {
        if (boite.estPersonnalisee()) {
            return new ArrayList<>(boite.getPieces());
        } else {
            List<ContenuPiece> result = new ArrayList<>();
            for (BoiteComposee.PieceRetiree pr : boite.getPiecesRetirees()) {
                ContenuPiece cpTrouve = boite.getPieces().stream()
                    .filter(cp ->
                        cp.getPiece().obtenirNumPiece().equals(pr.numPiece) &&
                        cp.getPiece().obtenirCouleur().getIdCouleur() == pr.idCoul
                    )
                    .findFirst()
                    .orElse(null);

                if (cpTrouve != null) {
                    result.add(new ContenuPiece(cpTrouve.getPiece(), pr.quantiteRetiree, cpTrouve.estEnSupplement()));
                } else {
                }
            }
            return result;
        }
    }

    private List<ContenuFigurine> calculerFigurinesManquantes(BoiteComposee boite) {
        if (boite.estPersonnalisee()) {
            return new ArrayList<>(boite.getFigurines());
        } else {
            List<ContenuFigurine> result = new ArrayList<>();
            for (BoiteComposee.FigurineRetiree fr : boite.getFigurinesRetirees()) {
                ContenuFigurine cfTrouve = boite.getFigurines().stream()
                    .filter(cf -> cf.getFigurine().getIdFigurine().equals(fr.idFig))
                    .findFirst()
                    .orElse(null);

                if (cfTrouve != null) {
                    result.add(new ContenuFigurine(cfTrouve.getFigurine(), fr.quantiteRetiree));
                }
            }
            return result;
        }
    }

    private VBox construireCarteBoite(BoiteComposee boite,List<ContenuPiece> manquantes,List<ContenuFigurine> figurinesManquantes,int nbTotal) {
        VBox carte = new VBox(8);
        carte.setStyle(
            "-fx-background-color: white; -fx-padding: 16;" +
            "-fx-border-color: #FF4D6A; -fx-border-width: 2;" +
            "-fx-border-radius: 6; -fx-background-radius: 6;"
        );

        HBox entete = new HBox(10);
        entete.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label labelNom = new Label(boite.getNumBoite() + "  —  " + boite.getNomBoite());
        labelNom.setStyle("-fx-font-size: 15; -fx-font-weight: bold; -fx-text-fill: #1E3050;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badgeNb = new Label(nbTotal + " manquante(s)");
        badgeNb.setStyle(
            "-fx-background-color: #FF4D6A; -fx-text-fill: white;" +
            "-fx-font-size: 11; -fx-font-weight: bold;" +
            "-fx-padding: 3 10; -fx-background-radius: 12;"
        );

        entete.getChildren().addAll(labelNom, spacer, badgeNb);
        carte.getChildren().add(entete);

        Label labelInfo = new Label(
            "Année : " + boite.getAnnee() +
            "  •  Pièces totales : " + boite.getNbPieces() +
            "  •  " + (boite.estPersonnalisee() ? "Personnalisée" : "Officielle") +
            "  •  " + (boite.estComplete() ? "Complète" : "Incomplète")
        );
        labelInfo.setStyle("-fx-font-size: 12; -fx-text-fill: #5A7A9A;");
        carte.getChildren().add(labelInfo);

        javafx.scene.control.Separator sep = new javafx.scene.control.Separator();
        sep.setStyle("-fx-background-color: #E0E8F0;");
        carte.getChildren().add(sep);

        if (!manquantes.isEmpty()) {
            Label titre = new Label("Pièces manquantes :");
            titre.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #1E3050;");
            carte.getChildren().add(titre);
            for (ContenuPiece cp : manquantes) {
                carte.getChildren().add(construireLignePiece(cp));
            }
        }

        if (!figurinesManquantes.isEmpty()) {
            Label titre = new Label("Figurines manquantes :");
            titre.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-text-fill: #1E3050;");
            carte.getChildren().add(titre);
            for (ContenuFigurine cf : figurinesManquantes) {
                carte.getChildren().add(construireLigneFigurine(cf));
            }
        }

        return carte;
    }

    private HBox construireLignePiece(ContenuPiece cp) {
        HBox ligne = new HBox(10);
        ligne.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        ligne.setStyle("-fx-padding: 4 0 4 8;");

        Label qte = new Label("x" + cp.getQuantite());
        qte.setStyle(
            "-fx-background-color: #FFE0E6; -fx-text-fill: #FF4D6A;" +
            "-fx-font-size: 11; -fx-font-weight: bold;" +
            "-fx-padding: 2 8; -fx-background-radius: 8; -fx-min-width: 36;"
        );
        qte.setAlignment(javafx.geometry.Pos.CENTER);

        Label pastille = new Label();
        String rgb = cp.getPiece().obtenirCouleur().getRgb();
        pastille.setStyle(
            "-fx-background-color: #" + rgb + ";" +
            "-fx-min-width: 14; -fx-min-height: 14;" +
            "-fx-max-width: 14; -fx-max-height: 14;" +
            "-fx-background-radius: 7;" +
            "-fx-border-color: #AAAACC; -fx-border-width: 1; -fx-border-radius: 7;"
        );

        Label labelPiece = new Label(
            cp.getPiece().obtenirNumPiece() + "  " + cp.getPiece().obtenirNomPiece()
        );
        labelPiece.setStyle("-fx-font-size: 13; -fx-text-fill: #1E3050;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label labelCouleur = new Label(cp.getPiece().obtenirCouleur().getNomCouleur());
        labelCouleur.setStyle("-fx-font-size: 11; -fx-text-fill: #5A7A9A;");

        if (cp.estEnSupplement()) {
            Label supplement = new Label("SUPPLÉMENT");
            supplement.setStyle(
                "-fx-background-color: #E8F0F8; -fx-text-fill: #1E3050;" +
                "-fx-font-size: 10; -fx-padding: 2 6; -fx-background-radius: 6;"
            );
            ligne.getChildren().addAll(qte, pastille, labelPiece, spacer, labelCouleur, supplement);
        } else {
            ligne.getChildren().addAll(qte, pastille, labelPiece, spacer, labelCouleur);
        }

        return ligne;
    }

    private HBox construireLigneFigurine(ContenuFigurine cf) {
        HBox ligne = new HBox(10);
        ligne.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        ligne.setStyle("-fx-padding: 4 0 4 8;");

        Label qte = new Label("x" + cf.getQuantite());
        qte.setStyle(
            "-fx-background-color: #FFE0E6; -fx-text-fill: #FF4D6A;" +
            "-fx-font-size: 11; -fx-font-weight: bold;" +
            "-fx-padding: 2 8; -fx-background-radius: 8; -fx-min-width: 36;"
        );
        qte.setAlignment(javafx.geometry.Pos.CENTER);

        Label labelFig = new Label(
            cf.getFigurine().getNomFigurine() +
            "  (" + cf.getFigurine().getNombrePartie() + " partie(s))"
        );
        labelFig.setStyle("-fx-font-size: 13; -fx-text-fill: #1E3050;");

        ligne.getChildren().addAll(qte, labelFig);
        return ligne;
    }
}