package app;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.util.Duration;
import model.Track;
import model.TrackDatabase;
import utils.TimeUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class StopWatchController implements Initializable {
    public Label trackNameLabel;
    TrackDatabase data = TrackDatabase.getINSTANCE();
    ObservableList<Track> list = data.getTracks();

    public Text timeText;
    public Button playBtn;
    public Button stopBtn;

    IntegerProperty counter = new SimpleIntegerProperty(0);
    static Timeline timeline = new Timeline();
    boolean started;
    int trackIndex;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        started = false;
       // timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(false);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000), event -> timeIt()));
        playBtn.disableProperty().bind(Controller.trackIndex.isEqualTo(-1));
        stopBtn.disableProperty().bind(counter.isEqualTo(0));

        trackNameLabel.visibleProperty().bind(timeline.statusProperty().isNotEqualTo(Animation.Status.STOPPED));



    }

    void timeIt() {
        counter.setValue(counter.get() + 1);
        timeText.setText(converterSeconds(counter.getValue()));
    }


    String converterSeconds(int counter) {
        int min = (counter / 60) % 60;
        int sec = counter % 60;
        int hour = counter / 3600;

        return String.format("%02d:%02d:%02d", hour, min, sec);
    }


    public void reset() {
        timeline.stop();
        timeText.setText("00:00:00");
        counter.setValue(0);
        started = false;
        playBtn.setText("\uE768");

    }

    public void start() {

        started = !started;
        if (started) {
            trackIndex = Controller.trackIndex.get();
            trackNameLabel.setText(list.get(trackIndex).getTrackName());
            timeline.play();
            playBtn.setText("\uE769");
        } else {
            timeline.pause();
            playBtn.setText("\uE768");
        }
    }

    public void stop() {
        String oldTime =list.get(trackIndex)
                .getTime();
        list.get(trackIndex)
                .setTime(TimeUtils.getNewTime(oldTime,counter.get()));
        reset();
    }


}
