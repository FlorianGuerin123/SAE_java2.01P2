import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class PartieCollection {

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

    public PartieCollection(ConnexionMySQL ConnexionMySQL, CollectionPersonnelle collection){ 
        this.boiteBD= new BoiteBD(ConnexionMySQL);
        this.pieceBD= new PieceBD(ConnexionMySQL);
        this.couleurBD= new CouleurBD(ConnexionMySQL);
        this.themeBD= new ThemeBD(ConnexionMySQL);
        this.contenuBD = new ContenuBD(ConnexionMySQL);
        this.contenirPieceBD = new ContenirPieceBD(ConnexionMySQL);
        this.contenirFigurineBD = new ContenirFigurineBD(ConnexionMySQL);
        this.contenirBoiteBD = new ContenirBoiteBD(ConnexionMySQL);
        this.collection = collection;
        this.figurineBD= new FigurineBD(ConnexionMySQL);
        this.menuConsole = new MenuConsole();
        this.scanner = new Scanner(System.in);
    }

    //  Sous-menu ajout collection 
    public void menuAjoutCollection() throws SQLException {
        menuConsole.afficherMenuAjoutCollection();
        String choix = scanner.nextLine().trim();
        switch (choix) {
            case "1" : ajouterBoiteExistante(); break;
            case "2" : composerBoitePersonnalisee();break;
            case "0" :break;
            default  : menuConsole.afficherErreur("Choix invalide."); break;
        }
    }

    //  1. Recherche par numéro
    public void rechercherBoiteParNumero() throws SQLException {
        System.out.print("Numéro de la boîte : ");
        String num = scanner.nextLine();
        BoiteSimple boite = boiteBD.rechercherBoite(num);
        if (boite != null) {
            System.out.println(boite);
        }
    }

    //  2. Recherche par nom
    public void rechercherBoiteParNom() throws SQLException {
        System.out.print("Nom de la boîte : ");
        String nom = scanner.nextLine();
        String num = boiteBD.getIdBoiteAvecNom(nom);
        BoiteSimple boite = boiteBD.rechercherBoite(num);
        if (boite != null) {
            System.out.println(boite);
        }
    }

    //  3. Afficher détail─
    public void afficherDetailBoite() throws SQLException {
        System.out.print("Numéro de la boîte : ");
        String num = scanner.nextLine();
        List<Piece> pieces = pieceBD.getPiecesBoite(num);
        List<Figurine> figurines = figurineBD.getFigurinesDansBoite(num);
        List<BoiteSimple> boitesIncluses = boiteBD.getBoitesIncluses(num);
        System.out.println("Pieces : \n");
        for (Piece p : pieces){
            System.out.println(p);
        }
        System.out.println("Figurines : \n");
        for (Figurine f : figurines) {
            System.out.println(f);
        }
        System.out.println("Boîtes incluses : \n");
        for (BoiteSimple b : boitesIncluses) {
            System.out.println(b);
        }
        if (!boiteBD.boiteEstDansBD(num)){
            menuConsole.afficherErreur("Aucune boîte trouvée avec ce numéro.");
        }
    }

    //  4. Rechercher par thème
    public void rechercherBoitesParTheme() throws SQLException {
        System.out.print("Nom du thème : ");
        String theme = scanner.nextLine();
        List<BoiteSimple> boites = boiteBD.getBoitesSelonTheme(theme);
        for (BoiteSimple b : boites) {
            System.out.println(b);
        }
        if (boites.isEmpty()){
            menuConsole.afficherErreur("Aucune boîte trouvée pour ce thème.");
        }
    }

    //  5. Statistiques
    public void afficherStatistiquesBoite() throws SQLException {
        System.out.print("Numéro de la boîte : ");
        String num = scanner.nextLine();
        if (!boiteBD.boiteEstDansBD(num)){
            menuConsole.afficherErreur("Aucune boîte trouvée avec ce numéro.");
            return;
        }
        List<Integer> stats = pieceBD.statsPieces(num);
        List<CouleurBD.StatCouleur> statsCouleurs = couleurBD.getStatsCouleurs(num);
        if (stats != null) {
            System.out.println("Nombre de types de pièces différentes : " + stats.get(0));
            System.out.println("Nombre total de pièces : " + stats.get(1));
        } else {
            menuConsole.afficherErreur("Aucune boîte trouvée avec ce numéro.");
        }
        for (CouleurBD.StatCouleur statCoul : statsCouleurs) {
            double pourcentage = ((double) statCoul.quantite() / statCoul.totalPiecesBoite()) * 100;
            System.out.println("La couleur " + statCoul.nomCouleur() + " représente " + Math.round(pourcentage) + "% des pièces.");
        }
    }

    //  6. Boîtes contenant une pièce 
    public void rechercherBoitesContenantPiece() throws SQLException {
        System.out.print("Numéro de la pièce : ");
        String numPiece = scanner.nextLine();
        List<BoiteSimple> boites = boiteBD.getBoitesPossedantPiece(numPiece);
        for (BoiteSimple b : boites) {
            System.out.println(b);
        }
        if (boites.isEmpty()) {
            menuConsole.afficherErreur("Aucune boîte trouvée contenant cette pièce.");
        }
    }

    //  8a. Ajouter une boîte existante 
    public void ajouterBoiteExistante() throws SQLException {
        System.out.print("Numéro de la boîte à ajouter : ");
        String numBoite = scanner.nextLine().trim();
        BoiteComposee boite = boiteBD.getBoiteComplete(numBoite, true, true);
        if (boite == null) {
            menuConsole.afficherErreur("Boîte introuvable en base de données.");
            return;
        }
        collection.ajouterBoite(boite);
    }

    //  8b. Composer une boîte personnalisée ─
    public void composerBoitePersonnalisee() throws SQLException {
        System.out.println("\n Composer une boîte personnalisée ─");
        System.out.print("Nom de votre boîte : ");
        String nom = scanner.nextLine().trim();
        Random rand = new Random(); 
        int randomNum = rand.nextInt(100000);
        String numBoite = "PERSO-" + randomNum; 
        System.out.print("Année : ");
        int annee;
        try { annee = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { menuConsole.afficherErreur("Année invalide."); return; }
        System.out.print("ID du thème : ");
        int idTheme;
        try { idTheme = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { menuConsole.afficherErreur("ID invalide."); return; }
        System.out.print("Nom du thème : ");
        String nomTheme = scanner.nextLine().trim();

        Theme theme = new Theme(idTheme, nomTheme);
        BoiteComposee boite = new BoiteComposee(numBoite, nom, annee, 0, theme, true, true, true);

        boolean continuer = true;
        while (continuer){
            System.out.println("\n  1. Ajouter une pièce");
            System.out.println("  2. Ajouter une figurine");
            System.out.println("  0. Terminer");
            System.out.print("Choix : ");
            String choix = scanner.nextLine().trim();
            switch (choix) {
                case "1" :ajouterPieceABoitePerso(boite); break;
                case "2" :ajouterFigurineABoitePerso(boite);break;
                case "0" :continuer = false;break;
                default  :menuConsole.afficherErreur("Choix invalide."); break;
            }
        }
        collection.ajouterBoite(boite);
        System.out.println("Boîte \"" + nom + "\" créée avec " + boite.getPieces().size() + " type(s) de pièce(s).");
    }
    public void ajouterPieceABoitePerso(BoiteComposee boite) {
        System.out.print("Numéro de la pièce à prendre : ");
        String numPiece = scanner.nextLine().trim();
    
        List<BoiteComposee> boitesSources = new ArrayList<>();
        for (BoiteComposee b : collection.getBoites()) {
            if (b == boite) continue;
            for (ContenuPiece cp : b.getPieces()) {
                if (cp.getPiece().obtenirNumPiece().equals(numPiece)) {
                    boitesSources.add(b);
                    break;
                }
            }
        }
    
        if (boitesSources.isEmpty()) {
            menuConsole.afficherErreur("Cette pièce n'est présente dans aucune boite de votre collection.");
            return;
        }
        System.out.println("\nBoites contenant cette pièce dans votre collection :");
        for (int i = 0; i < boitesSources.size(); i++) {
            BoiteComposee src = boitesSources.get(i);
            for (ContenuPiece cp : src.getPieces()) {
                if (cp.getPiece().obtenirNumPiece().equals(numPiece)){
                    System.out.println("  "+(i + 1) + ". ["+src.getNumBoite()+ "] "+ src.getNomBoite()+" – "+cp.getQuantite() +" exemplaire(s)"+ " – couleur : " + cp.getPiece().obtenirCouleur().getNomCouleur());
                    break;
                }
            }
        }
    
        
        System.out.print("Choisir la boite source (numéro) : ");
        int choix;
        try { choix = Integer.parseInt(scanner.nextLine().trim()) - 1; }
        catch (NumberFormatException e) { 
            menuConsole.afficherErreur("Choix invalide"); 
            return; 
        }
        if (choix < 0 || choix >= boitesSources.size()) {
            menuConsole.afficherErreur("Choix invalide");
            return;
        }
    
        BoiteComposee boiteSource = boitesSources.get(choix);
    
        ContenuPiece cpSource = null;
        for (ContenuPiece cp : boiteSource.getPieces()) {
            if (cp.getPiece().obtenirNumPiece().equals(numPiece)) {
                cpSource = cp;
                break;
            }
        }
        int nouvelleQuantite = cpSource.getQuantite() - 1;
        boiteSource.getPieces().remove(cpSource);
        if (nouvelleQuantite > 0) {
            boiteSource.getPieces().add(new ContenuPiece(cpSource.getPiece(), nouvelleQuantite, cpSource.estEnSupplement()));
            System.out.println("1 exemplaire retiré de [" + boiteSource.getNomBoite() + "] – reste " + nouvelleQuantite );
        } else {
            System.out.println("Dernier exemplaire pris,pièce retirée de la boite source");
        }
        if (!boiteSource.estPersonnalisee()) {
            boiteSource.enregistrerPieceRetiree(cpSource.getPiece().obtenirNumPiece(),cpSource.getPiece().obtenirCouleur().getIdCouleur());
        }
        boiteSource.setComplete(false);
        boolean dejaPresente = false;
        for (ContenuPiece cp : boite.getPieces()) {
            if (cp.getPiece().obtenirNumPiece().equals(numPiece)) {
                boite.getPieces().remove(cp);
                boite.getPieces().add(new ContenuPiece(cp.getPiece(), cp.getQuantite() + 1, cp.estEnSupplement()));
                dejaPresente = true;
                break;
            }
        }
        if (!dejaPresente) {
            boite.getPieces().add(new ContenuPiece(cpSource.getPiece(), 1, false));
            boite.incrementerNbPieces();
        }

        System.out.println("Pièce ajoutée à votre boite personnalisée");
    }


    public void ajouterFigurineABoitePerso(BoiteComposee boite) {
        System.out.print("ID de la figurine à prendre : ");
        String idFig = scanner.nextLine().trim();
    
        List<BoiteComposee> boitesSources = new ArrayList<>();
        for (BoiteComposee b : collection.getBoites()) {
            if (b == boite) continue;
            for (ContenuFigurine cf : b.getFigurines()) {
                if (cf.getFigurine().getIdFigurine().equals(idFig)) {
                    boitesSources.add(b);
                    break;
                }
            }
        }
        if (boitesSources.isEmpty()) {
            menuConsole.afficherErreur("Cette figurine n'est présente dans aucune boite de votre collection");
            return;
        }
        System.out.println("\nBoites contenant cette figurine dans votre collection :");
        for (int i = 0; i < boitesSources.size(); i++) {
            BoiteComposee src = boitesSources.get(i);
            for (ContenuFigurine cf : src.getFigurines()) {
                if (cf.getFigurine().getIdFigurine().equals(idFig)) {
                    System.out.println("  " + (i + 1) + ". [" + src.getNumBoite() + "] "+ src.getNomBoite() + " – " + cf.getQuantite() + " exemplaire(s)"+ " – " + cf.getFigurine().getNomFigurine());
                    break;
                }
            }
        }
        System.out.print("Choisir la boite source (numéro) : ");
        int choix;
        try { choix = Integer.parseInt(scanner.nextLine().trim()) - 1; }
        catch (NumberFormatException e) { 
            menuConsole.afficherErreur("Choix invalide"); 
            return; 
        }
        if (choix < 0 || choix >= boitesSources.size()) {
            menuConsole.afficherErreur("Choix invalide");
            return;
        }
    
        BoiteComposee boiteSource = boitesSources.get(choix);
    
        
        ContenuFigurine cfSource = null;
        for (ContenuFigurine cf : boiteSource.getFigurines()) {
            if (cf.getFigurine().getIdFigurine().equals(idFig)) {
                cfSource = cf;
                break;
            }
        }
        int nouvelleQuantite = cfSource.getQuantite() - 1;
        boiteSource.getFigurines().remove(cfSource);
        if (nouvelleQuantite > 0) {
            boiteSource.getFigurines().add(new ContenuFigurine(cfSource.getFigurine(), nouvelleQuantite));
            System.out.println("1 exemplaire retiré de [" + boiteSource.getNomBoite() + "] – reste " + nouvelleQuantite);
        } else {
            System.out.println("Dernier exemplaire pris figurine retirée de la boite source.");
        }

        if (!boiteSource.estPersonnalisee()) {
            boiteSource.enregistrerFigurineRetiree(idFig);
        }
    
        boiteSource.setComplete(false);
    
        boolean dejaPresente = false;
        for (ContenuFigurine cf : boite.getFigurines()) {
            if (cf.getFigurine().getIdFigurine().equals(idFig)) {
                boite.getFigurines().remove(cf);
                boite.getFigurines().add(new ContenuFigurine(cf.getFigurine(), cf.getQuantite() + 1));
                dejaPresente = true;
                break;
            }
        }
        if (!dejaPresente) {
            boite.getFigurines().add(new ContenuFigurine(cfSource.getFigurine(), 1));
        }
    
        System.out.println("Figurine ajoutée à votre boite personnalisée.");
    }


    //  9. Retirer une boîte
    public void retirerBoiteDeLaCollection() {
        if (collection.getBoites().isEmpty()) {
            menuConsole.afficherErreur("Votre collection est vide.");
            return;
        }
        collection.afficher();
        System.out.print("Numéro de la boîte à retirer : ");
        String numBoite = scanner.nextLine().trim();
        System.out.print("Confirmer ? (o/n) : ");
        if (scanner.nextLine().trim().equalsIgnoreCase("o")) {
            collection.retirerBoite(numBoite);
        } else {
            System.out.println("Annulé.");
        }
    }

    // -- 10. Dire les pieces et figurine manquante des boite de la collection --
    

    public void afficherPiecesManquantes() {
        if (collection.getBoites().isEmpty()) {
            menuConsole.afficherErreur("Votre collection est vide.");
            return;
        }

        System.out.println("\nPièces et figurines manquantes par boite ");

        boolean auMoinsUneIncomplete = false;

        for (BoiteComposee b : collection.getBoites()) {
            if (b.estComplete()) continue; 

            auMoinsUneIncomplete = true;
            System.out.println("\n  [" + b.getNumBoite() + "] " + b.getNomBoite() + (b.estPersonnalisee() ? " [PERSO]" : ""));

            if (!b.estPersonnalisee() && !b.getPiecesRetirees().isEmpty()) {
                System.out.println("  Pièces retirées :");
                for (BoiteComposee.PieceRetiree pr : b.getPiecesRetirees()) {
                    System.out.println("  - pièce " + pr.numPiece+ " (couleur id:" + pr.idCoul + ")"+ " * " + pr.quantiteRetiree);
                }
            }

            
            if (!b.estPersonnalisee() && !b.getFigurinesRetirees().isEmpty()) {
                System.out.println(" Figurines retirées :");
                for (BoiteComposee.FigurineRetiree fr : b.getFigurinesRetirees()) {
                    System.out.println("  - figurine " + fr.idFig
                        + " * " + fr.quantiteRetiree);
                }
            }

            
            if (b.estPersonnalisee()) {
                if (b.getPieces().isEmpty() && b.getFigurines().isEmpty()) {
                    System.out.println(" Aucune pièce ni figurine.");
                } else {
                    if (!b.getPieces().isEmpty()) {
                        System.out.println(" Pièces présentes :");
                        for (ContenuPiece cp : b.getPieces()) {
                            System.out.println("  - " + cp.getPiece().obtenirNumPiece()+ " " + cp.getPiece().obtenirNomPiece() + " (" + cp.getPiece().obtenirCouleur().getNomCouleur() + ")" + " * " + cp.getQuantite());
                        }
                    }
                    if (!b.getFigurines().isEmpty()) {
                        System.out.println("  Figurines présentes :");
                        for (ContenuFigurine cf : b.getFigurines()) {
                            System.out.println("  - " + cf.getFigurine().getIdFigurine()
                                + " " + cf.getFigurine().getNomFigurine()
                                + " * " + cf.getQuantite());
                        }
                    }
                }
            }

            
            if (!b.estPersonnalisee()
                    && b.getPiecesRetirees().isEmpty()
                    && b.getFigurinesRetirees().isEmpty()) {
                System.out.println(" Marquée incomplète mais aucune pièce retirée enregistrée.");
            }

            System.out.println(" ─");
        }

        if (!auMoinsUneIncomplete) {
            System.out.println("  Toutes vos boites sont complètes");
        }
        System.out.println();
    }
}
