package app;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import model.Track;
import model.TrackDatabase;
import utils.TimeUtils;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

public class TimerController implements Initializable {
    public Button playBtn;
    public Text timerText;
    public ComboBox<Integer> secChoice;
    public ComboBox<Integer> minChoice;
    public ComboBox<Integer> hourChoice;
    public Label checkTimerLabel;
    public Button stopBtn;
    public Button addBtn;
    public Button resetBtn;
    public HBox topPaneTimer;
    public Label trackNameLabel;
    ObservableList<Integer> hours = FXCollections.observableArrayList();
    ObservableList<Integer> min = FXCollections.observableArrayList();
    TrackDatabase data = TrackDatabase.getINSTANCE();
    ObservableList<Track> list = data.getTracks();

    long counter = 0;
    static Timeline timeline = new Timeline();
    boolean started;
    LongProperty initialTime = new SimpleLongProperty(0);
    long value;
    int trackIndex;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        topPaneTimer.setStyle("-fx-background-color: transparent;");
        hourChoice.setItems(hours);
        hourChoice.setVisibleRowCount(5);
        minChoice.setItems(min);
        minChoice.setVisibleRowCount(5);
        secChoice.setItems(min);
        secChoice.setVisibleRowCount(5);

        hourChoice.setValue(0);
        minChoice.setValue(0);
        secChoice.setValue(0);
        /*initialTime = getInitialTime();*/
        checkTimerLabel.setOpacity(0);
        started = false;
       // timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(false);
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000), event -> timeIt()));
        IntStream.range(0, 24).forEach(i -> hours.add(i));
        IntStream.range(0, 60).forEach(i -> min.add(i));


        BooleanBinding booleanBinding =
                hourChoice.getSelectionModel().selectedItemProperty().isEqualTo(0).and(
                        secChoice.getSelectionModel().selectedItemProperty().isEqualTo(0).and(
                                minChoice.getSelectionModel().selectedItemProperty().isEqualTo(0)));

        addBtn.disableProperty().bind(booleanBinding.or(timeline.statusProperty().isNotEqualTo(Animation.Status.STOPPED)));
        playBtn.disableProperty().bind(initialTime.isEqualTo(0).or(Controller.trackIndex.isEqualTo(-1)));
        stopBtn.disableProperty().bind(timeline.statusProperty().isEqualTo(Animation.Status.STOPPED));
        resetBtn.disableProperty().bind(timeline.statusProperty().isEqualTo(Animation.Status.STOPPED));

        trackNameLabel.visibleProperty().bind(timeline.statusProperty().isNotEqualTo(Animation.Status.STOPPED));


    }

    void timeIt() {
        counter++;
        value = initialTime.getValue() - counter;
        timerText.setText(converterSeconds(value));
        if (value == 0) {
            timeline.stop();
            checkTimerLabel.setOpacity(1);
            topPaneTimer.setStyle("-fx-background-color: accent_color;");
            initialTime.setValue(0);
            started = false;
            playBtn.setText("\uE768");
            String oldTime =list.get(trackIndex)
                    .getTime();
            list.get(trackIndex)
                    .setTime(TimeUtils.getNewTime(oldTime,counter));
            counter = 0;
            Sound.playAudio();
        }

    }


    String converterSeconds(long counter) {
        long min = (counter / 60) % 60;
        long sec = counter % 60;
        long hour = counter / 3600;

        return String.format("%02d:%02d:%02d", hour, min, sec);
    }


    public void reset() {
        timeline.stop();
        timerText.setText(converterSeconds(initialTime.getValue()));
        counter = 0;
        started = false;
        playBtn.setText("\uE768");
        checkTimerLabel.setOpacity(0);
        topPaneTimer.setStyle("-fx-background-color: transparent;");

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

    public void add() {
    reset();
    initialTime.setValue(getInitialTime());

        timerText.setText(converterSeconds(initialTime.getValue()));
    }

    private int getInitialTime() {
        int hour = hourChoice.getValue();
        int min = minChoice.getValue();
        int sec  = secChoice.getValue();
        return hour * 3600 + min * 60 + sec;
    }

    public void stop() {
        timeline.stop();
        checkTimerLabel.setOpacity(1);
        topPaneTimer.setStyle("-fx-background-color: accent_color;");
        initialTime.setValue(0);
        started = false;
        playBtn.setText("\uE768");
        String oldTime =list.get(trackIndex)
                .getTime();
        list.get(trackIndex)
                .setTime(TimeUtils.getNewTime(oldTime,counter));
        counter = 0;
    }

}
