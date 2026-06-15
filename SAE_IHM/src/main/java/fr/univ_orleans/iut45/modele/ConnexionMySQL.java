import java.sql.*;

public class ConnexionMySQL {
	private Connection mysql=null;
	private boolean connecte=false;
	public ConnexionMySQL() throws ClassNotFoundException{
		Class.forName("org.mariadb.jdbc.Driver");
	}

	public void connecter(String nomServeur, String nomBase, String nomLogin, String motDePasse) throws SQLException {
		// si tout s'est bien passé la connexion n'est plus nulle
		this.connecte=false;
		this.mysql=null;
		//DriverManager.getConnection("jdbc:mysql://servinfo-maria:3306/mabd","monlogin","motdepasse");
		this.mysql = DriverManager.getConnection("jdbc:mysql://" + nomServeur + "/" + nomBase, nomLogin, motDePasse);
		this.connecte=true;
	}
	public void close() throws SQLException {
		// fermer la connexion
		this.connecte=false;
	}

    	public boolean isConnecte() { return this.connecte;}
	public Statement createStatement() throws SQLException {
		return this.mysql.createStatement();
	}

	public PreparedStatement prepareStatement(String requete) throws SQLException{
		return this.mysql.prepareStatement(requete);
	}
	
}