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
        audioInputStream = AudioSystem.getAudioInputStream(new File(path).getAbsoluteFile());
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
}