import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

public class ContenuBD {
    ConnexionMySQL laConnexion;
	Statement st;
	ContenuBD(ConnexionMySQL laConnexion){
		this.laConnexion=laConnexion;
	}

    boolean contenuEstDansBD(int idContenu) throws SQLException {
        PreparedStatement ps = laConnexion.prepareStatement("SELECT COUNT(*) FROM CONTENU WHERE idContenu = ?");
        ps.setInt(1, idContenu);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return true;
        }
        return false;
    }

    int getIdcontDUneBoite(String numBoite) throws SQLException {
        st = laConnexion.createStatement();
        PreparedStatement ps = laConnexion.prepareStatement("SELECT idcont FROM CONTENU WHERE numBoite = ?");
        ps.setString(1, numBoite);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("idcont");
        }
        throw new SQLException("Aucun contenu trouvé pour la boîte numéro " + numBoite); 
    }

}
