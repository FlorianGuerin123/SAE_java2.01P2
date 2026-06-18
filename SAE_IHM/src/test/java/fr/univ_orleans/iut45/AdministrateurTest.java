package fr.univ_orleans.iut45;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import fr.univ_orleans.iut45.modele.BoiteComposee;
import fr.univ_orleans.iut45.modele.Categorie;
import fr.univ_orleans.iut45.modele.CollectionPersonnelle;
import fr.univ_orleans.iut45.modele.ContenuFigurine;
import fr.univ_orleans.iut45.modele.ContenuPiece;
import fr.univ_orleans.iut45.modele.Couleur;
import fr.univ_orleans.iut45.modele.Figurine;
import fr.univ_orleans.iut45.modele.PartieAdmin;
import fr.univ_orleans.iut45.modele.Piece;
import fr.univ_orleans.iut45.modele.Theme;

public class AdministrateurTest {

    private static final String FICHIER = "collection.json";
    private Couleur rouge;
    private Couleur bleu;
    private Categorie cat;
    private Theme theme;
    private Figurine fig;
    private Piece pieceA;
    private Piece pieceB;
    private CollectionPersonnelle col;
    private InputStream sysInOrig;

    @Before
    public void setup() {
        cat = new Categorie(1, "Base");
        rouge = new Couleur(1, "Rouge", "#FF0000", false);
        bleu = new Couleur(2, "Bleu", "#0000FF", false);
        theme = new Theme(1, "Ville");
        fig = new Figurine("F001", "Luke", 3);
        pieceA = new Piece("P001", "Brique", cat, rouge);
        pieceB = new Piece("P002", "Plaque", cat, bleu);
        col = new CollectionPersonnelle(null);
        sysInOrig = System.in;
    }

    @After
    public void cleanup() throws Exception {
        File f = new File(FICHIER);
        if (f.exists()) f.delete();
        System.setIn(sysInOrig);
    }

    @Test
    public void boitePersonnalisee() {
        BoiteComposee b = new BoiteComposee("B001", "Custom", 2024, 100, theme, true, true, true);
        assertTrue("Une boite construite avec personnalisee=true doit l'être", b.estPersonnalisee());
        assertTrue(b.estComplete());
        assertTrue(b.estDansCollection());
    }

    @Test
    public void boiteNonPersonnalisee() {
        BoiteComposee b = new BoiteComposee("B002", "Catalogue", 2024, 100, theme, true, true);
        assertFalse("Une boite du catalogue n'est pas personnalisée", b.estPersonnalisee());
    }

    @Test
    public void sauvegarderPersonnaliseeVide() {
        BoiteComposee b = new BoiteComposee("PV01", "Vide", 2024, 0, theme, false, true, true);
        col.ajouterBoite(b);
        col.sauvegarder();
        File f = new File(FICHIER);
        assertTrue("Le fichier de sauvegarde doit exister", f.exists());
    }

    @Test
    public void sauvegarderPersonnaliseeAvecContenu() {
        BoiteComposee b = new BoiteComposee("PV02", "Remplie", 2024, 500, theme, false, true, true);
        b.ajouterPiece(new ContenuPiece(pieceA, 2, false));
        b.ajouterPiece(new ContenuPiece(pieceB, 1, true));
        b.ajouterFigurine(new ContenuFigurine(fig, 1));
        col.ajouterBoite(b);
        col.sauvegarder();

        File f = new File(FICHIER);
        assertTrue(f.exists());
        assertTrue("Un fichier avec contenu doit être > 100 octets", f.length() > 100);
    }

    @Test
    public void sauvegarderNonPersonnaliseeAvecRetirees() throws Exception {
        BoiteComposee b = new BoiteComposee("NP01", "DuCatalogue", 2023, 500, theme, true, true);
        b.enregistrerPieceRetiree("P001", 1);
        b.enregistrerPieceRetiree("P002", 2);
        b.enregistrerFigurineRetiree("F001");
        col.ajouterBoite(b);
        col.sauvegarder();

        String json = new String(Files.readAllBytes(Paths.get(FICHIER)));
        assertTrue("Le JSON doit contenir piecesRetirees", json.contains("piecesRetirees"));
        assertTrue("Le JSON doit contenir figurinesRetirees", json.contains("figurinesRetirees"));
        assertTrue("Le JSON doit contenir la piece retiree P001", json.contains("P001"));
        assertTrue("Le JSON doit contenir la figurine retiree", json.contains("F001"));
    }

    @Test
    public void sauvegarderMultiplesBoites() throws Exception {
        BoiteComposee b1 = new BoiteComposee("M01", "Premiere", 2023, 100, theme, true, true, true);
        b1.ajouterPiece(new ContenuPiece(pieceA, 2, false));
        BoiteComposee b2 = new BoiteComposee("M02", "Seconde", 2024, 200, theme, false, true, true);
        b2.ajouterFigurine(new ContenuFigurine(fig, 1));
        col.ajouterBoite(b1);
        col.ajouterBoite(b2);
        col.sauvegarder();

        String json = new String(Files.readAllBytes(Paths.get(FICHIER)));
        assertTrue("JSON doit contenir M01", json.contains("M01"));
        assertTrue("JSON doit contenir M02", json.contains("M02"));
        assertTrue("JSON doit commencer par [", json.trim().startsWith("["));
        assertTrue("JSON doit finir par ]", json.trim().endsWith("]"));
    }

}