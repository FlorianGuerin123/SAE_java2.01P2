package fr.univ_orleans.iut45.modele;

public class Categorie {
    private int idCat;
    private String nomCat;

    public Categorie(int idCat, String nomCat){
        this.idCat = idCat;
        this.nomCat = nomCat;
    }

    public int getIdCat(){
        return idCat;
    }

    public String getNomCat(){ 
        return nomCat; 
    }

    @Override
    public String toString(){
        return "idCat: " + idCat + " - nomCat: " + nomCat;
    }
}
