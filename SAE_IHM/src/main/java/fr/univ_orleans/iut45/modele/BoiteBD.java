package fr.univ_orleans.iut45.modele;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.*;

public class BoiteBD {
    ConnexionMySQL laConnexion;
	Statement st;
	public BoiteBD(ConnexionMySQL laConnexion){
		this.laConnexion=laConnexion;
	}

    boolean boiteEstDansBD(String numBoite) throws SQLException {
        st = laConnexion.createStatement();
        try{
            String sql = "SELECT COUNT(*) FROM BOITE WHERE numBoite = " + numBoite;
            ResultSet rs = this.st.executeQuery(sql);
            if (rs.next()) {
                return true;
            }
        }
        catch (SQLException e) {
            return false;
        }
        return false; 
    }

    public void ajouterBoite(String num, String nomboite, int anneeInt, int nbpiecesInt, int idTheme) throws SQLException{
        st = laConnexion.createStatement();
        String ps = "INSERT INTO BOITE (numboite, nomboite, annee, nbpieces, idTheme) VALUES ("+num+", '"+nomboite+"', "+anneeInt+", "+nbpiecesInt+", "+idTheme+")";
        try{
            this.st.executeUpdate(ps);
            System.out.println("Boîte insérée avec succès.");
        }
        catch(SQLIntegrityConstraintViolationException e) {
            // Clé primaire dupliquée OU clé étrangère inexistante
            String message = e.getMessage().toLowerCase();

            if (message.contains("duplicate entry")) {
                throw new SQLException("Erreur : le numéro de boîte " + num + " existe déjà.");
            } else if (message.contains("foreign key constraint")) {
                throw new SQLException("Erreur : l'idThème " + idTheme + " n'existe pas dans la base.");
            } else {
                throw new SQLException("Violation de contrainte : " + e.getMessage());
            }
        }
    }

    void majBoite(String numBoite, String nomBoite, int annee, int nbPieces, int idTheme) throws SQLException {
        st = laConnexion.createStatement();
        PreparedStatement ps = laConnexion.prepareStatement("UPDATE BOITE SET nomboite = ?, annee = ?, nbpieces = ?, idTheme = ? WHERE numboite = ?");
        ps.setString(1, nomBoite);
        ps.setInt(2, annee);
        ps.setInt(3, nbPieces);
        ps.setInt(4, idTheme);
        ps.setString(5, numBoite);
        try {
            int i = ps.executeUpdate();
            if (i > 0) {
                System.out.println("Boîte mise à jour avec succès.");
            } else {
                System.out.println("Aucune boîte trouvée avec le numéro " + numBoite);
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            // Clé étrangère inexistante
            String message = e.getMessage().toLowerCase();
            if (message.contains("foreign key constraint")) {
                System.err.println("Erreur : l'idThème " + idTheme + " n'existe pas dans la base.");
            } else {
                System.err.println("Violation de contrainte : " + e.getMessage());
            }
        }
    }

    public BoiteSimple rechercherBoite(String num) throws SQLException {
        PreparedStatement ps=laConnexion.prepareStatement("SELECT * FROM BOITE WHERE numboite = ?");
        ps.setString(1,num);
        ResultSet rs=ps.executeQuery();
        int compteur = 0;
        while (rs.next()){
            compteur++;
            ThemeBD themeBD = new ThemeBD(laConnexion);
            String nomtheme = themeBD.getNomThemeAvecId(rs.getInt("idtheme"));
            return new BoiteSimple(rs.getString("numboite"), rs.getString("nomboite"), rs.getInt("annee"), rs.getInt("nbpieces"), new Theme(rs.getInt("idtheme"), nomtheme), true);
        }
        if (compteur == 0) {
            System.out.println("Aucune boîte trouvée avec ce numéro.");
        }
        return null;
    }


    BoiteComposee getBoiteComplete(String numBoite, boolean complete, boolean collection) throws SQLException{
        // 1. Boite
        PreparedStatement ps = laConnexion.prepareStatement("SELECT b.numboite, b.nomboite, b.annee, b.nbpieces,t.idtheme, t.nomtheme FROM BOITE b JOIN THEME t ON b.idtheme = t.idtheme WHERE b.numboite = ?");
        ps.setString(1, numBoite);
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            System.err.println("Boîte " + numBoite + " introuvable en base.");
            return null;
        }

        Theme theme = new Theme(rs.getInt("idtheme"), rs.getString("nomtheme"));
        BoiteComposee boite = new BoiteComposee(rs.getString("numboite"),rs.getString("nomboite"),rs.getInt("annee"),rs.getInt("nbpieces"),theme, complete, collection);

        int idCont = getIdCont(numBoite);
        if (idCont == -1){
            return boite;
        }

        // 2. Pieces
        PreparedStatement psPieces = laConnexion.prepareStatement(
            "SELECT p.numpiece, p.nompiece,c.idcat, c.nomcat,col.idcoul, col.nomcoul, col.RGB, col.transparent,cp.quantitep, cp.en_supplement FROM CONTENIRP cp JOIN PIECE p  ON cp.numpiece = p.numpiece JOIN CATEGORIE c ON p.idcat = c.idcat JOIN COULEUR col ON cp.idcoul = col.idcoul WHERE cp.idcont = ?");
        psPieces.setInt(1, idCont);
        ResultSet rsPieces = psPieces.executeQuery();
        while (rsPieces.next()) {
            Categorie cat = new Categorie(rsPieces.getInt("idcat"),rsPieces.getString("nomcat"));
            Couleur couleur = new Couleur(rsPieces.getInt("idcoul"),rsPieces.getString("nomcoul"),rsPieces.getString("RGB"),"Y".equalsIgnoreCase(rsPieces.getString("transparent")));
            Piece piece = new Piece(rsPieces.getString("numpiece"),rsPieces.getString("nompiece"),cat, couleur);
            boolean enSupp = "Y".equalsIgnoreCase(rsPieces.getString("en_supplement"));
            boite.ajouterPiece(new ContenuPiece(piece, rsPieces.getInt("quantitep"), enSupp));
        }

        // 3. Figurines
        PreparedStatement psFig = laConnexion.prepareStatement(
            "SELECT f.idfig, f.nomfig, f.nbparties, cf.quantitef FROM CONTENIRF cf JOIN FIGURINE f ON cf.idfig = f.idfig WHERE cf.idcont = ?");
        psFig.setInt(1, idCont);
        ResultSet rsFig = psFig.executeQuery();

        while (rsFig.next()) {
                Figurine fig = new Figurine(rsFig.getString("idfig"),rsFig.getString("nomfig"),rsFig.getInt("nbparties"));
            boite.ajouterFigurine(new ContenuFigurine(fig, rsFig.getInt("quantitef")));
        }

        // 4. Sous boites
        PreparedStatement psSous = laConnexion.prepareStatement(
            "SELECT b.numboite, b.nomboite, b.annee, b.nbpieces,t.idtheme, t.nomtheme, cb.quantiteb FROM CONTENIRB cb JOIN BOITE b ON cb.numboite  = b.numboite JOIN THEME t ON b.idtheme= t.idtheme WHERE cb.idcont = ?");
        psSous.setInt(1, idCont);
        ResultSet rsSous = psSous.executeQuery();

        while (rsSous.next()) {
            Theme themeSous = new Theme(rsSous.getInt("idtheme"), rsSous.getString("nomtheme"));
            BoiteComposee sousBoite = new BoiteComposee(rsSous.getString("numboite"),rsSous.getString("nomboite"),rsSous.getInt("annee"),rsSous.getInt("nbpieces"),themeSous, false, false);
            boite.ajouterSousBoite(new ContenuBoite(sousBoite, rsSous.getInt("quantiteb")));
        }

        return boite;
    }

