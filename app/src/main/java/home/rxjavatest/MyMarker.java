package home.rxjavatest;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Kapusta on 12.10.2017.
 */

public class MyMarker extends RealmObject {
    @PrimaryKey
    private String id;
    private double latitude;
    private double longitude;

    public String getId() {
        return id;
    }

    public void setId(double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.id = latitude + " " + longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "MyMarker{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
