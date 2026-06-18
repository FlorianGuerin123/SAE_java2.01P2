package fr.univ_orleans.iut45;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import fr.univ_orleans.iut45.modele.BoiteComposee;
import fr.univ_orleans.iut45.modele.Categorie;
import fr.univ_orleans.iut45.modele.CollectionPersonnelle;
import fr.univ_orleans.iut45.modele.ContenuFigurine;
import fr.univ_orleans.iut45.modele.ContenuPiece;
import fr.univ_orleans.iut45.modele.Couleur;
import fr.univ_orleans.iut45.modele.Figurine;
import fr.univ_orleans.iut45.modele.PartieAdmin;
import fr.univ_orleans.iut45.modele.Piece;
import fr.univ_orleans.iut45.modele.Theme;