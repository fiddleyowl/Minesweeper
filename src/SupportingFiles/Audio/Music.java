package SupportingFiles.Audio;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;

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
    FloatControl gainControl;
    float difference;
    float minimum;

    public Music() {
        this.path = "/Resources/Music/Raphaël Beau - Micmacs A La Gare.wav";
        try {
            audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream(path)));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            minimum = gainControl.getMinimum();
            difference = gainControl.getMaximum() - gainControl.getMinimum();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Music(String path) {
        this.path = path;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(getClass().getResourceAsStream(path)));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            minimum = gainControl.getMinimum();
            difference = gainControl.getMaximum() - gainControl.getMinimum();
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


    public void setVolume(int volume) {
        gainControl.setValue(minimum+Float(volume)/100.0f*(difference));
    }
}
