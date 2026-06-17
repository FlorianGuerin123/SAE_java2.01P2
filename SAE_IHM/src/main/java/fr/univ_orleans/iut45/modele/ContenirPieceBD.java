package fr.univ_orleans.iut45.modele;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;


public class ContenirPieceBD {
    ConnexionMySQL laConnexion;
	Statement st;
	public ContenirPieceBD(ConnexionMySQL laConnexion){
		this.laConnexion=laConnexion;
	}

   public void ajouterPieceDansBoite(String nomPiece, String numBoite, String nomCoul, String enSuppStr, int quantite) throws SQLException {
        PreparedStatement ps = laConnexion.prepareStatement("INSERT INTO CONTENIRP (idcont, numpiece, idcoul, en_supplement, quantitep) VALUES (?, ?, ?, ?, ?)");
        ContenuBD contenuBD = new ContenuBD(laConnexion);
        int idContenu = contenuBD.getIdcontDUneBoite(numBoite);
        CouleurBD couleurBD = new CouleurBD(laConnexion);
        PieceBD pieceBD = new PieceBD(laConnexion);
        String numPiece = pieceBD.getIdPiece(nomPiece);
        int idCoul = couleurBD.getIdCoul(nomCoul);
        ps.setInt(1, idContenu);
        ps.setString(2, numPiece);
        ps.setInt(3, idCoul);
        ps.setString(4, enSuppStr);
        ps.setInt(5, quantite);
        try {
            ps.executeUpdate();
            System.out.println("Pièce ajoutée dans la boîte avec succès.");
        } catch (SQLIntegrityConstraintViolationException e) {
            // Clé primaire dupliquée
            String message = e.getMessage().toLowerCase();
            if (message.contains("duplicate entry")) {
                System.err.println("Erreur : la pièce numéro " + numPiece + " est déjà présente dans la boîte numéro " + numBoite + ".");
            } else {
                System.err.println("Violation de contrainte : " + e.getMessage());
            }
        }
    }

    public void supprimerPieceContenue(String numPiece) throws SQLException {
        PreparedStatement ps = laConnexion.prepareStatement("DELETE FROM CONTENIRP WHERE numpiece = ?");
        ps.setString(1, numPiece);
        int i = ps.executeUpdate();
        if (i > 0) {
            System.out.println("Pièce supprimée de la boîte avec succès.");
        } else {
            System.out.println("Aucune pièce trouvée avec le numéro " + numPiece );
        }
    }

}
