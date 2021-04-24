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
    private String status;
    private AudioInputStream audioInputStream;


    public Music(String path) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        this.path = path;
        audioInputStream = AudioSystem.getAudioInputStream(new File(this.path).getAbsoluteFile());
        clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void musicPlay() {
        clip.start();
        status = "start";
    }

    public void musicStop() {
        clip.stop();
        clip.close();
    }

    public void changeMusic(String path) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        musicStop();
        this.path = path;
        this.audioInputStream = AudioSystem.getAudioInputStream(new File(this.path).getAbsoluteFile());
        this.clip = AudioSystem.getClip();
        this.clip.open(audioInputStream);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        musicPlay();
    }
}