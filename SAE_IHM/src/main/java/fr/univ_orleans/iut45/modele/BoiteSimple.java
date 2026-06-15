package fr.univ_orleans.iut45.modele;

public class BoiteSimple extends Boite{

    private boolean complete;

    
    public BoiteSimple(String numBoite, String nomBoite, int annee, int nbPieces,Theme theme, boolean complete){
        super(numBoite, nomBoite, annee, nbPieces, theme);
        this.complete      = complete;
    }

    public boolean estComplete(){ 
        return complete; 
    }

    public void setComplete(boolean complete){ 
        this.complete = complete; 
    }

    @Override
    public String toString() {
        return super.toString()+ " , complete = " + complete;
    }
}  
