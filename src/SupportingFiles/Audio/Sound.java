package SupportingFiles.Audio;

import javax.sound.sampled.*;
import java.io.File;

import static SupportingFiles.ConfigHelper.*;

/**
 * A class that controls the sound in the game.
 */
public class Sound {

    private static Clip clip;

    public static void gameFailed() {
        if (!isSoundEffectsEnabled()) {
            return;
        }
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/Resources/Sound/game failed.wav").getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
//        try { TimeUnit.SECONDS.sleep(5); } catch (InterruptedException e) { e.printStackTrace(); }
//        clip.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void win() {
        if (!isSoundEffectsEnabled()) {
            return;
        }
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/Resources/Sound/win.wav").getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
//        try { TimeUnit.SECONDS.sleep(5); } catch (InterruptedException e) { e.printStackTrace(); }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void uncover() {
        if (!isSoundEffectsEnabled()) {
            return;
        }
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/Resources/Sound/button.wav").getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
//        try { TimeUnit.MICROSECONDS.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void quickClick() {
        if (!isSoundEffectsEnabled()) {
            return;
        }
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/Resources/Sound/whoosh3.wav").getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
//        try { TimeUnit.MICROSECONDS.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void flag() {
        if (!isSoundEffectsEnabled()) {
            return;
        }
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/Resources/Sound/whoosh1.wav").getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
//        try { TimeUnit.MICROSECONDS.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void flagWrongly() {
        if (!isSoundEffectsEnabled()) {
            return;
        }
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/Resources/Sound/flag wrongly.wav").getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void flagCorrectly() {
        if (!isSoundEffectsEnabled()) {
            return;
        }
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/Resources/Sound/flag correctly.wav").getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void gameOver() {
        if (!isSoundEffectsEnabled()) {
            return;
        }
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/Resources/Sound/game over.wav").getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void glassAlert() {
        if (!isSoundEffectsEnabled()) {
            return;
        }
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/Resources/Sound/Glass.wav").getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
