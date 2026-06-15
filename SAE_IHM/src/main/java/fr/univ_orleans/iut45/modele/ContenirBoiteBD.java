package fr.univ_orleans.iut45.modele;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

public class ContenirBoiteBD {
    ConnexionMySQL laConnexion;
	Statement st;
	ContenirBoiteBD(ConnexionMySQL laConnexion){
		this.laConnexion=laConnexion;
	}

    void ajouterBoiteDansBoite(String numBoiteContenante, String numBoiteContenue) throws SQLException {
        PreparedStatement ps = laConnexion.prepareStatement("INSERT INTO CONTENIRB (idcont, numboite, quantiteb) VALUES (?, ?, 1)");
        ContenuBD contenuBD = new ContenuBD(laConnexion);
        int idContenante = contenuBD.getIdcontDUneBoite(numBoiteContenante);
        ps.setInt(1, idContenante);
        ps.setString(2, numBoiteContenue);
        try {
            ps.executeUpdate();
            System.out.println("Boîte ajoutée dans la boîte avec succès.");
        } catch (SQLIntegrityConstraintViolationException e) {
            String message = e.getMessage().toLowerCase();
            if (message.contains("duplicate entry")) {
                System.err.println("Erreur : la boîte numéro " + numBoiteContenue + " est déjà présente dans la boîte numéro " + numBoiteContenante + ".");
            } else {
                System.err.println("Violation de contrainte : " + e.getMessage());
            }
        }
    }
}
