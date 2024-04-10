package modele;

public class Bouton extends Vide {
    
        public Bouton(Jeu _jeu) {
            super(_jeu);
        }
    
        @Override
        public boolean peutEtreParcouru() {
            return true;
        }

        public boolean estActif() {
            return this.getEntite() != null;
        }
}
