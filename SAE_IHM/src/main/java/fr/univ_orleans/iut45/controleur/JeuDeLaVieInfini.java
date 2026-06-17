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

    private record Cellule(int x, int y) {}

    private Set<Cellule> vivantes = new HashSet<>();
    
    // Caméra et Zoom
    private double cameraX = 0;
    private double cameraY = 0;
    private double derniereSourisX = 0;
    private double derniereSourisY = 0;
    private double zoom = 1.0; 
    
    private boolean enLecture = false;
    private boolean modeEffacement = false; // <-- NOUVEAU : Pinceau ou Gomme
    private final int TAILLE_CELLULE = 20;

    public void lancer() {
        Stage stage = new Stage();
        stage.setTitle("Jeu de la Vie Infini  |  ESPACE = Play/Pause  |  CLIC GAUCHE = Pinceau/Gomme  |  CLIC DROIT = Déplacer  |  MOLETTE = Zoom");

        Pane racine = new Pane();
        racine.setStyle("-fx-background-color: #1E1E2E;");
        
        Canvas canvas = new Canvas();
        canvas.widthProperty().bind(racine.widthProperty());
        canvas.heightProperty().bind(racine.heightProperty());
        racine.getChildren().add(canvas);

        stage.setMaximized(true);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        // --- GESTION DES CLICS SOURIS ---
        canvas.setOnMousePressed(e -> {
            derniereSourisX = e.getX();
            derniereSourisY = e.getY();
            
            if (e.getButton() == MouseButton.PRIMARY) {
                Cellule c = obtenirCellule(e.getX(), e.getY());
                // Si on clique sur une case vivante, on passe en mode Gomme. Sinon, Pinceau.
                modeEffacement = vivantes.contains(c);
                modifierCellule(e.getX(), e.getY(), modeEffacement);
                dessiner(gc, canvas.getWidth(), canvas.getHeight());
            }
        });

        // --- GESTION DU GLISSEMENT ---
        canvas.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                cameraX += (e.getX() - derniereSourisX);
                cameraY += (e.getY() - derniereSourisY);
                dessiner(gc, canvas.getWidth(), canvas.getHeight());
            } else if (e.getButton() == MouseButton.PRIMARY) {
                // On trace une ligne continue pour boucher les trous de la souris
                dessinerLigne(derniereSourisX, derniereSourisY, e.getX(), e.getY(), modeEffacement);
                dessiner(gc, canvas.getWidth(), canvas.getHeight());
            }
            // On mémorise la position pour le prochain mouvement
            derniereSourisX = e.getX();
            derniereSourisY = e.getY();
        });

        // --- GESTION DU ZOOM ---
        canvas.setOnScroll(e -> {
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

        // Contrôle Play/Pause
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

    // --- NOUVELLES MÉTHODES POUR LE PINCEAU FLUIDE ---

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
            vivantes.add(c);
        }
    }

    private void dessinerLigne(double x1, double y1, double x2, double y2, boolean effacer) {
        double distance = Math.hypot(x2 - x1, y2 - y1);
        // On vérifie une case tous les 5 pixels pour être sûr de ne rater aucune cellule
        int nbEtapes = (int) Math.max(1, distance / 5); 
        
        for (int i = 0; i <= nbEtapes; i++) {
            double fraction = (double) i / nbEtapes;
            double x = x1 + fraction * (x2 - x1);
            double y = y1 + fraction * (y2 - y1);
            modifierCellule(x, y, effacer);
        }
    }

    // --- AFFICHAGE ET LOGIQUE ---

    private void dessiner(GraphicsContext gc, double largeur, double hauteur) {
        gc.clearRect(0, 0, largeur, hauteur);
        
        double tailleActuelle = TAILLE_CELLULE * zoom;

        // 1. Quadrillage
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

        // 2. Cellules vivantes
        gc.setFill(Color.web("#FF4D6A"));
        double marge = (tailleActuelle > 5) ? 1.0 : 0.0; 
        
        for (Cellule c : vivantes) {
            double ecranX = c.x * tailleActuelle + cameraX;
            double ecranY = c.y * tailleActuelle + cameraY;
            
            if (ecranX >= -tailleActuelle && ecranX <= largeur && ecranY >= -tailleActuelle && ecranY <= hauteur) {
                gc.fillRect(ecranX, ecranY, tailleActuelle - marge, tailleActuelle - marge);
            }
        }
        
        // UI
        gc.setFill(Color.WHITE);
        gc.fillText(enLecture ? "▶ LECTURE" : "⏸ PAUSE", 20, 30);
        gc.fillText("Zoom : " + Math.round(zoom * 100) + "%", 20, 50);
    }

    private void calculerGenerationSuivante() {
        Set<Cellule> prochaines = new HashSet<>();
        Set<Cellule> cellulesAVerifier = new HashSet<>();

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