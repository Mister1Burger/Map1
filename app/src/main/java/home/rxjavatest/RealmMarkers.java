package home.rxjavatest;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 * Created by Kapusta on 12.10.2017.
 */

public class RealmMarkers{

    public RealmMarkers() {
    }

    private Realm init(Context context) {
        Realm.init(context);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .name( "reminder.realm")
                .modules(new MyLibraryModule())
                .build();

        try {
            return Realm.getInstance(realmConfiguration);
        } catch (RealmMigrationNeededException e) {
            try {
                Log.e( "TAG",String.valueOf(e));
                Realm.deleteRealm(realmConfiguration);
                //Realm file has been deleted.
                return Realm.getInstance(realmConfiguration);
            } catch (Exception ex) {
                //No Realm file to remove.
                Log.e( "TAG",String.valueOf(ex));
            }
        }

        return null;
    }

    public List<MyMarker> readeMarkers(Context context) {
        Realm realm = init(context);
        File realmFile = new File(context.getFilesDir(), "reminder.realm");
        try {
            assert realm != null;
            RealmResults<MyMarker> list = realm.where(MyMarker.class).findAll();
            Log.d( "TAG",String.valueOf(realmFile.length()));
            if (list == null)
                return new ArrayList<>();
            return list;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public void saveMarker(Context context, MyMarker marker) {

        Realm realm = init(context);
        File realmFile = new File(context.getFilesDir(), "reminder.realm");
        assert realm != null;
        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(marker);
            Log.d( "TAG",String.valueOf(realmFile.length()));
            realm.commitTransaction();
        } catch (NullPointerException ignore) {
        }
    }


    public void removeMarker(Context context, double latitude, double longitude) {
        String id = latitude + " " + longitude;
        Realm realm = init(context);
        File realmFile = new File(context.getFilesDir(), "reminder.realm");
        assert realm != null;
        try {
            MyMarker marker = realm.where(MyMarker.class)
                    .equalTo("id", id).findFirst();
            if(marker!=null) {
                realm.beginTransaction();
                marker.deleteFromRealm();
                realm.commitTransaction();
            }
            Log.d( "TAG",String.valueOf(realmFile.length()));
        } catch (NullPointerException ignore) {
        }
    }


    public void onDestroy(Context context) {
        Realm realm = init(context);
        assert realm != null;
        realm.close();
    }
}