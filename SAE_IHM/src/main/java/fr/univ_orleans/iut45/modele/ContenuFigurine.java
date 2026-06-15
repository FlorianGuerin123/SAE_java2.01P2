public class ContenuFigurine{
    private Figurine figurine;
    private int quantite;

    public ContenuFigurine(Figurine figurine, int quantite){
        this.figurine = figurine;
        this.quantite = quantite;
    }

    public Figurine getFigurine(){ 
        return figurine; 
    }
    public int getQuantite(){ 
        return quantite; 
    }

    @Override
    public String toString(){
        return "" + quantite + figurine.toString();
    }
}
