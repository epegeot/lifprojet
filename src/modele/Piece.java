package modele;

public class Piece extends Case {

    public Piece(Jeu _jeu) {
        super(_jeu);
    }

    @Override
    public boolean peutEtreParcouru() {
        return true;
    }

}
