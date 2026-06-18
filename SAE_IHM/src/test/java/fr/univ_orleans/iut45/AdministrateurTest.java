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

    @Test
    public void chargerPersonnalisee() {
        BoiteComposee b = new BoiteComposee("PC01", "ARelier", 2023, 200, theme, true, true, true);
        b.ajouterPiece(new ContenuPiece(pieceA, 3, false));
        b.ajouterFigurine(new ContenuFigurine(fig, 2));
        col.ajouterBoite(b);
        col.sauvegarder();
        CollectionPersonnelle col2 = new CollectionPersonnelle(null);
        col2.charger();
        assertEquals("Doit contenir 1 boite après chargement", 1, col2.getBoites().size());
        BoiteComposee loaded = col2.getBoites().get(0);
        assertEquals("PC01", loaded.getNumBoite());
        assertTrue(loaded.estPersonnalisee());
        assertEquals(1, loaded.getPieces().size());
        assertEquals("Brique", loaded.getPieces().get(0).getPiece().obtenirNomPiece());
        assertEquals(3, loaded.getPieces().get(0).getQuantite());
        assertEquals(1, loaded.getFigurines().size());
        assertEquals("Luke", loaded.getFigurines().get(0).getFigurine().getNomFigurine());
        assertEquals(2, loaded.getFigurines().get(0).getQuantite());
    }

    @Test
    public void chargerDeuxPersonnalisees() {
        BoiteComposee b1 = new BoiteComposee("C01", "Alpha", 2023, 100, theme, true, true, true);
        b1.ajouterPiece(new ContenuPiece(pieceA, 1, false));
        BoiteComposee b2 = new BoiteComposee("C02", "Beta", 2024, 200, theme, false, true, true);
        b2.ajouterFigurine(new ContenuFigurine(fig, 1));
        col.ajouterBoite(b1);
        col.ajouterBoite(b2);
        col.sauvegarder();
        CollectionPersonnelle col2 = new CollectionPersonnelle(null);
        col2.charger();
        assertEquals("Doit contenir 2 boites", 2, col2.getBoites().size());
        assertTrue(col2.contientBoite("C01"));
        assertTrue(col2.contientBoite("C02"));
    }

    @Test
    public void chargerFichierInexistant() {
        new File(FICHIER).delete();
        CollectionPersonnelle col2 = new CollectionPersonnelle(null);
        col2.charger();
        assertTrue("Collection doit rester vide", col2.getBoites().isEmpty());
    }

    @Test
    public void lireEntierValide() {
        System.setIn(new ByteArrayInputStream("42\n".getBytes()));
        PartieAdmin admin = new PartieAdmin(null);
        assertEquals("42 doit être parsé en 42", Integer.valueOf(42), admin.lireEntier("test: "));
    }

    @Test
    public void lireEntierInvalide() {
        System.setIn(new ByteArrayInputStream("abc\n".getBytes()));
        PartieAdmin admin = new PartieAdmin(null);
        assertNull("'abc' doit retourner null", admin.lireEntier("test: "));
    }

    @Test
    public void lireEntierNegatif() {
        System.setIn(new ByteArrayInputStream("-5\n".getBytes()));
        PartieAdmin admin = new PartieAdmin(null);
        assertEquals("Un entier négatif doit être accepté", Integer.valueOf(-5), admin.lireEntier("test: "));
    }

    @Test
    public void lireEntierZero() {
        System.setIn(new ByteArrayInputStream("0\n".getBytes()));
        PartieAdmin admin = new PartieAdmin(null);
        assertEquals(Integer.valueOf(0), admin.lireEntier("test: "));
    }

    @Test
    public void lireEntierEspaces() {
        System.setIn(new ByteArrayInputStream("  99  \n".getBytes()));
        PartieAdmin admin = new PartieAdmin(null);
        assertEquals("Les espaces autour doivent être ignorés (trim)",
                Integer.valueOf(99), admin.lireEntier("test: "));
    }

    @Test
    public void retirerBoiteSpecifique() {
        BoiteComposee b1 = new BoiteComposee("R01", "A", 2020, 100, theme, false, true);
        BoiteComposee b2 = new BoiteComposee("R02", "B", 2020, 100, theme, false, true);
        BoiteComposee b3 = new BoiteComposee("R03", "C", 2020, 100, theme, false, true);
        col.ajouterBoite(b1);
        col.ajouterBoite(b2);
        col.ajouterBoite(b3);
        col.retirerBoite("R02");

        assertEquals(2, col.getBoites().size());
        assertFalse(col.contientBoite("R02"));
        assertTrue(col.contientBoite("R01"));
        assertTrue(col.contientBoite("R03"));
    }

    @Test
    public void retirerBoiteUnique() {
        BoiteComposee b = new BoiteComposee("UNIQ", "Seule", 2020, 50, theme, false, true);
        col.ajouterBoite(b);
        col.retirerBoite("UNIQ");
        assertTrue("Collection doit être vide après retrait de l'unique boite", col.getBoites().isEmpty());
    }

    @Test
    public void marquerCompleteIncomplete() {
        BoiteComposee b = new BoiteComposee("MC01", "Test", 2023, 500, theme, false, true);
        col.ajouterBoite(b);
        assertFalse(b.estComplete());
        col.marquerComplete("MC01", true);
        assertTrue("Boite doit être complète", b.estComplete());
        col.marquerComplete("MC01", false);
        assertFalse("Boite doit être incomplète", b.estComplete());
    }

    @Test
    public void ajouterDoublonRefuse() {
        BoiteComposee b1 = new BoiteComposee("DUP", "A", 2020, 100, theme, false, true);
        BoiteComposee b2 = new BoiteComposee("DUP", "B", 2020, 100, theme, false, true);
        assertTrue("Premier ajout OK", col.ajouterBoite(b1));
        assertFalse("Doublon refusé", col.ajouterBoite(b2));
        assertEquals("Un seul élément dans la collection", 1, col.getBoites().size());
    }

}