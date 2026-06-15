public class Couleur{

    private int idCouleur;
    private String nomCouleur;
    private String rgb;
    private boolean transparent;

    public Couleur(int idCouleur,String nomCouleur, String rgb, boolean transparent){
        this.idCouleur = idCouleur;
        this.nomCouleur = nomCouleur;
        this.rgb = rgb;
        this.transparent = transparent;
    }

    public int getIdCouleur(){
        return idCouleur;
    }

    public String getNomCouleur(){
        return nomCouleur;
    }


    public String getRgb(){
        return rgb;
    }


    public boolean isTransparent(){
        return transparent;
    }

    @Override
    public String toString(){
        return "Couleur{" +"idCouleur=" + idCouleur +", nomCouleur='" + nomCouleur + '\'' +", rgb='" + rgb + '\'' +", transparent=" + transparent +'}';
    }
}


    