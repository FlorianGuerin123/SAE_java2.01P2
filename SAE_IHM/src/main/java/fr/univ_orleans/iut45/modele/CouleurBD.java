package fr.univ_orleans.iut45.modele;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class CouleurBD {
    ConnexionMySQL laConnexion;
	Statement st;
	CouleurBD(ConnexionMySQL laConnexion){
		this.laConnexion=laConnexion;
    }

    int getIdCoul(String nomCouleur) throws SQLException {
        st = laConnexion.createStatement();
        String sql = "SELECT idcoul FROM COULEUR WHERE nomcoul = '" + nomCouleur + "'";
        ResultSet rs = this.st.executeQuery(sql);
        if (rs.next()) {
            return rs.getInt("idcoul");
        }
        throw new SQLException("Aucune couleur trouvée avec le nom : " + nomCouleur);
    }
    int getIdCoulAvecNom(String nomCouleur) throws SQLException {
        PreparedStatement ps = laConnexion.prepareStatement(
            "SELECT idcoul FROM COULEUR WHERE nomcoul = ?"
        );
        ps.setString(1, nomCouleur);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("idcoul");
        }
        throw new SQLException("Aucune couleur trouvée avec le nom : " + nomCouleur);
    }

    Couleur getCouleur(int numCouleur) throws SQLException {
        PreparedStatement ps = laConnexion.prepareStatement(
            "SELECT * FROM COULEUR WHERE idcoul = ?"
        );
        ps.setInt(1, numCouleur);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Couleur(
                rs.getInt("idcoul"),
                rs.getString("nomcoul"),
                rs.getString("RGB"),
                rs.getBoolean("transparent")
            );
        }
        throw new SQLException("Aucune couleur trouvée avec le numéro : " + numCouleur);
    }

    public record StatCouleur(String nomCouleur, int quantite, int totalPiecesBoite) {}

    public List<StatCouleur> getStatsCouleurs(String numBoite) throws SQLException {
        PreparedStatement ps = laConnexion.prepareStatement(
            "SELECT nomcoul, SUM(quantitep) AS nbCoul, nbpieces " +
            "FROM COULEUR NATURAL JOIN CONTENIRP NATURAL JOIN CONTENU NATURAL JOIN BOITE " +
            "WHERE numboite = ? " +
            "GROUP BY nomcoul"
        );
        ps.setString(1, numBoite);
        ResultSet rs = ps.executeQuery();
        List<StatCouleur> stats = new ArrayList<>();
        while (rs.next()) {
            stats.add(new StatCouleur(
                rs.getString("nomcoul"),
                rs.getInt("nbCoul"),
                rs.getInt("nbpieces")
            ));
        }
        return stats;
    }

}
