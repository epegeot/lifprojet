package VueControleur;

import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;


import modele.*;


/** Cette classe a deux fonctions :
 *  (1) Vue : proposer une représentation graphique de l'application (cases graphiques, etc.)
 *  (2) Controleur : écouter les évènements clavier et déclencher le traitement adapté sur le modèle (flèches direction Pacman, etc.))
 *
 */
public class VueControleur extends JFrame implements Observer {
    private Jeu jeu; // référence sur une classe de modèle : permet d'accéder aux données du modèle pour le rafraichissement, permet de communiquer les actions clavier (ou souris)

    private int sizeX; // taille de la grille affichée
    private int sizeY;

    // icones affichées dans la grille
    private ImageIcon icoHero;
    private ImageIcon icoVide;
    private ImageIcon icoMur;
    private ImageIcon icoBloc;

    private ImageIcon icoPiece;


    private JLabel[][] tabJLabel; // cases graphique (au moment du rafraichissement, chaque case va être associée à une icône, suivant ce qui est présent dans le modèle)


    public VueControleur(Jeu _jeu) {
        sizeX = jeu.SIZE_X;
        sizeY = _jeu.SIZE_Y;
        jeu = _jeu;

        chargerLesIcones();
        placerLesComposantsGraphiques();
        ajouterEcouteurClavier();

        jeu.addObserver(this);

        mettreAJourAffichage();

    }

    private void ajouterEcouteurClavier() {
        addKeyListener(new KeyAdapter() { // new KeyAdapter() { ... } est une instance de classe anonyme, il s'agit d'un objet qui correspond au controleur dans MVC
            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {  // on regarde quelle touche a été pressée

                    case KeyEvent.VK_LEFT : jeu.deplacerHeros(Direction.gauche); break;
                    case KeyEvent.VK_RIGHT : jeu.deplacerHeros(Direction.droite); break;
                    case KeyEvent.VK_DOWN : jeu.deplacerHeros(Direction.bas); break;
                    case KeyEvent.VK_UP : jeu.deplacerHeros(Direction.haut); break;


                }
            }
        });
    }


    private void chargerLesIcones() {
        icoHero = chargerIcone("Images/Pacman.png");
        icoVide = chargerIcone("Images/Vide.png");
        icoMur = chargerIcone("Images/Mur.png");
        icoBloc = chargerIcone("Images/Colonne.png");
        icoPiece = chargerIcone("Images/Piece.png");
    }

    private ImageIcon chargerIcone(String urlIcone) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(new File(urlIcone));
        } catch (IOException ex) {
            Logger.getLogger(VueControleur.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return new ImageIcon(image);
    }

    public JMenuBar creerMenu() {
        JMenuBar menuBar;
        JMenu menu;
        // Création de la barre de menu
        menuBar = new JMenuBar();

        // Création du menu "Niveau"
        menu = new JMenu("Niveau");

        // Ajout des éléments de menu de niveau 1 à 5
        for (int i = 1; i <= 5; i++) {
            JMenuItem menuItem = new JMenuItem("Niveau " + i);
            final int level = i; // Utilisé dans l'écouteur d'événement
            menuItem.addActionListener(e -> {
                String fileName = "";
                switch (level){
                    case 1:
                        fileName = "../game_files/niveau1.xsb";
                        break;
                    case 2:
                        fileName = "../game_files/niveau2.xsb";
                        break;
                    case 3:
                        fileName = "../game_files/niveau3.xsb";
                        break;
                    case 4:
                        fileName = "../game_files/niveau4.xsb";
                        break;
                    case 5:
                        fileName = "../game_files/niveau5.xsb";
                        break;
                    default:
                        break;
                }
                //TODO
                //Jeu j = new Jeu(fileName);
            });
            menu.add(menuItem);
        }

        JMenuItem menuPersonnalisee = new JMenuItem("Personnalisée");
        menuPersonnalisee.addActionListener(e -> {
            String fileName = JOptionPane.showInputDialog(null, "Entrez le nom du fichier :");
            if (fileName != null && !fileName.isEmpty()) {
                //TODO
                //Jeu j = new Jeu(fileName);
            }
        });
        menu.add(menuPersonnalisee);

        // Ajout du menu à la barre de menu
        menuBar.add(menu);
        return menuBar;
    }

    private void placerLesComposantsGraphiques() {
        setTitle("Sokoban");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // permet de terminer l'application à la fermeture de la fenêtre

        JComponent grilleJLabels = new JPanel(new GridLayout(sizeY, sizeX)); // grilleJLabels va contenir les cases graphiques et les positionner sous la forme d'une grille

        tabJLabel = new JLabel[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                JLabel jlab = new JLabel();
                tabJLabel[x][y] = jlab; // on conserve les cases graphiques dans tabJLabel pour avoir un accès pratique à celles-ci (voir mettreAJourAffichage() )
                grilleJLabels.add(jlab);
            }
        }

        // Création de l'instance du menu et obtention de sa barre de menu
        JMenuBar menuBar = this.creerMenu();

        // Ajout de la barre de menu à la fenêtre principale
        setJMenuBar(menuBar);

        add(grilleJLabels);
    }



    /**
     * Il y a une grille du côté du modèle ( jeu.getGrille() ) et une grille du côté de la vue (tabJLabel)
     */
    private void mettreAJourAffichage() {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                Case c = jeu.getGrille()[x][y];
                if (c != null) {
                    Entite e = c.getEntite();
                    if (e != null) {
                        if (e instanceof Heros) {
                            tabJLabel[x][y].setIcon(icoHero);
                        } else if (e instanceof Bloc) {
                            tabJLabel[x][y].setIcon(icoBloc);
                        }
                    } else {
                        if (c instanceof Mur) {
                            tabJLabel[x][y].setIcon(icoMur);
                        } else if (c instanceof Piece) {
                            tabJLabel[x][y].setIcon(icoPiece);
                        } else if (c instanceof Vide) {
                            tabJLabel[x][y].setIcon(icoVide);
                        }
                    }
                }
            }
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        if (jeu.jeuTermine()) {
            System.out.println("Jeu terminé en " + jeu.getNombreCoups() + " coups.");
            System.exit(1);
        }

        mettreAJourAffichage();
        /*

        // récupérer le processus graphique pour rafraichir
        // (normalement, à l'inverse, a l'appel du modèle depuis le contrôleur, utiliser un autre processus, voir classe Executor)


        SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        mettreAJourAffichage();
                    }
                });
        */

    }
}
