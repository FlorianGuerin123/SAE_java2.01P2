package fr.univ_orleans.iut45.modele;

import java.sql.SQLException;
import java.util.Scanner;

public class Application{
    private ConnexionMySQL ConnexionMySQL;
    private Scanner scanner;
    private CollectionPersonnelle collection;
    private MenuConsole menuConsole;
    private PartieCollection partieCollection;
    private PartieAdmin partieAdmin;

    public static void main(String[] args) {
        new Application().start();
    }

    public Application(){
        this.scanner = new Scanner(System.in);
        try {
            this.ConnexionMySQL = new ConnexionMySQL();
        } catch (ClassNotFoundException e) {
            System.out.println("Driver MySQL non trouvé!!!");
            System.exit(1);
        }
        this.collection = new CollectionPersonnelle(ConnexionMySQL);
        this.menuConsole = new MenuConsole();
        this.partieCollection = new PartieCollection(ConnexionMySQL, collection);
        this.partieAdmin = new PartieAdmin(ConnexionMySQL);
        

    }

    private void start() {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║       Briqu'IUTO – Gestionnaire LEGO             ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        seConnecter();
        collection.charger();

        boolean continuer = true;
        while (continuer) {
            menuConsole.afficherMenuPrincipal();
            String choix = scanner.nextLine().trim();
            switch (choix){
                case "1" : menuCollectionneur();break;
                case "2" : menuAdministrateur();break;
                case "0" : continuer = false;break;
                default  : menuConsole.afficherErreur("Choix invalide.");break;
            }
        }

        collection.sauvegarder();
        try { ConnexionMySQL.close(); } catch (SQLException ignored) {

        }
        System.out.println("\nAu revoir !");
        scanner.close();
        System.exit(0);
    }

    // ═══════════════════════════════════════════════════════════
    // CONNEXION
    // ═══════════════════════════════════════════════════════════
    private void seConnecter() {
        menuConsole.afficherTitre("Connexion à la base de données");
        System.out.print("Login : ");
        String login = scanner.nextLine().trim();
        String mdp;
        java.io.Console console = System.console();
        if (console != null) {
            char[] mdpchar = console.readPassword("Mot de passe : ");
            mdp = new String(mdpchar).trim();
        } else {
            System.out.print("Mot de passe : ");
            mdp = scanner.nextLine().trim();
        }
        System.out.print("Nom de la base de données : ");
        String nomBase = scanner.nextLine().trim();
        try {
            ConnexionMySQL.connecter("servinfo-maria", nomBase, login, mdp);
            System.out.println("Connexion réussie !");
        } 
        catch (SQLException ex) {
            menuConsole.afficherErreur("Connexion échouée : " + ex.getMessage());
            System.out.print("Réessayer ? (o/n) : ");
            if (scanner.nextLine().trim().equalsIgnoreCase("o")) {
                seConnecter();
            } else {
                System.exit(0);
            }
        }
    }

    private void menuCollectionneur() {
        boolean continuer = true;
        while (continuer) {
            menuConsole.afficherMenuCollectionneur();
            String choix = scanner.nextLine().trim();
            try {
                switch (choix) {
                    case "1" : partieCollection.rechercherBoiteParNumero();break;
                    case "2" : partieCollection.rechercherBoiteParNom();break;
                    case "3" : partieCollection.afficherDetailBoite();break;
                    case "4" : partieCollection.rechercherBoitesParTheme();break;
                    case "5" : partieCollection.afficherStatistiquesBoite();break;
                    case "6" : partieCollection.rechercherBoitesContenantPiece();break;
                    case "7" : collection.afficher();break;
                    case "8" : partieCollection.menuAjoutCollection();break;
                    case "9" : partieCollection.retirerBoiteDeLaCollection();break;
                    case "10": partieCollection.afficherPiecesManquantes ();break;
                    case "0" : continuer = false;break;
                    default  : menuConsole.afficherErreur("Choix invalide."); break;
                }
            } catch (SQLException ex) {
                menuConsole.afficherErreur("Erreur base de données : " + ex.getMessage());
            }
        }
    }

    private void menuAdministrateur() {
        boolean continuer = true;
        while (continuer) {
            menuConsole.afficherMenuAdministrateur();
            String choix = scanner.nextLine().trim();
            try {
                switch (choix) {
                    case "1" : partieAdmin.ajouterBoite(); break;
                    case "2" : partieAdmin.supprimerBoite(); break;
                    case "3" : partieAdmin.majContenuBoite();break;
                    case "4" : partieAdmin.ajouterPiece(); break;
                    case "5" : partieAdmin.supprimerPiece(); break;
                    case "6" : partieAdmin.creerTheme(); break;
                    case "0" : continuer = false; break;
                    default  : menuConsole.afficherErreur("Choix invalide."); break;
                }
            } catch (SQLException ex) {
                menuConsole.afficherErreur("Erreur base de données : " + ex.getMessage());
            }
        }
    }
}