package fr.univ_orleans.iut45.modele;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.*;

public class ContenirFigurineBD {
    ConnexionMySQL laConnexion;
	Statement st;
	ContenirFigurineBD(ConnexionMySQL laConnexion){
		this.laConnexion=laConnexion;
	}

    void ajouterFigurineDansBoite(String nomFigurine, String numBoite, int quantite) throws SQLException {
        String idfig = this.getIdFigAvecNom(nomFigurine);
        int idcontenu = (new ContenuBD(laConnexion)).getIdcontDUneBoite(numBoite);
        PreparedStatement ps = laConnexion.prepareStatement("INSERT INTO CONTENIRF (idcont, idfig, quantitef) VALUES (?, ?, ?)");
        ps.setInt(1, idcontenu);
        ps.setString(2, idfig);
        ps.setInt(3, quantite);
        try {
            ps.executeUpdate();
            System.out.println("Figurine ajoutée dans la boîte avec succès.");
        } catch (SQLIntegrityConstraintViolationException e) {
            // Clé primaire dupliquée
            String message = e.getMessage().toLowerCase();
            if (message.contains("duplicate entry")) {
                System.err.println("Erreur : la figurine numéro " + idfig + " est déjà présente dans la boîte numéro " + numBoite + ".");
            } else {
                System.err.println("Violation de contrainte : " + e.getMessage());
            }
        }
    }

    String getIdFigAvecNom(String nomFigurine) throws SQLException {
        PreparedStatement ps = laConnexion.prepareStatement("select idfig from FIGURINE where nomfig = ?");
        ps.setString(1, nomFigurine);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("idfig");
        }
        throw new SQLException("Figurine non trouvée");
    }
}
