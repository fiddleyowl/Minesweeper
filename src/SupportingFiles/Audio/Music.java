package SupportingFiles.Audio;

import javax.sound.sampled.*;
import java.io.File;

import static Extensions.Misc.Print.*;
import static Extensions.TypeCasting.CastFloat.*;
import static SupportingFiles.ConfigHelper.*;

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
        if (isMusicEnabled()) {
            try {
                setVolume(readConfig().musicVolume);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                print("Music started");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        clip.start();
    }

    public void stop() {
        try {
            clip.stop();
            print("Music stopped");
//            clip.close();
//            print("Music closed");
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

    public void setVolume(int volume) {
        FloatControl gainControl =
                (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float maximum = gainControl.getMaximum();
        float minimum = gainControl.getMinimum();
        gainControl.setValue(minimum+Float(volume)/100.0f*(maximum-minimum));
    }
}
