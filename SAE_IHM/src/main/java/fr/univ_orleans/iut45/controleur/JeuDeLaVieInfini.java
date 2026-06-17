package fr.univ_orleans.iut45.controleur;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

public class JeuDeLaVieInfini {

    // On utilise un Record pour stocker facilement les coordonnées (x,y)
    private record Cellule(int x, int y) {}

    private Set<Cellule> vivantes = new HashSet<>();
    
    // Variables de la caméra et du zoom
    private double cameraX = 0;
    private double cameraY = 0;
    private double derniereSourisX = 0;
    private double derniereSourisY = 0;
    private double zoom = 1.0; // <-- NOUVEAU : Variable de zoom (1.0 = 100%)
    
    private boolean enLecture = false;
    private final int TAILLE_CELLULE = 20;

    public void lancer() {
        Stage stage = new Stage();
        stage.setTitle("Jeu de la Vie Infini  |  ESPACE = Play/Pause  |  CLIC GAUCHE = Dessiner  |  CLIC DROIT = Déplacer  |  MOLETTE = Zoom");

        Pane racine = new Pane();
        racine.setStyle("-fx-background-color: #1E1E2E;");
        
        Canvas canvas = new Canvas();
        canvas.widthProperty().bind(racine.widthProperty());
        canvas.heightProperty().bind(racine.heightProperty());
        racine.getChildren().add(canvas);

        stage.setMaximized(true);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Contrôles de la souris (Clics)
        canvas.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                derniereSourisX = e.getX();
                derniereSourisY = e.getY();
            } else if (e.getButton() == MouseButton.PRIMARY) {
                ajouterCellule(e.getX(), e.getY());
                dessiner(gc, canvas.getWidth(), canvas.getHeight());
            }
        });

        // Contrôles de la souris (Glisser)
        canvas.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                cameraX += (e.getX() - derniereSourisX);
                cameraY += (e.getY() - derniereSourisY);
                derniereSourisX = e.getX();
                derniereSourisY = e.getY();
                dessiner(gc, canvas.getWidth(), canvas.getHeight());
            } else if (e.getButton() == MouseButton.PRIMARY) {
                ajouterCellule(e.getX(), e.getY());
                dessiner(gc, canvas.getWidth(), canvas.getHeight());
            }
        });

        // --- Contrôle de la molette pour le ZOOM ---
        canvas.setOnScroll(e -> {
            // Sécurité : on ignore les micro-mouvements à zéro des trackpads
            if (e.getDeltaY() == 0) return; 

            double ancienZoom = zoom;
            
            if (e.getDeltaY() > 0) {
                zoom *= 1.1; 
            } else {
                zoom /= 1.1; 
            }
            
            if (zoom < 0.1) zoom = 0.1;   
            if (zoom > 10.0) zoom = 10.0; 
            
            double facteur = zoom / ancienZoom;
            cameraX = e.getX() - (e.getX() - cameraX) * facteur;
            cameraY = e.getY() - (e.getY() - cameraY) * facteur;
            
            dessiner(gc, canvas.getWidth(), canvas.getHeight());
        });

        // Contrôles clavier (Espace pour Play/Pause)
        racine.setFocusTraversable(true);
        racine.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE) {
                enLecture = !enLecture;
            }
        });

        // Boucle principale
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

    private void ajouterCellule(double ecranX, double ecranY) {
        // On prend en compte le zoom pour savoir sur quelle case on a cliqué
        double tailleActuelle = TAILLE_CELLULE * zoom;
        int grilleX = (int) Math.floor((ecranX - cameraX) / tailleActuelle);
        int grilleY = (int) Math.floor((ecranY - cameraY) / tailleActuelle);
        
        Cellule c = new Cellule(grilleX, grilleY);
        if (vivantes.contains(c)) vivantes.remove(c);
        else vivantes.add(c);
    }

    private void dessiner(GraphicsContext gc, double largeur, double hauteur) {
        gc.clearRect(0, 0, largeur, hauteur);
        
        // On calcule la taille affichée avec le zoom
        double tailleActuelle = TAILLE_CELLULE * zoom;

        // 1. Dessiner le quadrillage infini
        gc.setStroke(Color.web("#333344"));
        gc.setLineWidth(1);
        
        double decalageX = cameraX % tailleActuelle;
        if (decalageX < 0) decalageX += tailleActuelle; // Sécurité pour les nombres négatifs
        
        double decalageY = cameraY % tailleActuelle;
        if (decalageY < 0) decalageY += tailleActuelle;
        
        // On ne dessine le quadrillage que si le zoom n'est pas trop lointain (sinon ça fait un bloc gris)
        if (tailleActuelle > 3) {
            for (double x = decalageX; x < largeur; x += tailleActuelle) {
                gc.strokeLine(x, 0, x, hauteur);
            }
            for (double y = decalageY; y < hauteur; y += tailleActuelle) {
                gc.strokeLine(0, y, largeur, y);
            }
        }

        // 2. Dessiner les cellules vivantes
        gc.setFill(Color.web("#FF4D6A"));
        
        // La marge évite que les blocs soient collés, sauf si on a trop dézoomé
        double marge = (tailleActuelle > 5) ? 1.0 : 0.0; 
        
        for (Cellule c : vivantes) {
            double ecranX = c.x * tailleActuelle + cameraX;
            double ecranY = c.y * tailleActuelle + cameraY;
            
            // Optimisation : On ne dessine que ce qui est visible à l'écran
            if (ecranX >= -tailleActuelle && ecranX <= largeur && ecranY >= -tailleActuelle && ecranY <= hauteur) {
                gc.fillRect(ecranX, ecranY, tailleActuelle - marge, tailleActuelle - marge);
            }
        }
        
        // Indicateur d'état
        gc.setFill(Color.WHITE);
        gc.fillText(enLecture ? "▶ LECTURE" : "⏸ PAUSE", 20, 30);
        gc.fillText("Zoom : " + Math.round(zoom * 100) + "%", 20, 50);
    }

    private void calculerGenerationSuivante() {
        Set<Cellule> prochaines = new HashSet<>();
        Set<Cellule> cellulesAVerifier = new HashSet<>();

        // On ne vérifie que les cellules vivantes et leurs voisins directs
        for (Cellule c : vivantes) {
            cellulesAVerifier.add(c);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    cellulesAVerifier.add(new Cellule(c.x + dx, c.y + dy));
                }
            }
        }

        for (Cellule c : cellulesAVerifier) {
            int voisins = 0;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if ((dx != 0 || dy != 0) && vivantes.contains(new Cellule(c.x + dx, c.y + dy))) {
                        voisins++;
                    }
                }
            }

            if (vivantes.contains(c) && (voisins == 2 || voisins == 3)) {
                prochaines.add(c);
            } else if (!vivantes.contains(c) && voisins == 3) {
                prochaines.add(c);
            }
        }

        vivantes = prochaines;
    }
}