package fr.univ_orleans.iut45.controleur;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JeuDeLaVieInfini {

    private record Cellule(int x, int y) {}

    private Map<Cellule, Color> vivantes = new HashMap<>();
    
    private double cameraX = 0;
    private double cameraY = 0;
    private double derniereSourisX = 0;
    private double derniereSourisY = 0;
    private double zoom = 1.0; 
    
    private boolean enLecture = false;
    private boolean modeEffacement = false; 
    private final int TAILLE_CELLULE = 20;
    
    // Le pinceau manuel est maintenant blanc (plus propre que le rouge)
    private final Color COULEUR_DEFAUT = Color.WHITE;

    public void lancer() {
        Stage stage = new Stage();
        stage.setTitle("Jeu de la Vie Infini  |  ESPACE = Play/Pause  |  CLIC GAUCHE = Pinceau/Gomme  |  CLIC DROIT = Déplacer  |  MOLETTE = Zoom");

        Pane racine = new Pane();
        racine.setStyle("-fx-background-color: #1E1E2E;");
        
        Canvas canvas = new Canvas();
        canvas.widthProperty().bind(racine.widthProperty());
        canvas.heightProperty().bind(racine.heightProperty());
        
        Button btnImage = new Button("🖼 Importer Image");
        btnImage.setFocusTraversable(false); 
        btnImage.setStyle("-fx-background-color: #4bdb6a; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-font-size: 14; -fx-padding: 8 15 8 15;");
        btnImage.setLayoutY(20);
        btnImage.layoutXProperty().bind(racine.widthProperty().subtract(180));
        
        btnImage.setOnAction(e -> importerImage(stage, canvas));

        racine.getChildren().addAll(canvas, btnImage);
        stage.setMaximized(true);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        canvas.setOnMousePressed(e -> {
            derniereSourisX = e.getX();
            derniereSourisY = e.getY();
            
            if (e.getButton() == MouseButton.PRIMARY) {
                Cellule c = obtenirCellule(e.getX(), e.getY());
                modeEffacement = vivantes.containsKey(c); 
                modifierCellule(e.getX(), e.getY(), modeEffacement);
                dessiner(gc, canvas.getWidth(), canvas.getHeight());
            }
        });

        canvas.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                cameraX += (e.getX() - derniereSourisX);
                cameraY += (e.getY() - derniereSourisY);
                dessiner(gc, canvas.getWidth(), canvas.getHeight());
            } else if (e.getButton() == MouseButton.PRIMARY) {
                dessinerLigne(derniereSourisX, derniereSourisY, e.getX(), e.getY(), modeEffacement);
                dessiner(gc, canvas.getWidth(), canvas.getHeight());
            }
            derniereSourisX = e.getX();
            derniereSourisY = e.getY();
        });

        canvas.setOnScroll(e -> {
            if (e.getDeltaY() == 0) return; 

            double ancienZoom = zoom;
            if (e.getDeltaY() > 0) zoom *= 1.1; 
            else zoom /= 1.1; 
            
            if (zoom < 0.1) zoom = 0.1;   
            if (zoom > 10.0) zoom = 10.0; 
            
            double facteur = zoom / ancienZoom;
            cameraX = e.getX() - (e.getX() - cameraX) * facteur;
            cameraY = e.getY() - (e.getY() - cameraY) * facteur;
            
            dessiner(gc, canvas.getWidth(), canvas.getHeight());
        });

        racine.setFocusTraversable(true);
        racine.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                enLecture = !enLecture;
            }
        });

        AnimationTimer timer = new AnimationTimer() {
            private long dernierUpdate = 0;

            @Override
            public void handle(long maintenant) {
                if (enLecture && maintenant - dernierUpdate >= 100_000_000) {
                    calculerGenerationSuivante();
                    dernierUpdate = maintenant;
                }
                dessiner(gc, canvas.getWidth(), canvas.getHeight());
            }
        };

        Scene scene = new Scene(racine, 1000, 700);
        stage.setScene(scene);
        stage.show();
        
        racine.requestFocus();
        timer.start();
        stage.setOnCloseRequest(e -> timer.stop()); 
    }

    private void importerImage(Stage stage, Canvas canvas) {
        javafx.stage.FileChooser selecteur = new javafx.stage.FileChooser();
        selecteur.setTitle("Choisir une image pour le Jeu de la Vie");
        selecteur.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.bmp"));
        
        java.io.File fichier = selecteur.showOpenDialog(stage);

        if (fichier != null) {
            try {
                javafx.scene.image.Image image = new javafx.scene.image.Image(fichier.toURI().toString());
                javafx.scene.image.PixelReader lecteur = image.getPixelReader();
                
                int largeurImg = (int) image.getWidth();
                int hauteurImg = (int) image.getHeight();
                
                int limiteMax = 150; 
                int pas = Math.max(1, Math.max(largeurImg / limiteMax, hauteurImg / limiteMax));
                
                double tailleActuelle = TAILLE_CELLULE * zoom;
                int centreEcranX = (int) (((canvas.getWidth() / 2) - cameraX) / tailleActuelle);
                int centreEcranY = (int) (((canvas.getHeight() / 2) - cameraY) / tailleActuelle);
                
                int decalageX = centreEcranX - ((largeurImg / pas) / 2);
                int decalageY = centreEcranY - ((hauteurImg / pas) / 2);
                
                for (int y = 0; y < hauteurImg; y += pas) {
                    for (int x = 0; x < largeurImg; x += pas) {
                        Color couleur = lecteur.getColor(x, y);
                        
                        if (couleur.getOpacity() > 0.5 && couleur.getBrightness() < 0.9) {
                            vivantes.put(new Cellule(decalageX + (x / pas), decalageY + (y / pas)), couleur);
                        }
                    }
                }
                
                enLecture = false; 
                dessiner(canvas.getGraphicsContext2D(), canvas.getWidth(), canvas.getHeight());
                
            } catch (Exception ex) {
                System.err.println("Impossible de charger l'image.");
            }
        }
    }

    private Cellule obtenirCellule(double ecranX, double ecranY) {
        double tailleActuelle = TAILLE_CELLULE * zoom;
        int grilleX = (int) Math.floor((ecranX - cameraX) / tailleActuelle);
        int grilleY = (int) Math.floor((ecranY - cameraY) / tailleActuelle);
        return new Cellule(grilleX, grilleY);
    }

    private void modifierCellule(double ecranX, double ecranY, boolean effacer) {
        Cellule c = obtenirCellule(ecranX, ecranY);
        if (effacer) {
            vivantes.remove(c);
        } else {
            vivantes.put(c, COULEUR_DEFAUT);
        }
    }

    private void dessinerLigne(double x1, double y1, double x2, double y2, boolean effacer) {
        double distance = Math.hypot(x2 - x1, y2 - y1);
        int nbEtapes = (int) Math.max(1, distance / 5); 
        
        for (int i = 0; i <= nbEtapes; i++) {
            double fraction = (double) i / nbEtapes;
            double x = x1 + fraction * (x2 - x1);
            double y = y1 + fraction * (y2 - y1);
            modifierCellule(x, y, effacer);
        }
    }

    private void dessiner(GraphicsContext gc, double largeur, double hauteur) {
        gc.clearRect(0, 0, largeur, hauteur);
        
        double tailleActuelle = TAILLE_CELLULE * zoom;

        gc.setStroke(Color.web("#333344"));
        gc.setLineWidth(1);
        
        double decalageX = cameraX % tailleActuelle;
        if (decalageX < 0) decalageX += tailleActuelle; 
        
        double decalageY = cameraY % tailleActuelle;
        if (decalageY < 0) decalageY += tailleActuelle;
        
        if (tailleActuelle > 3) {
            for (double x = decalageX; x < largeur; x += tailleActuelle) {
                gc.strokeLine(x, 0, x, hauteur);
            }
            for (double y = decalageY; y < hauteur; y += tailleActuelle) {
                gc.strokeLine(0, y, largeur, y);
            }
        }

        double marge = (tailleActuelle > 5) ? 1.0 : 0.0; 
        
        for (Map.Entry<Cellule, Color> entree : vivantes.entrySet()) {
            Cellule c = entree.getKey();
            Color couleur = entree.getValue();
            
            double ecranX = c.x * tailleActuelle + cameraX;
            double ecranY = c.y * tailleActuelle + cameraY;
            
            if (ecranX >= -tailleActuelle && ecranX <= largeur && ecranY >= -tailleActuelle && ecranY <= hauteur) {
                gc.setFill(couleur); 
                gc.fillRect(ecranX, ecranY, tailleActuelle - marge, tailleActuelle - marge);
            }
        }
        
        gc.setFill(Color.WHITE);
        gc.fillText(enLecture ? "▶ LECTURE" : "⏸ PAUSE", 20, 30);
        gc.fillText("Zoom : " + Math.round(zoom * 100) + "%", 20, 50);
    }

    private void calculerGenerationSuivante() {
        Map<Cellule, Color> prochaines = new HashMap<>();
        Set<Cellule> cellulesAVerifier = new HashSet<>();

        for (Cellule c : vivantes.keySet()) {
            cellulesAVerifier.add(c);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    cellulesAVerifier.add(new Cellule(c.x + dx, c.y + dy));
                }
            }
        }

        for (Cellule c : cellulesAVerifier) {
            int voisins = 0;
            double r = 0, g = 0, b = 0; // On prépare les variables pour mélanger les couleurs

            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx != 0 || dy != 0) {
                        Cellule voisin = new Cellule(c.x + dx, c.y + dy);
                        if (vivantes.containsKey(voisin)) {
                            voisins++;
                            // On récupère la couleur du parent pour le mélange
                            Color couleurVoisin = vivantes.get(voisin);
                            r += couleurVoisin.getRed();
                            g += couleurVoisin.getGreen();
                            b += couleurVoisin.getBlue();
                        }
                    }
                }
            }

            if (vivantes.containsKey(c) && (voisins == 2 || voisins == 3)) {
                // Elle survit : elle garde sa propre couleur intacte
                prochaines.put(c, vivantes.get(c));
            } else if (!vivantes.containsKey(c) && voisins == 3) {
                // HÉRITAGE ! Elle naît avec la moyenne exacte des couleurs de ses 3 parents
                prochaines.put(c, Color.color(r / 3.0, g / 3.0, b / 3.0));
            }
        }

        vivantes = prochaines;
    }
}