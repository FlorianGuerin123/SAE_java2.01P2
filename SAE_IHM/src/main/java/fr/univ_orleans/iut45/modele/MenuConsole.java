public class MenuConsole {

    private  String SEPARATEUR = "-".repeat(55);
    private String SEPARATEUR_EPAIS = "=".repeat(55);

    public void afficherTitre(String titre) {
        System.out.println("\n" + SEPARATEUR_EPAIS);
        System.out.println("  " + titre.toUpperCase());
        System.out.println(this.SEPARATEUR_EPAIS);
    }

    public void afficherSeparateur() {
        System.out.println(SEPARATEUR);
    }

    public void afficherMenuPrincipal() {
        afficherTitre("Briqu'IUTO – Gestionnaire de collection LEGO");
        System.out.println("  1. Espace collectionneur");
        System.out.println("  2. Espace administrateur");
        System.out.println("  0. Quitter");
        afficherSeparateur();
        System.out.print("Votre choix : ");
    }

    public void afficherMenuCollectionneur() {
        afficherTitre("Espace collectionneur");
        System.out.println("  ── Catalogue ──────────────────────────────────");
        System.out.println("  1. Rechercher une boîte par numéro");
        System.out.println("  2. Rechercher une boîte par nom");
        System.out.println("  3. Afficher le détail d'une boîte");
        System.out.println("  4. Rechercher les boîtes par thème");
        System.out.println("  5. Afficher les statistiques d'une boîte");
        System.out.println("  6. Rechercher les boîtes contenant une pièce");
        System.out.println("  ── Ma collection ──────────────────────────────");
        System.out.println("  7. Afficher ma collection");
        System.out.println("  8. Ajouter une boîte à ma collection");
        System.out.println("  9. Retirer une boîte de ma collection");
        System.out.println("  10. Afficher les pièces et figurines manquantes par boîte");
        System.out.println("  ───────────────────────────────────────────────");
        System.out.println("  0. Retour");
        afficherSeparateur();
        System.out.print("Votre choix : ");
    }

    public void afficherMenuAjoutCollection() {
        afficherTitre("Ajouter une boîte à ma collection");
        System.out.println("  1. Ajouter une boîte existante (depuis le catalogue)");
        System.out.println("  2. Composer une boîte personnalisée");
        System.out.println("  0. Annuler");
        afficherSeparateur();
        System.out.print("Votre choix : ");
    }

    public void afficherMenuAdministrateur() {
        afficherTitre("Espace administrateur");
        System.out.println("  1. Ajouter une boîte");
        System.out.println("  2. Supprimer une boîte");
        System.out.println("  3. Mettre à jour le contenu d'une boîte");
        System.out.println("  4. Ajouter une pièce");
        System.out.println("  5. Supprimer une pièce");
        System.out.println("  6. Créer un thème / sous-thème");
        System.out.println("  0. Retour");
        afficherSeparateur();
        System.out.print("Votre choix : ");
    }

    public void afficherErreur(String message) {
        System.out.println("ERREUR : " + message);
    }
}