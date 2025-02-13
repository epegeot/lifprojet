package VueControleur;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
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
    private ImageIcon icoPorteFermee;
    private ImageIcon icoPorteOuverte;
    private ImageIcon icoBouton;
    private ImageIcon icoBoutonAppuye;


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
        icoHero = chargerIcone("Images/Heros.png");
        icoVide = chargerIcone("Images/Vide.png");
        icoMur = chargerIcone("Images/Mur.png");
        icoBloc = chargerIcone("Images/Caisse.png");
        icoPiece = chargerIcone("Images/Objectif.png");

        icoPorteFermee = chargerIcone("Images/Porte.png");
        icoPorteOuverte = chargerIcone("Images/Vide.png");
        icoBouton = chargerIcone("Images/Bouton.png");
        icoBoutonAppuye = chargerIcone("Images/Bouton.png");
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

    private JMenuBar creerMenu() {
        JMenuBar menuBar;
        JMenu menuNiveau;
        // Création de la barre de menu
        menuBar = new JMenuBar();

        // Création du menu "Niveau"
        menuNiveau = new JMenu("Niveau");

        // Ajout des éléments de menu de niveau 1 à 5
        for (int i = 1; i <= 5; i++) {
            JMenuItem optionMenu = new JMenuItem("Niveau " + i);
            final int level = i; // Utilisé dans l'écouteur d'événement
            optionMenu.addActionListener(e -> {
                String fileName = "";
                switch (level){
                    case 1:
                        fileName = "game_files/niveau1.xsb";
                        break;
                    case 2:
                        fileName = "game_files/niveau2.xsb";
                        break;
                    case 3:
                        fileName = "game_files/niveau3.xsb";
                        break;
                    case 4:
                        fileName = "game_files/niveau4.xsb";
                        break;
                    case 5:
                        fileName = "game_files/niveau5.xsb";
                        break;
                    default:
                        break;
                }
                jeu.charger_jeu(fileName);
            });
            menuNiveau.add(optionMenu);
        }

        JMenuItem optionPersonalisee = new JMenuItem("Personnalisé");
        optionPersonalisee.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String fileName = JOptionPane.showInputDialog(null, "Entrez le nom du fichier :");
                if (fileName != null && !fileName.isEmpty()) {
                    jeu.charger_jeu(fileName);
                }
            }
        });
        menuNiveau.add(optionPersonalisee);

        // PARTIE UNDO/REDO
        JMenu menuUndoRedo = new JMenu("Undo/Redo");

        JMenuItem optionUndo = new JMenuItem("Undo");
        optionUndo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jeu.undo();
            }
        });
        menuUndoRedo.add(optionUndo);

        JMenuItem optionRedo = new JMenuItem("Redo");
        optionRedo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jeu.redo();
            }
        });
        menuUndoRedo.add(optionRedo);

        menuBar.add(menuUndoRedo);
        // Ajout du menu à la barre de menu
        menuBar.add(menuNiveau);
        return menuBar;
    }

    private void placerLesComposantsGraphiques() {
        setTitle("Sokoban");
        setSize(1500, 1000);
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
                        if (c instanceof Piece) {
                            tabJLabel[x][y].setIcon(icoPiece);
                        } else if (c instanceof Bouton) {
                            tabJLabel[x][y].setIcon(icoBouton);
                        } else if (c instanceof Porte) {
                            if (((Porte) c).peutEtreParcouru()) {
                                tabJLabel[x][y].setIcon(icoPorteOuverte);
                            } else {
                                tabJLabel[x][y].setIcon(icoPorteFermee);
                            }
                        } else if (c instanceof Vide) { // On mets les types plus "génériques" après
                            tabJLabel[x][y].setIcon(icoVide);
                        } else if (c instanceof Mur) {
                            tabJLabel[x][y].setIcon(icoMur);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void update(Observable o, Object arg) {
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

        if (jeu.jeuTermine()) {
            // Jouer du son
            Son s = new Son("audio/win.wav");
            s.jouerSon();

            String pseudo = JOptionPane.showInputDialog(null, "Jeu terminé en " + jeu.getNombreCoups() + " coups.\nEntrez votre pseudo :");
            if (pseudo != null && !pseudo.isEmpty()) {
                System.out.println("Pseudo entré " + pseudo);
            }
            else {
                System.out.println("Jeu fermé.");
                System.exit(0);
            }
            int choix = JOptionPane.showOptionDialog(null,
                    "Que voulez-vous faire "+pseudo+"?", "Fin de jeu",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Sauvegarder Score", "Fermer"},
                    "Fermer");

            if (choix == JOptionPane.YES_OPTION) {
                jeu.gestionnaireScores.addScore(pseudo, jeu.getNombreCoups());
                jeu.gestionnaireScores.export(jeu.scoresPath);
                StringBuilder message = new StringBuilder();
                message.append("Meilleurs scores :\n");

                // Récupérer les trois premiers scores
                List<Score> topScores = jeu.gestionnaireScores.getNFirst(3);

                // Parcourir les scores et les ajouter au message
                int position = 1;
                for (Score score : topScores) {
                    message.append(position).append(". ");
                    message.append(score.toString()).append("\n");
                    position++;
                }

                String[] options = {"Fermer"};
                int option_selected = JOptionPane.showOptionDialog(null, message.toString(), "Meilleurs Scores", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                if (option_selected == 0) {
                    System.exit(0);
                }
            }
            else if (choix == JOptionPane.NO_OPTION) {
                System.out.println("Jeu fermé.");
                System.exit(0);
            }
        }


    }
}
