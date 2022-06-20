package app;


import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javafx.collections.FXCollections;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.Track;
import model.TrackDatabase;
import utils.TimeUtils;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.*;

public class ReportController implements Initializable {
    public TableView<Track> table;
    public TableColumn<Track, String> groupCol;
    public TableColumn<Track, String> trackCol;
    public TableColumn<Track, String> dateCol;
    public TableColumn<Track, Long> timeCol;
    public PieChart pieChart;
    public Button showChartBtn;
    public VBox chartPane;
    public Button deleteBtn;
    public TextField searchField;
    public ToggleButton trackPieBtn;
    public ToggleGroup pieButton;
    public ToggleButton groupPieBtn ;
    TrackDatabase data = TrackDatabase.getINSTANCE();
    ObservableList<Track> list = data.getTracks();
    ObservableList<PieChart.Data> pieChartData;
    Tooltip tooltip;
    static BooleanProperty isShowed = new SimpleBooleanProperty(false);
    boolean isByTrack = true;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        table.setEditable(true);
        groupCol.setCellValueFactory(new PropertyValueFactory<>("group"));
        trackCol.setCellValueFactory(new PropertyValueFactory<>("trackName"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateCreated"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        pieButton.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
                oldVal.setSelected(true);
        });

        chartPane.managedProperty().bind(isShowed);
        chartPane.visibleProperty().bind(isShowed);

        trackPieBtn.visibleProperty().bind(isShowed);
        groupPieBtn.visibleProperty().bind(isShowed);

        groupCol.setCellFactory(TextFieldTableCell.forTableColumn());
        trackCol.setCellFactory(TextFieldTableCell.forTableColumn());

        groupCol.setOnEditCommit(ReportController::handleGroup);
        trackCol.setOnEditCommit(ReportController::handleTrack);
        deleteBtn.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));

        table.setItems(list);
        pieChartData =
                list.stream().collect(Collectors.toMap(Track::getTrackName,track -> getPieTime(track.getTime())))
                        .entrySet().stream()
                        .map(entry -> new PieChart.Data(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));


        updatePieChart();

        FilteredList<Track> filteredList = new FilteredList<>(list,b -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filteredList.setPredicate(track -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = newValue.toLowerCase();

            return track.getGroup().toLowerCase().contains(lowerCaseFilter)
                    || track.getTrackName().toLowerCase().contains(lowerCaseFilter)
                    || track.getDateCreated().toLowerCase().contains(lowerCaseFilter)
                    || track.getTime().contains(lowerCaseFilter);
        }));

        SortedList<Track> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedList);
        trackPieBtn.setSelected(true);
        print();
        list.addListener((ListChangeListener<Track>) c -> {
            if (isByTrack) {
                print();
            } else {
                printByGroup();
            }
        });
    }

    public void getPie() {
        if (groupPieBtn.isSelected()) {
            printByGroup();
        } else if (trackPieBtn.isSelected()){
            print();
        }
    }

    public void delete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Are you sure you want to delete this word");
        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                list.removeAll(table.getSelectionModel().getSelectedItem());
            }
        });
    }


    private void hideChart() {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.4));
        slide.setNode(chartPane);
        slide.setToY(213);
        slide.play();


    }

    private void showChart() {
        updatePieChart();
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.4));
        slide.setNode(chartPane);
        slide.setToY(0);
        slide.play();
    }

    private void updatePieChart() {
        pieChart.setData(pieChartData);
        pieChart.getData().forEach(data1 -> {
            String percentage = String.format("%.1f%%", 100*data1.getPieValue()/getTotal()) ;
            tooltip = new Tooltip(percentage);
            Tooltip.install(data1.getNode(), tooltip);

        });
    }


    private static void handleTrack(TableColumn.CellEditEvent<Track, String> event) {
        event.getTableView().getItems().get(event.getTablePosition().getRow()).setTrackName(event.getNewValue());
    }
    private static void handleGroup(TableColumn.CellEditEvent<Track, String> event) {
        event.getTableView().getItems().get(event.getTablePosition().getRow()).setGroup(event.getNewValue());
    }
    public void print() {
        pieChartData =
                list.stream().collect(Collectors.toMap(Track::getTrackName,track -> getPieTime(track.getTime())))
                        .entrySet().stream()
                        .map(entry -> new PieChart.Data(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));
        updatePieChart();
        isByTrack = true;

    }
    public long getPieTime(String time) {
     return TimeUtils.getSeconds(time);
    }
    public void printByGroup() {
        pieChartData =
                list.stream()
                        .collect(groupingBy(Track::getGroup, summingLong(track -> getPieTime(track.getTime()))))
                        .entrySet().stream()
                        .map(entry -> new PieChart.Data(entry.getKey(), entry.getValue()))
                        .collect(Collectors.toCollection(FXCollections::observableArrayList));

       updatePieChart();
       isByTrack = false;
    }

    private double getTotal() {
        return pieChart.getData().stream().mapToDouble(PieChart.Data::getPieValue).sum();
    }

    public void manageChart() {

        if (!isShowed.get()) {

            showChartBtn.setText("Hide chart");
            showChart();
            isShowed.setValue(true);
        } else {
            showChartBtn.setText("Show chart");
            hideChart();
            isShowed.setValue(false);
        }
    }
}
