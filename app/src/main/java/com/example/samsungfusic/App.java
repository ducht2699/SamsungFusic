package com.example.samsungfusic;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.samsungfusic.Utils.Constants;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
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
