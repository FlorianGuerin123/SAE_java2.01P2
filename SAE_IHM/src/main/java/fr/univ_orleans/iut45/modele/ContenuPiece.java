public class ContenuPiece{
    private Piece piece;
    private int quantite;
    private boolean enSupplement;

    public ContenuPiece(Piece piece, int quantite, boolean enSupplement){
        this.piece = piece;
        this.quantite = quantite;
        this.enSupplement = enSupplement;
    }

    public Piece getPiece(){ 
        return this.piece; 
    
    }
    public int getQuantite(){ 
        return this.quantite; 
    }
    public boolean estEnSupplement(){ 
        return this.enSupplement; 
    }

    @Override
    public String toString(){
        return "" + this.quantite + this.piece.toString() + (this.enSupplement ? " [SUPPLÉMENT]" : "");
    }
}