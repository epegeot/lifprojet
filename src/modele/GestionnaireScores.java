package modele;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class GestionnaireScores {
    private ArrayList<Score> scores = new ArrayList<>();
    
    /**
     * Constructeur de la classe, les données sont de la forme "nom:score"
     * @param path_to_scores
     */
    public GestionnaireScores(String path_to_scores) {
        
        File f = new File(path_to_scores);
        Scanner fileScanner;
        try {
            fileScanner = new Scanner(f);
        } catch (FileNotFoundException e) {
            // On a un tableau vide
            return; // Obligé de le mentionner sinon le langage pense qu'on est pas sortis
        }
        fileScanner.useDelimiter("\n");
        while (fileScanner.hasNext()) {
            String l = fileScanner.next();
            Scanner lineScanner = new Scanner(l);
            lineScanner.useDelimiter(":");
            String nom = lineScanner.next();
            int score = lineScanner.nextInt();
            addScore(nom, score);
            lineScanner.close();
        }
        fileScanner.close();
    }

    /**
     * AJotue un score au tableau, garantit que le tableau est trié
     * @param nom
     * @param score
     */
    public void addScore(String nom, int score) {
        for (int i = 0; i < scores.size(); i++) {
            if (score > scores.get(i).getScore()) {
                scores.add(i, new Score(nom, score));
            }
        }
    }

    public void export(String export_path) {
        try {
            FileWriter f = new FileWriter(export_path);
            
            BufferedWriter writer = new BufferedWriter(f);

            for (Score score : scores) {
                writer.write(score.getNom() + ":" + score.getScore());
                writer.newLine();
            }

            writer.close();
        }  catch (IOException e) {
            System.out.println("Erreur lors de l'écriture du fichier de scores");
            System.exit(1);
            return; // Obligé de le mentionner sinon le langage pense qu'on est pas sortis
        }
    }
}
