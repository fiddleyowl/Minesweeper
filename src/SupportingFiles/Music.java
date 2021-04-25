package SupportingFiles;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

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
        } catch (Exception ignored) { }

    }

    public void play() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
//        clip.start();
    }

    public void stop() {
        clip.stop();
        clip.close();
    }

    public void changeMusic(String path) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        stop();
        this.path = path;
        this.audioInputStream = AudioSystem.getAudioInputStream(new File(this.path).getAbsoluteFile());
        this.clip = AudioSystem.getClip();
        this.clip.open(audioInputStream);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        play();
    }

}