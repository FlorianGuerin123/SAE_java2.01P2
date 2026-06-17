package fr.univ_orleans.iut45;

import java.io.File;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import fr.univ_orleans.iut45.modele.BoiteComposee;
import fr.univ_orleans.iut45.modele.BoiteSimple;
import fr.univ_orleans.iut45.modele.Categorie;
import fr.univ_orleans.iut45.modele.CollectionPersonnelle;
import fr.univ_orleans.iut45.modele.ContenuBoite;
import fr.univ_orleans.iut45.modele.ContenuFigurine;
import fr.univ_orleans.iut45.modele.ContenuPiece;
import fr.univ_orleans.iut45.modele.Couleur;
import fr.univ_orleans.iut45.modele.Figurine;
import fr.univ_orleans.iut45.modele.Piece;
import fr.univ_orleans.iut45.modele.Theme;

public class CollectionneurTest {

    private Piece pieceA, pieceB;
    private Categorie cat;
    private Couleur rouge, bleu;
    private Theme theme;
    private Figurine fig;
    private BoiteSimple simple;
    private BoiteComposee composee;

    @Before
    public void setup() {
        cat = new Categorie(1, "Base");
        rouge = new Couleur(1, "Rouge", "#FF0000", false);
        bleu = new Couleur(2, "Bleu", "#0000FF", false);
        theme = new Theme(1, "Ville");
        fig = new Figurine("F001", "Luke", 3);
        pieceA = new Piece("P001", "Brique", cat, rouge);
        pieceB = new Piece("P002", "Plaque", cat, bleu);
        simple = new BoiteSimple("B001", "Caserne", 2023, 500, theme, true);
        composee = new BoiteComposee("BC001", "Château", 2022, 1500, new Theme(2, "Château"), true, true);
    }

    @After
    public void cleanup() {
        File f = new File("collection.json");
        if (f.exists()) f.delete();
    }

    @Test
    public void pieceEgaleMemeNumero() {
        Piece clone = new Piece("P001", "Autre", cat, bleu);
        assertTrue(pieceA.equals(clone));
    }

    @Test
    public void pieceEgaleNumeroDifferent() {
        assertFalse(pieceA.equals(pieceB));
    }

    @Test
    public void pieceEgaleNull() {
        assertFalse(pieceA.equals(null));
    }

    @Test
    public void pieceHashCodeEstLeNumero() {
        assertEquals("P001".hashCode(), pieceA.hashCode());
    }

    @Test
    public void themeAvecParent() {
        Theme enfant = new Theme(2, "Caserne", theme);
        assertEquals(theme, enfant.getParent());
        assertTrue(theme.getSousThemes().contains(enfant));
    }

    @Test
    public void themeAjoutSousTheme() {
        Theme enfant = new Theme(2, "Pompier");
        theme.ajouterSousTheme(enfant);
        assertEquals(1, theme.getSousThemes().size());
    }

    @Test
    public void themePasDeDoublonSousTheme() {
        Theme enfant = new Theme(2, "Pompier");
        theme.ajouterSousTheme(enfant);
        theme.ajouterSousTheme(enfant);
        assertEquals(1, theme.getSousThemes().size());
    }

    @Test
    public void boiteEgaleMemeNumero() {
        BoiteSimple clone = new BoiteSimple("B001", "Autre", 2020, 100, theme, false);
        assertTrue(simple.equals(clone));
    }

    @Test
    public void boiteEgaleNumeroDifferent() {
        BoiteSimple autre = new BoiteSimple("B002", "Caserne", 2023, 500, theme, true);
        assertFalse(simple.equals(autre));
    }

    @Test
    public void boiteAjouterPiece() {
        simple.ajouterPiece(new ContenuPiece(pieceA, 2, false));
        simple.ajouterPiece(new ContenuPiece(pieceB, 1, true));
        assertEquals(2, simple.getPieces().size());
    }

    @Test
    public void boiteAjouterFigurineEtSousBoite() {
        simple.ajouterFigurine(new ContenuFigurine(fig, 1));
        BoiteSimple mini = new BoiteSimple("SUB01", "Mini", 2024, 50, theme, false);
        simple.ajouterSousBoite(new ContenuBoite(mini, 2));
        assertEquals(1, simple.getFigurines().size());
        assertEquals(1, simple.getSousBoites().size());
    }

    @Test
    public void boiteIncrementerNbPieces() {
        int avant = simple.getNbPieces();
        simple.incrementerNbPieces();
        assertEquals(avant + 1, simple.getNbPieces());
    }

