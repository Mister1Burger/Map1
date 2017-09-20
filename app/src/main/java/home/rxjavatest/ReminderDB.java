package home.rxjavatest;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ReminderDB extends RealmObject{

    @PrimaryKey
    private long id;
    private long time;
    private String tour;

    public ReminderDB() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTour() {
        return tour;
    }

    public void setTour(String tour) {
        this.tour = tour;
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "id=" + id +
                ", time=" + time +
                ", tour='" + tour + '\'' +
                '}';
    }
}
