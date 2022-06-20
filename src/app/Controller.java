package app;

import javafx.animation.TranslateTransition;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Callback;
import javafx.util.Duration;
import model.Track;
import model.TrackDatabase;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    public static Map<View, Parent> cache = new HashMap<>();
    public ListView<Track> unitListView;
    public BorderPane mainPane;
    public VBox vbCenter;
    public VBox vbRight;
    public ToggleGroup topBarGroupBtn;
    public Button createBtn;
    public TextField trackTextField;
    public TextField groupTextField;
    public TextField searchTextField;
    public Button showRightBtn;
    TrackDatabase data = TrackDatabase.getINSTANCE();
    ObservableList<Track> list = data.getTracks();
    public static IntegerProperty trackIndex = new SimpleIntegerProperty(-1);
    public final String TEXT_LABEL_STYLE = "-fx-font: 14px \"Segoe UI\";\n" +
            " -fx-text-fill: white;";

    public ToggleButton pomodoroTogBtn;
    public ToggleButton stopwatchTogBtn;
    public ToggleButton timerTogBtn;
    public ToggleButton reportTogBtn;
    boolean isShowPane = true;
    BooleanProperty reportMode = new SimpleBooleanProperty(false);
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showRightBtn.disableProperty().bind(reportMode);
        unitListView.setItems(list);
        topBarGroupBtn.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
                oldVal.setSelected(true);
        });


        unitListView.setCellFactory(getCellFactory());
        BooleanBinding bb = new BooleanBinding() {
            {
                super.bind(trackTextField.textProperty(),
                        groupTextField.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return (trackTextField.getText().isBlank()
                        || groupTextField.getText().isBlank());
            }
        };

        createBtn.disableProperty().bind(bb);

        unitListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            trackIndex.setValue(list.indexOf(newValue));
        });




        loadCenterPane(View.POMODORO);

        FilteredList<Track> filteredList = new FilteredList<>(list, b -> true);
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> filteredList.setPredicate(track -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();
            return track.getTrackName().toLowerCase().contains(lowerCaseFilter);
        }));

        SortedList<Track> sortedList = new SortedList<>(filteredList);
        unitListView.setItems(sortedList);

    }

    private Callback<ListView<Track>, ListCell<Track>> getCellFactory() {
        return new Callback<>() {
            @Override
            public ListCell<Track> call(ListView<Track> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Track item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            HBox box = new HBox();
                            box.setSpacing(50);
                            Label name = new Label();
                            VBox box1 = new VBox();
                            box1.setPrefWidth(136);
                            box1.setMaxWidth(box1.getPrefWidth());
                            name.textProperty().bind(item.trackNameProperty());
                            name.setStyle(TEXT_LABEL_STYLE);
                            box1.getChildren().add(name);
                            Label time = new Label();
                            time.setStyle(TEXT_LABEL_STYLE);
                            time.textProperty().bind(item.timeProperty());
                            HBox timeBox = new HBox();
                            timeBox.getChildren().add(time);
                            timeBox.setAlignment(Pos.CENTER_RIGHT);
                            HBox.setHgrow(timeBox, Priority.ALWAYS);
                            box.getChildren().addAll(box1, timeBox);
                            setGraphic(box);

                        } else {
                            setGraphic(null);
                        }
                    }
                };
            }
        };
    }


    public void manageRightPane() {
        isShowPane = !isShowPane;
        if (isShowPane) {
            showRightPane();
        } else {
            hideRightPane();
        }
    }

    public void getCenterPane() {
        reportMode.setValue(false);
        showRightPane();
        isShowPane = true;
        if (pomodoroTogBtn.isSelected()) {
            loadCenterPane(View.POMODORO);
        } else if (timerTogBtn.isSelected()) {
            loadCenterPane(View.TIMER);
        } else if (stopwatchTogBtn.isSelected()) {
            loadCenterPane(View.STOPWATCH);
        } else if (reportTogBtn.isSelected()) {
            reportMode.setValue(true);
            vbRight.setVisible(false);
            vbRight.setManaged(false);
            hideRightPane();
            isShowPane = false;
            loadCenterPane(View.REPORT);
        }
    }

    public void showRightPane() {
        vbRight.setManaged(true);
        vbRight.setVisible(true);
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.4));
        slide.setNode(vbRight);
        slide.setToX(0);
        slide.play();
        slide.setOnFinished(e -> {
                }
        );
    }

    public void hideRightPane() {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.4));
        slide.setNode(vbRight);
        slide.setToX(305);
        slide.play();
        slide.setOnFinished(e -> {
            if (vbRight.isManaged()) {
                vbRight.setVisible(false);
                vbRight.setManaged(false);
            }
        });
    }

    public void loadCenterPane(View view) {


        try {
            Parent root;
            if (cache.containsKey(view)) {
                root = cache.get(view);
            } else {
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(view.getFilename())));
                cache.put(view, root);
            }

            mainPane.setCenter(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void create() {
        String dateCreated = LocalDate.now().toString();
        list.add(new Track(groupTextField.getText(), trackTextField.getText(),
                dateCreated));
        groupTextField.clear();
        trackTextField.clear();
    }


    public void onEnter() {
        if (!createBtn.isDisable()) {
            createBtn.fire();
            trackTextField.requestFocus();
        }
    }
}
