package SupportingFiles;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import static Extensions.Misc.Print.print;

/**
 * Create a class representing the music file.
 */
public class Music {

    private String path;
    private Clip clip;
    private AudioInputStream audioInputStream;

    public Music(String path) {
        this.path = path;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(new File(this.path).getAbsoluteFile());
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        try {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        clip.start();
    }

    public void stop() {
        try {
            clip.stop();
            print("Music stopped");
            clip.close();
            print("Music closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeMusic(String path) {
        stop();
        try {
            this.path = path;
            this.audioInputStream = AudioSystem.getAudioInputStream(new File(this.path).getAbsoluteFile());
            this.clip = AudioSystem.getClip();
            this.clip.open(audioInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        play();
    }

}