package fr.univ_orleans.iut45.modele;

public class Piece{

    private String numPiece;
    private String nomPiece;
    private Categorie categorie;
    private Couleur couleur;

    public Piece(String numPiece, String nomPiece, Categorie categorie, Couleur couleur) {
        this.numPiece = numPiece;
        this.nomPiece = nomPiece;
        this.categorie = categorie;
        this.couleur = couleur;
    }

    public String obtenirNumPiece(){ 
        return this.numPiece; 
    }

    public String    obtenirNomPiece(){ 
        return this.nomPiece; 
    }

    public Categorie obtenirCategorie(){ 
        return this.categorie; 
    }
    public Couleur obtenirCouleur(){ 
        return this.couleur; 
    }

    @Override
    public boolean equals(Object o){
        if (this == o){
            return true;
        }
        if (o==null){
            return false;
        }
        if (!(o instanceof Piece)){ 
            return false;
        }
        return this.numPiece.equals(((Piece) o).numPiece);
    }

    @Override
    public int hashCode(){ 
        return this.numPiece.hashCode(); 
    }

    @Override
    public String toString(){
        return "NumPiece: " + this.numPiece + " - Nom: " + this.nomPiece + " - Catégorie: " + this.categorie + " - Couleur: " + this.couleur;
    }
}