    public int getIdCont(String numBoite) throws SQLException {
        PreparedStatement ps = laConnexion.prepareStatement("SELECT idcont FROM CONTENU WHERE numboite = ?");
        ps.setString(1, numBoite);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("idcont");
        }
        return -1;
    }

    public String getIdBoiteAvecNom(String nomBoite) throws SQLException {
        PreparedStatement ps = laConnexion.prepareStatement("SELECT numboite FROM BOITE WHERE nomboite = ?");
        ps.setString(1, nomBoite);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("numboite");
        }
        throw new SQLException("Boîte non trouvée");
    }

    public List<BoiteSimple> getBoitesIncluses(String numBoite) throws SQLException {
        PreparedStatement ps = laConnexion.prepareStatement("select b2.numboite, b2.nomboite, b2.annee, b2.nbpieces from BOITE b1 natural join CONTENU c1 join CONTENIRB cb on c1.idcont = cb.idcont join BOITE b2 on cb.numboite = b2.numboite join THEME t on b2.idtheme = t.idtheme where b1.numboite = ?");
        ps.setString(1, numBoite);
        ResultSet rs = ps.executeQuery();
        List<BoiteSimple> boitesIncluses = new ArrayList<>();
        ThemeBD themeBD = new ThemeBD(laConnexion);
        while (rs.next()) {
            Theme theme = themeBD.getThemeBoite(rs.getString("numboite"));
            BoiteSimple boite = new BoiteSimple(rs.getString("numboite"),rs.getString("nomboite"),rs.getInt("annee"),rs.getInt("nbpieces"),theme, true);
            boitesIncluses.add(boite);
        }
        return boitesIncluses;
    }

    public List<BoiteSimple> getBoitesSelonTheme(String nomTheme) throws SQLException {
        PreparedStatement ps = laConnexion.prepareStatement("SELECT numboite, nomboite, annee, nbpieces, idtheme, nomtheme FROM BOITE natural join THEME WHERE nomtheme = ?");
        ps.setString(1, nomTheme);
        ResultSet rs = ps.executeQuery();
        List<BoiteSimple> boites = new ArrayList<>();
        while (rs.next()) {
            Theme theme = new Theme(rs.getInt("idtheme"), nomTheme);
            BoiteSimple boite = new BoiteSimple(rs.getString("numboite"),rs.getString("nomboite"),rs.getInt("annee"),rs.getInt("nbpieces"),theme, true);
            boites.add(boite);
        }
        return boites;
    }

    public List<BoiteSimple> getBoitesPossedantPiece(String numPiece) throws SQLException {
        PreparedStatement ps = laConnexion.prepareStatement("select numboite, nomboite, annee, nbpieces from PIECE natural join CONTENIRP natural join CONTENU natural join BOITE where numpiece = ?");
        ps.setString(1, numPiece);
        ResultSet rs = ps.executeQuery();
        List<BoiteSimple> boites = new ArrayList<>();
        while (rs.next()) {
            ThemeBD themeBD = new ThemeBD(laConnexion);
            Theme theme = themeBD.getThemeBoite(rs.getString("numboite"));
            BoiteSimple boite = new BoiteSimple(rs.getString("numboite"),rs.getString("nomboite"),rs.getInt("annee"),rs.getInt("nbpieces"),theme, true);
            boites.add(boite);
        }
        return boites;
    }

    public void supprimerBoitePartout(String numBoite) throws SQLException {
        PreparedStatement supprimerContenirP = laConnexion.prepareStatement("DELETE FROM CONTENIRP WHERE idcont IN (SELECT idcont FROM CONTENU WHERE numboite = ?)");
        PreparedStatement supprimerContenirF = laConnexion.prepareStatement("DELETE FROM CONTENIRF WHERE idcont IN (SELECT idcont FROM CONTENU WHERE numboite = ?)");
        PreparedStatement supprimerContenirB = laConnexion.prepareStatement("DELETE FROM CONTENIRB WHERE numboite = ?");
        PreparedStatement supprimerContenu = laConnexion.prepareStatement("DELETE FROM CONTENU WHERE numboite = ?");
        PreparedStatement supprimerBoite = laConnexion.prepareStatement("DELETE FROM BOITE WHERE numboite = ?");
        supprimerContenirP.setString(1, numBoite);
        supprimerContenirF.setString(1, numBoite);
        supprimerContenirB.setString(1, numBoite);
        supprimerContenu.setString(1, numBoite);
        supprimerBoite.setString(1, numBoite);
        try {
            supprimerContenirP.executeUpdate();
            supprimerContenirF.executeUpdate();
            supprimerContenirB.executeUpdate();
            supprimerContenu.executeUpdate();
            int res = supprimerBoite.executeUpdate();
            if (res > 0) {
                System.out.println("Boîte supprimée avec succès.");
            }
            else {
                System.out.println("Aucune boîte trouvée avec ce numéro.");
            }    
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la suppression : " + ex.getMessage());
        }
    }

    public java.util.List<BoiteSimple> rechercherBoitesDynamique(String recherche) throws java.sql.SQLException {
        java.util.List<BoiteSimple> listeResultats = new java.util.ArrayList<>();
        
        String query = "SELECT b.numboite, b.nomboite, b.annee, b.nbpieces, b.idtheme, t.nomtheme " +
                       "FROM BOITE b JOIN THEME t ON b.idtheme = t.idtheme " +
                       "WHERE b.numboite LIKE ? OR b.nomboite LIKE ? LIMIT 5";
        
        java.sql.PreparedStatement ps = laConnexion.prepareStatement(query);
        ps.setString(1, recherche + "%"); 
        ps.setString(2, recherche + "%");
        
        java.sql.ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Theme theme = new Theme(rs.getInt("idtheme"), rs.getString("nomtheme"));
            BoiteSimple b = new BoiteSimple(
                rs.getString("numboite"), 
                rs.getString("nomboite"), 
                rs.getInt("annee"), 
                rs.getInt("nbpieces"), 
                theme, 
                true
            );
            listeResultats.add(b);
        }
        
        rs.close();
        ps.close();
        return listeResultats;
    }

    public List<BoiteSimple> getBoitesParNom(String nomBoite) throws SQLException{
    PreparedStatement ps = laConnexion.prepareStatement("SELECT b.numboite, b.nomboite, b.annee, b.nbpieces, " +"       t.idtheme, t.nomtheme " +"FROM BOITE b JOIN THEME t ON b.idtheme = t.idtheme " +"WHERE b.nomboite = ?"
    );
    ps.setString(1, nomBoite);
    ResultSet rs = ps.executeQuery();
 
    List<BoiteSimple> boites = new ArrayList<>();
    while (rs.next()) {
        Theme theme = new Theme(rs.getInt("idtheme"), rs.getString("nomtheme"));
        boites.add(new BoiteSimple(
            rs.getString("numboite"),
            rs.getString("nomboite"),
            rs.getInt("annee"),
            rs.getInt("nbpieces"),
            theme,
            true
        ));
    }
    return boites;
}

}