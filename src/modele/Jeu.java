/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;


import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Scanner;


public class Jeu extends Observable {
    public static final int SIZE_X = 30;
    public static final int SIZE_Y = 30;

    private Heros heros;

    private HashMap<Case, Point> map = new HashMap<Case, Point>(); // permet de récupérer la position d'une case à partir de sa référence
    private Case[][] grilleEntites = new Case[SIZE_X][SIZE_Y]; // permet de récupérer une case à partir de ses coordonnées

    private int nombreCoups = 0;
    
    private GestionnaireScores gestionnaireScores;
    private String scoresPath;

    public Jeu() {
        initialisationNiveau();
    }

    public Jeu(String path) {
        charger_jeu(path);
    }

    /**
     * Charge un fichier format .xsb pour initialiser le jeu
     * @param path the path to the file
     * @todo : Faire une méthode pour charger un niveau sauvegardé en .xsbe
     */
    public void charger_jeu(String path) {
        // Charger la grille
        System.out.println(path);
        File f = new File(path);
        Scanner fileScanner;
        try {
            fileScanner = new Scanner(f);
        } catch (FileNotFoundException e) {
            System.out.println("Fichier de jeu non trouvé");
            System.exit(1);
            return; // Obligé de le mentionner sinon le langage pense qu'on est pas sortis
        }
        fileScanner.useDelimiter("\n");
        
        remplirGrilleDeVide();
        int y = 0;
        while (fileScanner.hasNext()) {
            int x = 0;

            String l = fileScanner.next();
            Scanner lineScanner = new Scanner(l);
            lineScanner.useDelimiter("");
            while (lineScanner.hasNext()) {
                String c = lineScanner.next();
                // Types cases
                if (c.equals("#")) {
                    addCase(new Mur(this), x, y);
                } else if (c.equals(".")) {
                    addCase(new Piece(this), x, y);
                } else { // Toute case (non mur) ET (non pièce) est vide
                    addCase(new Vide(this), x, y);
                }

                // Types entités
                if (c.equals("@")) {
                    heros = new Heros(this, grilleEntites[x][y]);
                } else if (c.equals("$")) {
                    new Bloc(this, grilleEntites[x][y]);
                }
                x++;
            }
            y++;
            lineScanner.close();
        }
        fileScanner.close();

        // Charger les scores
        scoresPath = path.replace(".xsb", ".xsb.scores");
        gestionnaireScores = new GestionnaireScores(scoresPath);
        nombreCoups = 0;

        setChanged();
        notifyObservers();
    }

    public Case[][] getGrille() {
        return grilleEntites;
    }
    
    public Heros getHeros() {
        return heros;
    }

    public int getNombreCoups() {
        return this.nombreCoups;
    }
    public void deplacerHeros(Direction d) {
        this.nombreCoups++;
        heros.avancerDirectionChoisie(d);
        setChanged();
        notifyObservers();
    }

    private void remplirGrilleDeVide() {
        map = new HashMap<Case, Point>();
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                this.addCase(new Vide(this), x, y);
            }
        }
    }
    
    private void initialisationNiveau() {
        // murs extérieurs horizontaux
        for (int x = 0; x < 20; x++) {
            addCase(new Mur(this), x, 0);
            addCase(new Mur(this), x, 9);
        }

        // murs extérieurs verticaux
        for (int y = 1; y < 9; y++) {
            addCase(new Mur(this), 0, y);
            addCase(new Mur(this), 19, y);
        }

        for (int x = 1; x < 19; x++) {
            for (int y = 1; y < 9; y++) {
                addCase(new Vide(this), x, y);
            }

        }

        heros = new Heros(this, grilleEntites[4][4]);
        Bloc b = new Bloc(this, grilleEntites[6][6]);
    }

    private void addCase(Case e, int x, int y) {
        grilleEntites[x][y] = e;
        map.put(e, new Point(x, y));
    }
    
    /** Si le déplacement de l'entité est autorisé (pas de mur ou autre entité), il est réalisé
     * Sinon, rien n'est fait.
     */
    public boolean deplacerEntite(Entite e, Direction d) {
        boolean retour = true;
        
        Point pCourant = map.get(e.getCase());
        
        Point pCible = calculerPointCible(pCourant, d);

        if (contenuDansGrille(pCible)) {
            Entite eCible = caseALaPosition(pCible).getEntite();
            if (eCible != null) {
                eCible.pousser(d);
            }

            // si la case est libérée
            if (caseALaPosition(pCible).peutEtreParcouru()) {
                e.getCase().quitterLaCase();
                caseALaPosition(pCible).entrerSurLaCase(e);

            } else {
                retour = false;
            }

        } else {
            retour = false;
        }

        return retour;
    }
    
    private Point calculerPointCible(Point pCourant, Direction d) {
        Point pCible = null;
        
        switch(d) {
            case haut: pCible = new Point(pCourant.x, pCourant.y - 1); break;
            case bas : pCible = new Point(pCourant.x, pCourant.y + 1); break;
            case gauche : pCible = new Point(pCourant.x - 1, pCourant.y); break;
            case droite : pCible = new Point(pCourant.x + 1, pCourant.y); break;     
            
        }
        
        return pCible;
    }
    
   
    /** Indique si p est contenu dans la grille
     */
    private boolean contenuDansGrille(Point p) {
        return p.x >= 0 && p.x < SIZE_X && p.y >= 0 && p.y < SIZE_Y;
    }
    
    private Case caseALaPosition(Point p) {
        Case retour = null;
        
        if (contenuDansGrille(p)) {
            retour = grilleEntites[p.x][p.y];
        }
        
        return retour;
    }

    public boolean jeuTermine() {
        // Parcours de toutes les cases de la grille
        for (int x = 0; x < SIZE_X; x++) {
            for (int y = 0; y < SIZE_Y; y++) {
                Case caseCourante = grilleEntites[x][y];
                if (caseCourante.getEntite() instanceof Bloc) {
                    if (!(caseCourante instanceof Piece)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
