package modele;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;


public class GestionnaireScores {
    private ArrayList<Score> scores = new ArrayList<>();

    /**
     * Constructeur de la classe, les données sont de la forme "nom:score"
     * @param path_to_scores
     */
    public GestionnaireScores(String path_to_scores) {
        File f = new File(path_to_scores);
        try (Scanner fileScanner = new Scanner(f)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    String nom = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    scores.add(new Score(nom, score));
                } else {
                    // Gérer les lignes incorrectement formatées
                    System.out.println("Ligne incorrecte: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            // Gérer l'exception de fichier non trouvé
            System.err.println("Fichier non trouvé: " + path_to_scores);
        } catch (NumberFormatException e) {
            // Gérer l'exception de format de nombre incorrect
            System.err.println("Format de score incorrect sur la ligne: " + e.getMessage());
        }
    }


    /**
     * AJotue un score au tableau, garantit que le tableau est trié
     * @param nom
     * @param score
     */
    public void addScore(String nom, int score) {
        // Recherche la position où ajouter le nouveau score
        int index = 0;
        while (index < scores.size() && score >= scores.get(index).getScore()) {
            index++;
        }
        // Ajoute le nouveau score à la position trouvée
        scores.add(index, new Score(nom, score));
    }


    public void export(String export_path) {
        try {
            FileWriter f = new FileWriter(export_path);

            BufferedWriter writer = new BufferedWriter(f);

            for (Score score : scores) {
                writer.write(score.getNom() + " " + score.getScore());
                writer.newLine();
            }

            writer.close();
        }  catch (IOException e) {
            System.out.println("Erreur lors de l'écriture du fichier de scores");
            System.exit(1);
            return; // Obligé de le mentionner sinon le langage pense qu'on est pas sortis
        }
    }

    /**
     *
     * @param n the number of players wanted
     * @return An array of Score
     */
    public List<Score> getNFirst(int n) {
        if(n > scores.size()) {
            return this.scores.subList(0, scores.size());
        }
        return this.scores.subList(0, n);
    }

}