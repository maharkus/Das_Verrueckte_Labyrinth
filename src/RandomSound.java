import java.io.File;
import java.util.Random;

public class RandomSound {

    File randomFile;

    public File getRandomFile() {
        return randomFile;
    }

    public void setRandomFile(File randomFile) {
        this.randomFile = randomFile;
    }

    public RandomSound() {

        Random selectSound = new Random();
        switch (selectSound.nextInt(4)) {
            case 0 -> this.randomFile = new File("resources/sounds/spookyWav.wav");
            case 1 -> this.randomFile = new File("resources/sounds/evilLaugh.wav");
            case 2 -> this.randomFile = new File("resources/sounds/shyLaugh.wav");
            case 3 -> this.randomFile = new File("resources/sounds/ghost.wav");
            default -> System.out.println("Sound nicht gefunden.");
        }
    }
}
