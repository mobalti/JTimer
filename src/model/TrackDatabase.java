package model;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;

public class TrackDatabase implements Serializable {
    private transient ObservableList<Track> tracks =
            FXCollections.observableArrayList(
                    track -> new Observable[] {track.groupProperty(),
                            track.trackNameProperty(),
                    track.dateCreatedProperty(),
                    track.timeProperty()});

    public static TrackDatabase INSTANCE = new TrackDatabase();

    @Serial
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeObject(tracks.toArray(new Track[0]));
    }

    @Serial
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        var list = FXCollections.observableArrayList((Track[]) s.readObject());
        tracks = FXCollections.observableList(list,  track -> new Observable[] {track.groupProperty(),
                track.trackNameProperty(),
                track.dateCreatedProperty(),
                track.timeProperty()});

    }


    public ObservableList<Track> getTracks() {
        return tracks;
    }



    public static TrackDatabase getINSTANCE() {
        return INSTANCE;
    }


}
