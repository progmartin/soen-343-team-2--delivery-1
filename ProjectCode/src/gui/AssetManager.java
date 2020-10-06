package gui;

import java.io.File;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;

/**
 *
 * @author d_ruizci
 */
public class AssetManager {

    public static final Image EXAMPLE_IMAGE = new Image("PATH/TO/FILE.png");
    public static final Image DEFAULT_USER_IMAGE = new Image("file:assets/defaultUserProfile.png");
    
    public static final File EXAMPLE_FILE = new File("PATH/TO/FILE.file");

    private static final AudioClip EXAMPLE_SOUND = new AudioClip(new File("PATH/TO/FILE.wav").toURI().toString());
    
    
    public static double volume = 0.5;

    public static void playSound() {
        EXAMPLE_SOUND.play(volume);
    }

}
