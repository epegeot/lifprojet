package modele;

public class Porte extends Mur {
        
        public Porte(Jeu _jeu) {
            super(_jeu);
        }
        
        @Override
        public boolean peutEtreParcouru() {
            // Regarde si le bouton est actif
            for (int x =0; x < this.jeu.SIZE_X; x++) {
                for (int y =0; y < this.jeu.SIZE_Y; y++) {
                    Case e = this.jeu.getGrille()[x][y];
                    if (e instanceof Bouton) {
                        if (((Bouton) e).estActif()) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

}
