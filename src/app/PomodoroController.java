package app;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.util.Duration;
import model.Track;
import model.TrackDatabase;
import utils.TimeUtils;
import java.net.URL;
import java.util.ResourceBundle;

public class PomodoroController implements Initializable {

    public Button resetBtn;
    public Label trackNameLabel;
    TrackDatabase data = TrackDatabase.getINSTANCE();
    ObservableList<Track> list = data.getTracks();

    public Button stopBtn;
    public ProgressIndicator pi;
    public Label proLabel;
    public Button playBtn;

    Timeline timeline = new Timeline();
    boolean started;
    long initialTime = 1500; // 25 min

    int trackIndex;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        proLabel.setOpacity(0);
        started = false;
        timeline.setAutoReverse(true);
        timeline.getKeyFrames().add( new KeyFrame(Duration.ZERO, new KeyValue(pi.progressProperty(), 0)));
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(initialTime), new KeyValue(pi.progressProperty(), 1)));
        timeline.setOnFinished(event -> onFinished());
        playBtn.disableProperty().bind(Controller.trackIndex.isEqualTo(-1));
        stopBtn.disableProperty().bind(timeline.statusProperty().isEqualTo(Animation.Status.STOPPED));
        resetBtn.disableProperty().bind(timeline.statusProperty().isEqualTo(Animation.Status.STOPPED));
        trackNameLabel.visibleProperty().bind(timeline.statusProperty().isNotEqualTo(Animation.Status.STOPPED));
    }

    private void onFinished() {
        proLabel.setOpacity(1);
        pi.setProgress(1);
        String oldTime =list.get(trackIndex)
                .getTime();
        list.get(trackIndex)
                .setTime(TimeUtils.getNewTime(oldTime,initialTime));
        Sound.playAudio();
        playBtn.setText("\uE768");
        timeline.stop();
    }


    public void reset() {
        proLabel.setOpacity(0);
        timeline.stop();
        pi.setProgress(0);
        started = false;
        playBtn.setText("\uE768");
    }

    public void start() {
        if (!started) {
            timeline.play();
            trackIndex = Controller.trackIndex.get();
            trackNameLabel.textProperty().bind(list.get(trackIndex).trackNameProperty());
            started = true;
            playBtn.setText("\uE769");
        } else {
            timeline.pause();
            started = false;
            playBtn.setText("\uE768");
        }


    }



    public void stop() {
        String oldTime =list.get(trackIndex)
                .getTime();
        list.get(trackIndex)
                .setTime(TimeUtils.getNewTime(oldTime,(int) timeline.getCurrentTime().toSeconds()));
        reset();

    }
}
