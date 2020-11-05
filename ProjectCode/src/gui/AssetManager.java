package gui;

import java.io.File;
import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;

/**
 *
 * @author d_ruizci
 */
public class AssetManager {

    //public static final Image EXAMPLE_IMAGE = new Image("PATH/TO/FILE.png");

    public static final String DEFAULT_USER_IMAGE_URL = "jar:" + AssetManager.class.getResource("/assets/defaultUserProfile.png").getFile();
    public static final String PERSON_IMAGE_URL = "jar:" + AssetManager.class.getResource("/assets/housePerson.png").getFile();
    public static final String LIGHT_OFF_IMAGE_URL = "jar:" + AssetManager.class.getResource("/assets/houseLightOff.png").getFile();
    public static final String LIGHT_ON_IMAGE_URL = "jar:" + AssetManager.class.getResource("/assets/houseLightOn.png").getFile();

    //public static final File EXAMPLE_FILE = new File("PATH/TO/FILE.file");

    //private static final AudioClip EXAMPLE_SOUND = new AudioClip(new File("PATH/TO/FILE.wav").toURI().toString());

    public static double volume = 0.5;
/*
    public static Image getDefaultUserImage() {
        //return new Image(DEFAULT_USER_IMAGE_URL);
    }*/

    public static Image getPersonImage() {
        return new Image(PERSON_IMAGE_URL);
    }

    public static Image getLightOffImage() {
        return new Image(LIGHT_OFF_IMAGE_URL);
    }

    public static Image getLightOnImage() {
        return new Image(LIGHT_ON_IMAGE_URL);
    }
/*
    public static void playSound() {
        EXAMPLE_SOUND.play(volume);
    }*/

}
