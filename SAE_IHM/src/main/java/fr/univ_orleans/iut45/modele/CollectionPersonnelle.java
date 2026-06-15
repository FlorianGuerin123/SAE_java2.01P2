import com.google.gson.*;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CollectionPersonnelle {

    private static final String FICHIER_COLLECTION = "collection.json";

    private List<BoiteComposee> boites = new ArrayList<>();
    private final ConnexionMySQL connexion;

    public CollectionPersonnelle(ConnexionMySQL connexion){
        this.connexion = connexion;
    }

    public List<BoiteComposee> getBoites() { 
        return boites; 
    }

    public void ajouterBoite(BoiteComposee boite){
        if (!boites.contains(boite)) {
            boites.add(boite);
            System.out.println("Boîte " + boite.getNumBoite() + " ajoutée à la collection.");
        } else {
            System.out.println("Cette boîte est déjà dans la collection.");
        }
    }

    public void retirerBoite(String numBoite) {
        boolean ok = boites.removeIf(b -> b.getNumBoite().equals(numBoite));
        System.out.println(ok ? "Boîte " + numBoite + " retirée." : "Boîte introuvable.");
    }

    public void marquerComplete(String numBoite, boolean complete) {
        for (BoiteComposee b : boites) {
            if (b.getNumBoite().equals(numBoite)) {
                b.setComplete(complete);
                System.out.println("Boîte " + numBoite + " marquée " + (complete ? "complète" : "incomplète") + ".");
                return;
            }
        }
        System.out.println("Boîte introuvable dans la collection.");
    }

    public void sauvegarder() {
        JsonArray tableau = new JsonArray();

        for (BoiteComposee b : boites) {
            JsonObject obj = new JsonObject();
            obj.addProperty("numBoite",b.getNumBoite());
            obj.addProperty("complete",b.estComplete());
            obj.addProperty("collection",b.estDansCollection());
            obj.addProperty("personnalisee",b.estPersonnalisee());

            if (b.estPersonnalisee()) {
                obj.addProperty("nomBoite",b.getNomBoite());
                obj.addProperty("annee",b.getAnnee());
                obj.addProperty("nbPieces",b.getNbPieces());
                obj.addProperty("idTheme",b.getTheme() != null ? b.getTheme().getIdTheme()  : -1);
                obj.addProperty("nomTheme",b.getTheme() != null ? b.getTheme().getNomTheme() : "");
                JsonArray pieces = new JsonArray();
                for (ContenuPiece cp : b.getPieces()) {
                    JsonObject p = new JsonObject();
                    p.addProperty("numPiece",cp.getPiece().obtenirNumPiece());
                    p.addProperty("nomPiece",cp.getPiece().obtenirNomPiece());
                    p.addProperty("idCat",cp.getPiece().obtenirCategorie().getIdCat());
                    p.addProperty("nomCat",cp.getPiece().obtenirCategorie().getNomCat());
                    p.addProperty("idCoul",cp.getPiece().obtenirCouleur().getIdCouleur());
                    p.addProperty("nomCoul", cp.getPiece().obtenirCouleur().getNomCouleur());
                    p.addProperty("rgb", cp.getPiece().obtenirCouleur().getRgb());
                    p.addProperty("transparent",cp.getPiece().obtenirCouleur().isTransparent());
                    p.addProperty("quantite",cp.getQuantite());
                    p.addProperty("enSupplement",cp.estEnSupplement());
                    pieces.add(p);
                }
                obj.add("pieces", pieces);

                JsonArray figurines = new JsonArray();
                for (ContenuFigurine cf : b.getFigurines()) {
                    JsonObject f = new JsonObject();
                    f.addProperty("idFig",cf.getFigurine().getIdFigurine());
                    f.addProperty("nomFig", cf.getFigurine().getNomFigurine());
                    f.addProperty("nbParties",cf.getFigurine().getNombrePartie());
                    f.addProperty("quantite", cf.getQuantite());
                    figurines.add(f);
                }
                obj.add("figurines", figurines);

            } else {
                JsonArray piecesRetirees = new JsonArray();
                for (BoiteComposee.PieceRetiree pr : b.getPiecesRetirees()){
                    JsonObject p = new JsonObject();
                    p.addProperty("numPiece",pr.numPiece);
                    p.addProperty("idCoul", pr.idCoul);
                    p.addProperty("quantiteRetiree", pr.quantiteRetiree);
                    piecesRetirees.add(p);
                }
                obj.add("piecesRetirees", piecesRetirees);

                JsonArray figurinesRetirees = new JsonArray();
                for (BoiteComposee.FigurineRetiree fr : b.getFigurinesRetirees()){
                    JsonObject f = new JsonObject();
                    f.addProperty("idFig",fr.idFig);
                    f.addProperty("quantiteRetiree",fr.quantiteRetiree);
                    figurinesRetirees.add(f);
                }
                obj.add("figurinesRetirees", figurinesRetirees);
            }

            tableau.add(obj);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (Writer writer = new FileWriter(FICHIER_COLLECTION)) {
            gson.toJson(tableau, writer);
            System.out.println("Collection sauvegardée (" + boites.size() + " boîte(s)).");
        } catch (IOException e) {
            System.err.println("Erreur sauvegarde : " + e.getMessage());
        }
    }

   
    public void charger() {
        File fichier = new File(FICHIER_COLLECTION);
        if (!fichier.exists()) {
            System.out.println("Aucune collection sauvegardée");
            return;
        }

        Gson gson = new Gson();
        try (Reader reader = new FileReader(fichier)) {
            JsonArray tableau = gson.fromJson(reader, JsonArray.class);
            BoiteBD boiteBD = new BoiteBD(connexion);
            int chargees = 0;

            for (JsonElement element : tableau) {
                JsonObject obj= element.getAsJsonObject();
                String  numBoite = obj.get("numBoite").getAsString();
                boolean complete = obj.get("complete").getAsBoolean();
                boolean collection = obj.get("collection").getAsBoolean();
                boolean personnalisee =obj.get("personnalisee").getAsBoolean();

                BoiteComposee boite;
                if (personnalisee) {
                    boite = chargerBoitePersoDepuisJson(obj, complete, collection);
                } else {
                    boite = boiteBD.getBoiteComplete(numBoite, complete, collection);
                    if (boite == null) {
                        System.err.println("Boîte " + numBoite + " ignorée");
                        continue;
                    }
                    appliquerPiecesRetirees(boite, obj);
                }
                boites.add(boite);
                chargees++;
            }
            System.out.println(chargees + " boîte(s) rechargée.");

        } catch (IOException | SQLException e) {
            System.err.println("Erreur chargement : " + e.getMessage());
        }
    }

    private void appliquerPiecesRetirees(BoiteComposee boite, JsonObject obj) {
        if (obj.has("piecesRetirees")) {
            for (JsonElement pe : obj.getAsJsonArray("piecesRetirees")) {
                JsonObject p= pe.getAsJsonObject();
                String numPiece = p.get("numPiece").getAsString();
                int idCoul = p.get("idCoul").getAsInt();
                int qteRetiree = p.get("quantiteRetiree").getAsInt();

                ContenuPiece cpTrouve = null;
                for (ContenuPiece cp : boite.getPieces()) {
                    if (cp.getPiece().obtenirNumPiece().equals(numPiece)&& cp.getPiece().obtenirCouleur().getIdCouleur() == idCoul){
                        cpTrouve = cp;
                        break;
                    }
                }
                if (cpTrouve != null){
                    int nouvelleQte = cpTrouve.getQuantite()-qteRetiree;
                    boite.getPieces().remove(cpTrouve);
                    if (nouvelleQte > 0) {
                        boite.getPieces().add(new ContenuPiece(cpTrouve.getPiece(), nouvelleQte, cpTrouve.estEnSupplement()));
                    }
                    boite.enregistrerPieceRetiree(numPiece, idCoul);
                }
            }
        }
        if (obj.has("figurinesRetirees")) {
            for (JsonElement fe : obj.getAsJsonArray("figurinesRetirees")) {
                JsonObject f = fe.getAsJsonObject();
                String idFig = f.get("idFig").getAsString();
                int qteRetiree = f.get("quantiteRetiree").getAsInt();

                ContenuFigurine cfTrouve = null;
                for (ContenuFigurine cf : boite.getFigurines()){
                    if (cf.getFigurine().getIdFigurine().equals(idFig)) {
                        cfTrouve = cf;
                        break;
                    }
                }
                if (cfTrouve != null){
                    int nouvelleQte = cfTrouve.getQuantite()-qteRetiree;
                    boite.getFigurines().remove(cfTrouve);
                    if (nouvelleQte > 0) {
                        boite.getFigurines().add(new ContenuFigurine(cfTrouve.getFigurine(), nouvelleQte));
                    }
                    boite.enregistrerFigurineRetiree(idFig);
                }
            }
        }
    }

    private BoiteComposee chargerBoitePersoDepuisJson(JsonObject obj,boolean complete,boolean collection){
        String numBoite =obj.get("numBoite").getAsString();
        String nomBoite =obj.get("nomBoite").getAsString();
        int annee =obj.get("annee").getAsInt();
        int nbPieces =obj.get("nbPieces").getAsInt();
        int idTheme = obj.get("idTheme").getAsInt();
        String nomTheme = obj.get("nomTheme").getAsString();

        Theme theme = new Theme(idTheme, nomTheme);
        BoiteComposee boite = new BoiteComposee(numBoite, nomBoite, annee, nbPieces,theme, complete, collection, true);
        if (obj.has("pieces")){
            for (JsonElement pe : obj.getAsJsonArray("pieces")){
                JsonObject p  = pe.getAsJsonObject();
                Categorie cat = new Categorie(p.get("idCat").getAsInt(), p.get("nomCat").getAsString());
                Couleur coul  = new Couleur(p.get("idCoul").getAsInt(), p.get("nomCoul").getAsString(),p.get("rgb").getAsString(),  p.get("transparent").getAsBoolean());
                Piece piece = new Piece(p.get("numPiece").getAsString(), p.get("nomPiece").getAsString(), cat, coul);
                boite.ajouterPiece(new ContenuPiece(piece,p.get("quantite").getAsInt(), p.get("enSupplement").getAsBoolean()));
            }
        }
        if (obj.has("figurines")) {
            for (JsonElement fe : obj.getAsJsonArray("figurines")) {
                JsonObject f = fe.getAsJsonObject();
                Figurine fig = new Figurine(f.get("idFig").getAsString(), f.get("nomFig").getAsString(),f.get("nbParties").getAsInt()
                );
                boite.ajouterFigurine(new ContenuFigurine(fig, f.get("quantite").getAsInt()));
            }
        }
        return boite;
    }

    
    public void afficher() {
        if (boites.isEmpty()){ 
            System.out.println("La collection est vide.");
            return; 
        }
        System.out.println("\n Ma collection ──────────────────────────────────");
        for (BoiteComposee b : boites) {
            System.out.println(b);}
        
    }
}
