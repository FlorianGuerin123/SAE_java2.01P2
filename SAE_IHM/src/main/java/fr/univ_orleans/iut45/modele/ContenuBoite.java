public class ContenuBoite{
    private Boite boite;
    private int quantite;

    public ContenuBoite(Boite boite, int quantite){
        this.boite = boite;
        this.quantite = quantite;
    }

    public Boite getBoite(){ 
        return boite; 
    }

    public int getQuantite(){ 
        return quantite; 
    }

    @Override
    public String toString(){
        return "" + quantite + boite.getNumBoite() + " – " + boite.getNomBoite();
    }
}
