package fr.univ_orleans.iut45.modele;

import java.util.ArrayList;
import java.util.List;

public class BoiteComposee extends Boite{

    private boolean complete;
    private boolean collection;
    private boolean personnalisee;
    private List<PieceRetiree> piecesRetirees;
    private List<FigurineRetiree> figurinesRetirees;

    public static class PieceRetiree {
        public String numPiece;
        public int idCoul;
        public int quantiteRetiree;

        public PieceRetiree(String numPiece, int idCoul, int quantiteRetiree){
            this.numPiece= numPiece;
            this.idCoul= idCoul;
            this.quantiteRetiree = quantiteRetiree;
        }
    }

    public static class FigurineRetiree {
        public String idFig;
        public int quantiteRetiree;

        public FigurineRetiree(String idFig, int quantiteRetiree) {
            this.idFig= idFig;
            this.quantiteRetiree = quantiteRetiree;
        }
    }

    public BoiteComposee(String numBoite, String nomBoite, int annee, int nbPieces,Theme theme, boolean complete, boolean collection){
        super(numBoite, nomBoite, annee, nbPieces, theme);
        this.complete = complete;
        this.collection = collection;
        this.personnalisee = false;
        this.piecesRetirees = new ArrayList<>();
        this.figurinesRetirees = new ArrayList<>();
    }

    public BoiteComposee(String numBoite, String nomBoite, int annee, int nbPieces,Theme theme, boolean complete, boolean collection, boolean personnalisee){
        super(numBoite, nomBoite, annee, nbPieces, theme);
        this.complete = complete;
        this.collection = collection;
        this.personnalisee = personnalisee;
        this.piecesRetirees = new ArrayList<>();
        this.figurinesRetirees = new ArrayList<>();
    }

   
    public boolean estComplete(){ 
        return complete; 
    }
    public boolean estDansCollection(){ 
        return collection;
    }
    public boolean estPersonnalisee(){ 
        return personnalisee; 
    }
    public List<PieceRetiree> getPiecesRetirees(){ 
        return piecesRetirees;
    }
    public List<FigurineRetiree> getFigurinesRetirees(){ 
        return figurinesRetirees; 
    }

    
    public void setComplete(boolean complete){ 
        this.complete = complete; 
    }
    public void setCollection(boolean collection){
         this.collection = collection; 
    }

    
    
    public void enregistrerPieceRetiree(String numPiece, int idCoul){
        for (PieceRetiree pr : piecesRetirees) {
            if (pr.numPiece.equals(numPiece) && pr.idCoul == idCoul) {
                pr.quantiteRetiree++;
                return;
            }
        }
        piecesRetirees.add(new PieceRetiree(numPiece, idCoul, 1));
    }

    public void enregistrerFigurineRetiree(String idFig) {
        for (FigurineRetiree fr : figurinesRetirees) {
            if (fr.idFig.equals(idFig)) {
                fr.quantiteRetiree++;
                return;
            }
        }
        figurinesRetirees.add(new FigurineRetiree(idFig, 1));
    }

    @Override
    public String toString() {
        String res = super.toString()+ ", complete=" + complete+ ", collection=" + collection;
        if (personnalisee) {
            res += ", personnalisee=" + personnalisee;
        }
        return res;
    }
}