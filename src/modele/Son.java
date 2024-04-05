package modele;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Son {

    private String file;

    public Son(String _file) {
        file = _file;
    }
    public void jouerSon() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(file));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
