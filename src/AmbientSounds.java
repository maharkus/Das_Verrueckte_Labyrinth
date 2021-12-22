import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class AmbientSounds {

    File randomFile;
    File sounds;
    ArrayList<File> files;

    public File getRandomFile() {

        Random rand = new Random();
        int i = rand.nextInt(files.size());
        File randomFile = files.get(i);
        files.remove(i);
        System.out.println(files.size());
        if (files.size() == 0) {
            files = new ArrayList<>(Arrays.asList(Objects.requireNonNull(sounds.listFiles())));
        }
        return randomFile;
    }

    public void setRandomFile(File randomFile) {
        this.randomFile = randomFile;
    }

    public AmbientSounds() {
        sounds = new File("resources/sounds/random_sounds");
        files = new ArrayList<>(Arrays.asList(Objects.requireNonNull(sounds.listFiles())));
    }
}
