public class Figurine {
    private String idFigurine;
    private String nomFigurine;
    private int nbParties;

    public Figurine(String idFigurine,String nomFigurine,int nbParties){
        this.idFigurine = idFigurine;
        this.nomFigurine = nomFigurine;
        this.nbParties = nbParties;
    }

    public String getIdFigurine(){ 
        return idFigurine; 
    }
    
    public String getNomFigurine(){ 
        return nomFigurine; 
    }
    public int getNombrePartie(){ 
        return nbParties; 
    }

    @Override
    public String toString(){
        return "Figurine{" +"idFigurine=" + idFigurine + ", nomFigurine='" + nomFigurine + '\'' +", nbParties=" + nbParties +'}';
    }
}
