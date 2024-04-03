package modele;
public class Score {
    private String nom;
    private int score;
    
    public Score(String _nom, int _score) {
        nom = _nom;
        score = _score;
    }
    
    public String getNom() {
        return nom;
    }
    
    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return nom + " avec un score de " + score + " coups ";
    }

}