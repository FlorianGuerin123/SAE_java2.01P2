import java.util.List;
import java.util.ArrayList;


public abstract class Boite {

    private String numBoite;
    private String nomBoite;
    private int annee;
    private int nbPieces;
    private Theme theme;

    private List<ContenuPiece> pieces = new ArrayList<>();
    private List<ContenuFigurine> figurines  = new ArrayList<>();
    private List<ContenuBoite> sousBoites = new ArrayList<>();

    public Boite(String numBoite, String nomBoite, int annee, int nbPieces, Theme theme) {
        this.numBoite = numBoite;
        this.nomBoite = nomBoite;
        this.annee = annee;
        this.nbPieces = nbPieces;
        this.theme = theme;
    }

     public String getNumBoite(){
        return numBoite;
    }

    public String getNomBoite(){
        return nomBoite;
    }

    public int getAnnee(){
        return annee;
    }

    public int getNbPieces(){
        return nbPieces;
    }

    public Theme getTheme(){
        return theme;
    }

    public List<ContenuPiece> getPieces(){
        return pieces;
    }

    public List<ContenuFigurine> getFigurines(){
        return figurines;
    }

    public List<ContenuBoite> getSousBoites(){
        return sousBoites;
    }
    

    public void ajouterPiece(ContenuPiece cp){ 
        pieces.add(cp); 
    }
    public void ajouterFigurine(ContenuFigurine cf){
        figurines.add(cf); 
    }
    public void ajouterSousBoite(ContenuBoite cb){
        sousBoites.add(cb);
    }

    public void incrementerNbPieces(){
        nbPieces++;
    }


    @Override
    public boolean equals(Object o){
        if (this == o){ 
            return true;
        }
        if (!(o instanceof Boite)) {
            return false;
        }
        Boite boite = (Boite) o;
        return this.numBoite==boite.numBoite;
    }

    @Override
    public int hashCode(){ 
        return numBoite.hashCode();
    }

    @Override
    public String toString() {
        return "Boite : " + numBoite + " , nomBoite = " + nomBoite + " , annee = " + annee + " , nbPieces = " + nbPieces + " , theme = " + theme.getNomTheme();
    }

}