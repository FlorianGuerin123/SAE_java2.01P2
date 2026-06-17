package fr.univ_orleans.iut45.modele;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FigurineBD {
    ConnexionMySQL laConnexion;
	Statement st;
	public FigurineBD(ConnexionMySQL laConnexion){
		this.laConnexion=laConnexion;
    }

    Figurine getFigurine(String idFigurine) throws SQLException {
        PreparedStatement ps = laConnexion.prepareStatement(
            "SELECT * FROM FIGURINE WHERE idfig = ?"
        );
        ps.setString(1, idFigurine);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Figurine(
                rs.getString("idfig"),
                rs.getString("nomfig"),
                rs.getInt("nbparties")
            );
        }
        throw new SQLException("Aucune figurine trouvée avec l'ID : " + idFigurine);
    }

    public List<Figurine> getFigurinesDansBoite(String numBoite) throws SQLException {
        PreparedStatement ps = laConnexion.prepareStatement(
            "select FIGURINE.idfig, FIGURINE.nomfig, FIGURINE.nbparties from BOITE natural join CONTENU join CONTENIRF on CONTENIRF.idcont = CONTENU.idcont join FIGURINE on FIGURINE.idfig= CONTENIRF.idfig where BOITE.numboite = ?"

        );
        ps.setString(1, numBoite);
        ResultSet rs = ps.executeQuery();
        List<Figurine> figurines = new ArrayList<>();
        while (rs.next()) {
            figurines.add(new Figurine(
                rs.getString("idfig"),
                rs.getString("nomfig"),
                rs.getInt("nbparties")
            ));
        }
        return figurines;
    }

    public List<Figurine> getFigurinesParNom(String nom) throws SQLException {
    PreparedStatement ps = laConnexion.prepareStatement("SELECT idfig, nomfig, nbparties FROM FIGURINE WHERE nomfig LIKE ? LIMIT 5");
    ps.setString(1, "%" + nom + "%");
    ResultSet rs = ps.executeQuery();
    List<Figurine> res = new ArrayList<>();
    while (rs.next())
        res.add(new Figurine(rs.getString("idfig"), rs.getString("nomfig"), rs.getInt("nbparties")));
    return res;
}
}
