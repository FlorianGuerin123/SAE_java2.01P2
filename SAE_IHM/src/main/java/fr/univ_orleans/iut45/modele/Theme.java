import java.util.ArrayList;
import java.util.List;

public class Theme {

    private int idTheme;
    private String nomTheme;
    private Theme parent;
    private List<Theme> sousThemes;

    public Theme(int idTheme, String nomTheme){
        this.idTheme = idTheme;
        this.nomTheme = nomTheme;
        this.sousThemes = new ArrayList<>();
    }

    public Theme(int idTheme, String nomTheme, Theme parent){
        this(idTheme, nomTheme);
        this.parent = parent;
        if (parent != null) parent.ajouterSousTheme(this);
    }

    public void ajouterSousTheme(Theme sousTheme){
        if (!sousThemes.contains(sousTheme)) sousThemes.add(sousTheme);
    }

    public int getIdTheme(){
        return idTheme;
    }

    public String getNomTheme(){
        return nomTheme;
    }

    public Theme getParent(){
        return parent;
    }

    public List<Theme> getSousThemes(){
        return sousThemes;
    }

    @Override
    public String toString(){
        return "idtheme: " + idTheme + " - nom: " + nomTheme + (parent != null ? " - parent: " + parent.nomTheme : "") + " - nbSousThèmes: " + sousThemes.size();
    }

}
