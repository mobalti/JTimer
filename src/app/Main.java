package app;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.Track;
import model.TrackDatabase;
import utils.SerializationUtils;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    public static ObservableList<Track> list =
            FXCollections.observableArrayList(
                    track -> new Observable[] {track.groupProperty(),
                            track.trackNameProperty(),
                            track.dateCreatedProperty(),
                            track.timeProperty()});
    public static String getFilePath() {
        String path = System.getProperty("user.home") + File.separator + "Documents";
        path += File.separator + "JTimer";
        return path;
    }


    static File customDir = new File(getFilePath());
    static  File file = new File(customDir, "database.db");

    @Override
    public void start(Stage primaryStage) throws Exception{
        if (!customDir.exists()) {
            boolean mkdir = customDir.mkdir();
        }
        boolean empty = !file.exists() || file.length() == 0;


        if (!empty) {
            TrackDatabase.INSTANCE = (TrackDatabase) SerializationUtils.deserialize(file.getAbsolutePath());
        }


        BooleanProperty isSaved = new SimpleBooleanProperty(false);
        ObservableList<Track> list = TrackDatabase.INSTANCE.getTracks();
        list.addListener((ListChangeListener<Track>) c -> isSaved.set(true));

        Image icon16 = new Image("/app/icon16.png");
        Image icon24 = new Image("/app/icon24.png");
        Image icon32 = new Image("/app/icon32.png");
        Image icon48 = new Image("/app/icon48.png");

        primaryStage.getIcons().setAll(icon16,icon24,icon32,icon48);
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("home.fxml")));
        primaryStage.setTitle("JTimer");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            if (isSaved.get()) {
                save();
            }

        });
    }


    public static void save() {
        try {
            SerializationUtils.serialize(TrackDatabase.getINSTANCE(),file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }


}
