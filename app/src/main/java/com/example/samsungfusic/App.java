package com.example.samsungfusic;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.samsungfusic.Utils.Constants;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        initRealm();
    }

    private void initRealm() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(Constants.DB_NAME)
                .schemaVersion(1)
                .allowQueriesOnUiThread(true)
                .allowWritesOnUiThread(true)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel playbackMusicChannel = new NotificationChannel(Constants.CHANNEL_ID, "Playback Music", NotificationManager.IMPORTANCE_DEFAULT);
            playbackMusicChannel.setDescription("Playback Music");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(playbackMusicChannel);
        }
    }
}
