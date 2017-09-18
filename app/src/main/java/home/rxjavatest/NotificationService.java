package home.rxjavatest;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

public class NotificationService extends Service {

    private CompositeDisposable disposables;

    public static boolean isStarted;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isStarted = true;
        if (disposables == null || disposables.isDisposed())
            disposables = new CompositeDisposable();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        disposables.add(Observable.interval(10, TimeUnit.SECONDS)
                .doOnNext(value -> Log.d("TAG", "interval value: " + value))
                .subscribe(value -> sendNotification()));

        return START_STICKY;
    }

    private void sendNotification() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags((Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_FORWARD_RESULT));
        intent.putExtra("text","test");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(((BitmapDrawable) this.getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap())
                .setContentTitle(getResources().getString(R.string.CHAT_MESSAGE_NOTIFICATION_TITLE))
                .setContentText(getResources().getString(R.string.CHAT_MESSAGE_NOTIFICATION_BODY, "firstname", "lastname"))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void onDestroy() {
        isStarted = false;
        disposables.clear();
        super.onDestroy();
    }
}
