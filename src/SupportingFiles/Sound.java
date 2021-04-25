package SupportingFiles;

import javax.sound.sampled.*;
import java.io.File;

/**
 * A class that controls the sound in the game.
 */
public class Sound {

    private static Clip clip;

    public static void gameOver() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/Resources/Sound/gameOver.wav").getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void win() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/Resources/Sound/win.wav").getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void uncover() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/Resources/Sound/button.wav").getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void quickClick() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/Resources/Sound/whoosh3.wav").getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void flag() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/Resources/Sound/whoosh1.wav").getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
