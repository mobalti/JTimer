package app;

import javafx.scene.media.AudioClip;

import java.net.URL;
import java.util.Objects;

public class Sound {
    public static void playAudio() {
        URL resource = Objects.requireNonNull(Sound.class.getResource("SoundTimer.mp3"));
        AudioClip plonkSound = new AudioClip(resource.toString());
        plonkSound.setVolume(0.2);
        plonkSound.play();
    }
}
