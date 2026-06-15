package fr.univ_orleans.iut45.modele;

import java.sql.SQLException;
import java.util.Scanner;

public class PartieAdmin {
    
    private ConnexionMySQL ConnexionMySQL;
    private Scanner scanner;
    private BoiteBD boiteBD;
    private PieceBD pieceBD;
    private CouleurBD couleurBD;
    private ThemeBD themeBD;
    private ContenuBD contenuBD;
    private ContenirPieceBD contenirPieceBD;
    private ContenirFigurineBD contenirFigurineBD;
    private ContenirBoiteBD contenirBoiteBD;
    private CollectionPersonnelle collection;
    private FigurineBD figurineBD;
    private MenuConsole menuConsole;

    public PartieAdmin(ConnexionMySQL ConnexionMySQL){
        this.boiteBD= new BoiteBD(ConnexionMySQL);
        this.pieceBD= new PieceBD(ConnexionMySQL);
        this.couleurBD= new CouleurBD(ConnexionMySQL);
        this.themeBD= new ThemeBD(ConnexionMySQL);
        this.contenuBD = new ContenuBD(ConnexionMySQL);
        this.contenirPieceBD = new ContenirPieceBD(ConnexionMySQL);
        this.contenirFigurineBD = new ContenirFigurineBD(ConnexionMySQL);
        this.contenirBoiteBD = new ContenirBoiteBD(ConnexionMySQL);
        this.collection = new CollectionPersonnelle(ConnexionMySQL);
        this.figurineBD= new FigurineBD(ConnexionMySQL);
        this.menuConsole = new MenuConsole();
        this.scanner = new Scanner(System.in);
    }

    public void ajouterBoite() throws SQLException {
        System.out.print("Numéro de la boîte : ");
        String num = scanner.nextLine();
        System.out.print("Nom de la boîte : ");
        String nomboite = scanner.nextLine();
        System.out.print("Année : ");
        int anneeInt;
        try { anneeInt = Integer.parseInt(scanner.nextLine()); }
        catch (NumberFormatException e) {
             System.out.println("Erreur : l'année doit être un entier."); 
             return; 
        }
        System.out.print("Nombre de pièces : ");
        int nbpiecesInt;
        try { nbpiecesInt = Integer.parseInt(scanner.nextLine()); }
        catch (NumberFormatException e) { 
            System.out.println("Erreur : le nombre de pièces doit être un entier."); 
            return; 
        }
        System.out.print("ID du thème : ");
        int idTheme;
        try { idTheme = Integer.parseInt(scanner.nextLine()); }
        catch (NumberFormatException e) { 
            System.out.println("Erreur : l'id du thème doit être un entier."); 
            return; 
        }
        boiteBD.ajouterBoite(num, nomboite, anneeInt, nbpiecesInt, idTheme);
    }

    public void supprimerBoite() throws SQLException {
        System.out.print("Numéro de la boîte à supprimer : ");
        String numBoite = scanner.nextLine();
        boiteBD.supprimerBoitePartout(numBoite);
        collection.retirerBoite(numBoite);
    }

    public void ajouterPiece() throws SQLException {
        System.out.print("Numéro de la pièce : ");
        String strPiece = scanner.nextLine();
        System.out.print("Nom de la pièce : ");
        String nomPiece = scanner.nextLine();
        System.out.print("Nom de la catégorie : ");
        String nomCat = scanner.nextLine();
        pieceBD.ajouterPiece(strPiece, nomPiece, nomCat);
    }

    public void supprimerPiece() throws SQLException {
        System.out.print("Numéro de la pièce à supprimer : ");
        String nomPiece = scanner.nextLine();
        pieceBD.supprimerPiece(nomPiece);
    }

    public void creerTheme() throws SQLException {
        System.out.print("ID du thème : ");
        int idTheme;
        try { 
            idTheme = Integer.parseInt(scanner.nextLine()); 
        }
        catch (NumberFormatException e) {
             System.out.println("Erreur : l'id du thème doit être un entier."); 
             return; 
            }
        System.out.print("Nom du thème : ");
        String nomTheme = scanner.nextLine();
        System.out.print("Nom du thème parent (laisser vide si aucun) : ");
        String themeParent = scanner.nextLine();
        themeBD.ajouterTheme(idTheme, nomTheme, themeParent);
    }

    public void majContenuBoite() throws SQLException{
        System.out.print("Numéro de la boite dont vous souhaitez modifier le contenu :");
        String strBoite = scanner.nextLine();
        if (!boiteBD.boiteEstDansBD(strBoite)) {
            System.out.println("Erreur : la boîte " + strBoite + " n'existe pas.");
            return;
        }

        System.out.print("Que voulez-vous faire ?\n 1 : Ajouter une pièce\n 2 : Ajouter une figurine\n 3 : Ajouter une boîte : ");
        String choix = scanner.nextLine();
        switch (choix) {    
            case "1" : ajouterPieceAContenuBoite(strBoite);break;
            case "2" : ajouterFigurineAContenuBoite(strBoite);break;
            case "3" : ajouterBoiteAContenuBoite(strBoite);break;
            default  : menuConsole.afficherErreur("Choix invalide.");break;
        }
    }

    public void ajouterPieceAContenuBoite(String numBoite) throws SQLException{
        System.out.println("Nom de la pièce à ajouter :");
        String nomPiece = scanner.nextLine();

        System.out.println("Couleur de la pièce :");
        String nomCoul = scanner.nextLine();

        System.out.println("La pièce est-elle en supplément ? (T : Vrai, F : Faux) :");
        String enSuppStr = scanner.nextLine();
        if (!enSuppStr.equals("T") && !enSuppStr.equals("F")) {
            System.out.println("Erreur : la réponse doit être T ou F.");
            return;
        }
        Integer quantitep = this.lireEntier("Quantité de cette pièce dans la boîte : ");
        if (quantitep == null) {
            return;
        }
        this.contenirPieceBD.ajouterPieceDansBoite(nomPiece, numBoite, nomCoul, enSuppStr, quantitep);
        }
    

    public void ajouterFigurineAContenuBoite(String numBoite) throws SQLException{
        System.out.println("Nom de la figurine à ajouter :");
        String nomFig = scanner.nextLine();
        Integer quantite = this.lireEntier("Quantité de cette figurine dans la boîte : ");
        if (quantite == null) {
            return;
        }
        this.contenirFigurineBD.ajouterFigurineDansBoite(nomFig, numBoite, quantite);
    }

    public void ajouterBoiteAContenuBoite(String numBoite) throws SQLException{
        System.out.println("Numéro de la boîte à ajouter :");
        String strNumBoiteIncluse = scanner.nextLine();
        this.contenirBoiteBD.ajouterBoiteDansBoite(numBoite, strNumBoiteIncluse);
    }


    public Integer lireEntier(String message) {
        System.out.print(message);
        String saisie = scanner.nextLine().trim();
        try {
            return Integer.parseInt(saisie);
        } catch (NumberFormatException e) {
            menuConsole.afficherErreur("Valeur invalide : \"" + saisie + "\" n'est pas un entier.");
            return null;
        }
    }
}