    @Test
    public void simpleSetComplete() {
        simple.setComplete(false);
        assertFalse(simple.estComplete());
        simple.setComplete(true);
        assertTrue(simple.estComplete());
    }

    @Test
    public void composeeSetCompleteEtCollection() {
        composee.setComplete(false);
        composee.setCollection(false);
        assertFalse(composee.estComplete());
        assertFalse(composee.estDansCollection());
    }

    @Test
    public void composeeEnregistrerPieceRetireeNouvelle() {
        composee.enregistrerPieceRetiree("P001", 1);
        assertEquals(1, composee.getPiecesRetirees().size());
        assertEquals(1, composee.getPiecesRetirees().get(0).quantiteRetiree);
    }

    @Test
    public void composeeEnregistrerPieceRetireeIncrement() {
        composee.enregistrerPieceRetiree("P001", 1);
        composee.enregistrerPieceRetiree("P001", 1);
        assertEquals(1, composee.getPiecesRetirees().size());
        assertEquals(2, composee.getPiecesRetirees().get(0).quantiteRetiree);
    }

    @Test
    public void composeeEnregistrerPieceRetireeCouleurDifferent() {
        composee.enregistrerPieceRetiree("P001", 1);
        composee.enregistrerPieceRetiree("P001", 2);
        assertEquals(2, composee.getPiecesRetirees().size());
    }

    @Test
    public void composeeEnregistrerFigurineRetiree() {
        composee.enregistrerFigurineRetiree("F001");
        composee.enregistrerFigurineRetiree("F001");
        assertEquals(1, composee.getFigurinesRetirees().size());
        assertEquals(2, composee.getFigurinesRetirees().get(0).quantiteRetiree);
    }

    @Test
    public void collectionAjouterBoite() {
        CollectionPersonnelle col = new CollectionPersonnelle(null);
        assertTrue(col.ajouterBoite(composee));
        assertEquals(1, col.getBoites().size());
    }

    @Test
    public void collectionRefuseDoublon() {
        CollectionPersonnelle col = new CollectionPersonnelle(null);
        col.ajouterBoite(composee);
        assertFalse(col.ajouterBoite(composee));
        assertEquals(1, col.getBoites().size());
    }

    @Test
    public void collectionContientBoite() {
        CollectionPersonnelle col = new CollectionPersonnelle(null);
        col.ajouterBoite(composee);
        assertTrue(col.contientBoite("BC001"));
        assertFalse(col.contientBoite("INEXISTANTE"));
    }

    @Test
    public void collectionRetirerBoite() {
        CollectionPersonnelle col = new CollectionPersonnelle(null);
        col.ajouterBoite(composee);
        col.retirerBoite("BC001");
        assertTrue(col.getBoites().isEmpty());
    }

    @Test
    public void collectionRetirerInexistante() {
        CollectionPersonnelle col = new CollectionPersonnelle(null);
        col.ajouterBoite(composee);
        col.retirerBoite("INEXISTANTE");
        assertEquals(1, col.getBoites().size());
    }

    @Test
    public void collectionMarquerComplete() {
        CollectionPersonnelle col = new CollectionPersonnelle(null);
        BoiteComposee b = new BoiteComposee("B001", "Test", 2023, 100, theme, false, true);
        col.ajouterBoite(b);
        col.marquerComplete("B001", true);
        assertTrue(b.estComplete());
        col.marquerComplete("B001", false);
        assertFalse(b.estComplete());
    }

    @Test
    public void collectionSauvegarder() {
        CollectionPersonnelle col = new CollectionPersonnelle(null);
        BoiteComposee b = new BoiteComposee("B001", "Test", 2023, 100, theme, true, true, true);
        b.ajouterPiece(new ContenuPiece(pieceA, 2, false));
        b.ajouterFigurine(new ContenuFigurine(fig, 1));
        col.ajouterBoite(b);
        col.sauvegarder();
        File f = new File("collection.json");
        assertTrue(f.exists());
        assertTrue(f.length() > 0);
    }

    @Test
    public void pieceRetireeChamps() {
        composee.enregistrerPieceRetiree("P001", 1);
        BoiteComposee.PieceRetiree pr = composee.getPiecesRetirees().get(0);
        assertEquals("P001", pr.numPiece);
        assertEquals(1, pr.idCoul);
        assertEquals(1, pr.quantiteRetiree);
    }

    @Test
    public void figurineRetireeChamps() {
        composee.enregistrerFigurineRetiree("F001");
        BoiteComposee.FigurineRetiree fr = composee.getFigurinesRetirees().get(0);
        assertEquals("F001", fr.idFig);
        assertEquals(1, fr.quantiteRetiree);
    }
}
