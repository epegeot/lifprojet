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

}