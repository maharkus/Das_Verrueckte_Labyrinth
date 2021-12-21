import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;

public class PlayMusic {
    File file;

    PlayMusic(File Sound, float volume) {
        this.file = Sound;
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(Sound));
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(volume);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            Thread.sleep(10000);
            clip.start();
        } catch (Exception e) {
            System.out.println("Sound file not found.");
        }
    }
}