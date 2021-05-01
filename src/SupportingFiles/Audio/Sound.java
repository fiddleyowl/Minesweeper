package SupportingFiles.Audio;

import javax.sound.sampled.*;
import java.io.File;

import static SupportingFiles.ConfigHelper.*;

/**
 * A class that controls the sound in the game.
 */
public class Sound {

    private static Clip clip;

    public static void gameOver() {
        if (!isSoundEffectsEnabled()) {
            return;
        }
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/Resources/Sound/gameOver.wav").getAbsoluteFile());
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

}