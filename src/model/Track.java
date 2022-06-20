package model;


import javafx.beans.property.SimpleStringProperty;
import java.io.*;


public class Track implements Serializable {
    private transient SimpleStringProperty group;
    private transient SimpleStringProperty trackName;

    private transient SimpleStringProperty dateCreated;
    private transient SimpleStringProperty time;

    public Track(String group, String trackName, String dateCreated) {
        this.group = new SimpleStringProperty(group);
        this.trackName = new SimpleStringProperty(trackName);

        this.dateCreated = new SimpleStringProperty(dateCreated);
        this.time = new SimpleStringProperty("00:00:00");
    }

    @Serial
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeObject(group.get());
        s.writeObject(trackName.get());
        s.writeObject(dateCreated.get());
        s.writeObject(time.get());

    }

    @Serial
    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        group = new SimpleStringProperty((String) s.readObject());
        trackName= new SimpleStringProperty((String) s.readObject());
        dateCreated = new SimpleStringProperty((String) s.readObject());
        time = new SimpleStringProperty((String) s.readObject());

    }


    public String getTrackName() {
        return trackName.get();
    }

    public SimpleStringProperty trackNameProperty() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName.set(trackName);
    }

    public String getGroup() {
        return group.get();
    }


    public void setGroup(String group) {
        this.group.set(group);
    }

    public String getDateCreated() {
        return dateCreated.get();
    }


    public String getTime() {
        return time.get();
    }

    public SimpleStringProperty groupProperty() {
        return group;
    }

    public SimpleStringProperty dateCreatedProperty() {
        return dateCreated;
    }


    public SimpleStringProperty timeProperty() {
        return time;
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    @Override
    public String toString() {
        return String.format("%s   %s",getTrackName(), getTime());

    }

}
