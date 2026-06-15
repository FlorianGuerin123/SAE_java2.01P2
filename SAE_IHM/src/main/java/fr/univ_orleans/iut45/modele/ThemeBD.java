import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ThemeBD {
    ConnexionMySQL laConnexion;
	Statement st;
	ThemeBD(ConnexionMySQL laConnexion){
		this.laConnexion=laConnexion;
	}

    boolean themeEstDansBD(int numTheme) throws SQLException {
        st = laConnexion.createStatement();
        String sql = "SELECT COUNT(*) FROM THEME WHERE numTheme = " + numTheme;
        ResultSet rs = this.st.executeQuery(sql);
        if (rs.next()) {
            return true;
        }
        return false; 
    }

    int getIdThemeAvecNom(String nomTheme) throws SQLException {
        PreparedStatement ps = laConnexion.prepareStatement("SELECT idtheme FROM THEME WHERE nomtheme = ?");
        ps.setString(1, nomTheme);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("idtheme");
        } else {
            throw new SQLException("Thème non trouvé");
        }
    }

    String getNomThemeAvecId(int idTheme) throws SQLException {
        PreparedStatement ps = laConnexion.prepareStatement("SELECT nomtheme FROM THEME WHERE idtheme = ?");
        ps.setInt(1, idTheme);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("nomtheme");
        } else {
            throw new SQLException("Thème non trouvé");
        }
    }

    void ajouterTheme(int idTheme, String nomTheme, String themeParent) throws SQLException {
        PreparedStatement ps = laConnexion.prepareStatement("insert into THEME (idtheme, nomtheme, idtheme_pere) values (?, ?, ?)");
        ps.setInt(1, idTheme);
        ps.setString(2, nomTheme);
        if (themeParent == null || themeParent.isEmpty()) {
            ps.setNull(3, java.sql.Types.NULL);
        }
        else{
            int idThemeParent = getIdThemeAvecNom(themeParent);
            ps.setInt(3, idThemeParent);
        }
        try{
            ps.executeUpdate();
            System.out.println("Thème inséré avec succès.");
        }
        catch(SQLException e){
            String message = e.getMessage().toLowerCase();
            if (message.contains("duplicate entry")) {
                System.err.println("Erreur : l'id de thème " + idTheme + " existe déjà.");
            } else if (message.contains("foreign key constraint")) {
                System.err.println("Erreur : le thème parent " + themeParent + " n'existe pas dans la base.");
            } else {
                System.err.println("Violation de contrainte : " + e.getMessage());
            }
             return;
        }
    }

    public Theme getThemeBoite(String numBoite) throws SQLException {
        PreparedStatement ps = laConnexion.prepareStatement("select t.idtheme, t.nomtheme from BOITE b join THEME t on b.idtheme = t.idtheme where b.numboite = ?");
        ps.setString(1, numBoite);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Theme(rs.getInt("idtheme"), rs.getString("nomtheme"));
        }
        throw new SQLException("Thème de la boîte non trouvé");
    }
}
