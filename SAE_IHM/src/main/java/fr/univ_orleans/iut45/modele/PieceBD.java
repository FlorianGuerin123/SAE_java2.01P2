package fr.univ_orleans.iut45.modele;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.*;

public class PieceBD {
    ConnexionMySQL laConnexion;
	Statement st;
	public PieceBD(ConnexionMySQL laConnexion){
		this.laConnexion=laConnexion;
    }

    public void ajouterPiece(String  numPiece, String nomPiece, String nomCat) throws SQLException {
        st = laConnexion.createStatement();
        PreparedStatement ps = laConnexion.prepareStatement("SELECT idcat FROM CATEGORIE WHERE nomcat = ?");
        ps.setString(1, nomCat);
        ResultSet rs = ps.executeQuery();
        int idcat = -1;
        if (rs.next()) {
            idcat = rs.getInt("idcat");
        } else {
            System.err.println("Erreur : la catégorie " + nomCat + " n'existe pas dans la base.");
            return;
        }
        PreparedStatement ps2 = laConnexion.prepareStatement("INSERT INTO PIECE (numpiece, nompiece, idcat) VALUES (?, ?, ?)");
        ps2.setString(1, numPiece);
        ps2.setString(2, nomPiece);
        ps2.setInt(3, idcat);
        try {
            ps2.executeUpdate();
            System.out.println("Pièce insérée avec succès.");
        } catch (SQLIntegrityConstraintViolationException e) {
            // Clé primaire dupliquée
            String message = e.getMessage().toLowerCase();
            if (message.contains("duplicate entry")) {
                System.err.println("Erreur : le numéro de pièce " + numPiece + " existe déjà.");
            } else {
                System.err.println("Violation de contrainte : " + e.getMessage());
            }
        }
    }

    void supprimerPiece(String numPiece) throws SQLException {
        ContenirPieceBD contenirPieceBD = new ContenirPieceBD(laConnexion);
        contenirPieceBD.supprimerPieceContenue(numPiece);
        PreparedStatement ps = laConnexion.prepareStatement("DELETE FROM PIECE WHERE numpiece = ?");
        ps.setString(1, numPiece);
        int i = ps.executeUpdate();
        if (i > 0) {
            System.out.println("Pièce supprimée avec succès.");
        } else {
            System.out.println("Aucune pièce trouvée avec le numéro " + numPiece);
        }
    }

    int getNumPieceAvecNom(String nomPiece) throws SQLException{
        PreparedStatement ps = laConnexion.prepareStatement("select numpiece from PIECE where nompiece = ?");
        ps.setString(1, nomPiece);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int numPiece = rs.getInt("numPiece");
            System.out.println("Le numéro de la pièce " + nomPiece + " est : " + numPiece);
            return numPiece;
        } else {
            throw new SQLException("Aucune pièce trouvée avec le nom " + nomPiece);
        }
    }
    
    String getIdPiece(String nomPiece) throws SQLException {
        PreparedStatement ps = laConnexion.prepareStatement("SELECT numpiece FROM PIECE WHERE nomPiece = ?");
        ps.setString(1, nomPiece);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            String numPiece = rs.getString("numpiece");
            return numPiece;
        } else {
            throw new SQLException("Aucune pièce trouvée avec le nom " + nomPiece);
        }
    }

    List<Piece> getPiecesBoite(String numBoite) throws SQLException {
        PreparedStatement ps = laConnexion.prepareStatement("select numpiece, nompiece, idcoul, nomcat, idcat, quantitep FROM BOITE natural join CONTENU natural join CONTENIRP natural join PIECE natural join CATEGORIE natural join COULEUR WHERE numboite = ?");
        ps.setString(1, numBoite);
        ResultSet rs = ps.executeQuery();
        List<Piece> pieces = new ArrayList<>();
        while (rs.next()) {
             String numPiece = rs.getString("numpiece");
             String nomPiece = rs.getString("nompiece");
             String nomCat = rs.getString("nomcat");
             int idcat = rs.getInt("idcat");
             CouleurBD couleurBD = new CouleurBD(laConnexion);
             Couleur couleur = couleurBD.getCouleur(rs.getInt("idcoul"));
             pieces.add(new Piece(numPiece, nomPiece, new Categorie(idcat, nomCat), couleur));
        }
        return pieces;
    }

    /**
     * Retourne le nombre de types de pièces différentes et le total de pièces
     * pour une boîte donnée. Tableau : [nb_pieces_differentes, total_pieces].
    */
    List<Integer> statsPieces(String numboite) throws SQLException{
        PreparedStatement ps = laConnexion.prepareStatement("SELECT COUNT(*) AS nb_pieces_differentes, SUM(quantitep) AS total_pieces FROM BOITE natural join CONTENU natural join CONTENIRP natural join PIECE natural join COULEUR WHERE numboite = ?");
        ps.setString(1, numboite);
        ResultSet rs = ps.executeQuery();
        List<Integer> stats = new ArrayList<>();
        if (rs.next()) {
            int nbPiecesDifferentes = rs.getInt("nb_pieces_differentes");
            int totalPieces = rs.getInt("total_pieces");
            stats.add(nbPiecesDifferentes);
            stats.add(totalPieces);
        } else {
            throw new SQLException("Aucune boîte trouvée avec le numéro " + numboite);
        }
        return stats;
    }
}
