package SupportingFiles.Audio;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;

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
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(Sound.class.getResourceAsStream("/Resources/Sound/game failed.wav")));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void win() {
        if (!isSoundEffectsEnabled()) {
            return;
        }
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(Sound.class.getResourceAsStream("/Resources/Sound/win.wav")));
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
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(Sound.class.getResourceAsStream("/Resources/Sound/button.wav")));
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
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(Sound.class.getResourceAsStream("/Resources/Sound/whoosh3.wav")));
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
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(Sound.class.getResourceAsStream("/Resources/Sound/whoosh1.wav")));
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
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(Sound.class.getResourceAsStream("/Resources/Sound/flag wrongly.wav")));
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
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(Sound.class.getResourceAsStream("/Resources/Sound/flag correctly.wav")));
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
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(Sound.class.getResourceAsStream("/Resources/Sound/game over.wav")));
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
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(Sound.class.getResourceAsStream("/Resources/Sound/Glass.wav")));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
