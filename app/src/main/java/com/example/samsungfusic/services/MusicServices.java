package com.example.samsungfusic.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;

import com.example.samsungfusic.R;
import com.example.samsungfusic.Utils.Constants;
import com.example.samsungfusic.Utils.Utils;
import com.example.samsungfusic.activities.MainActivity;
import com.example.samsungfusic.models.Track;

import java.io.IOException;
import java.util.List;

import static com.example.samsungfusic.Utils.Constants.CHANNEL_ID;

public class MusicServices extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private List<Track> trackList;
    private MediaPlayer player;
    private IBinder iBinder = new MusicBinder();
    private boolean isPlaying = false;
    private MediaSessionCompat mediaSession;
    private PendingIntent pendingIntent;

    public MusicServices() {
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mediaSession, intent);
        Intent intent1 = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.player = new MediaPlayer();
        initMediaPlayer();
        mediaSession = new MediaSessionCompat(this, "Tag");

    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

    public void setTrackList(List<Track> trackList) {
        this.trackList = trackList;
    }

    public void play() {
        player.start();
        isPlaying = true;
    }

    public void pause() {
        player.pause();
        isPlaying = false;
    }

    public class MusicBinder extends Binder {
        public MusicServices getService() {
            return MusicServices.this;
        }
    }

    public void playTrack(Track track, Context context) {
        player.reset();
        try {
            player.setDataSource(context, Uri.parse(track.getM_sLocation()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.prepareAsync();
        isPlaying = true;

        createNotification(track, true);
    }

    public void createNotification(Track track, boolean isPlaying) {
        Intent iPrevious = new Intent(Constants.CLICK_PREVIOUS);
//        iPrevious.setAction(Constants.CLICK_PREVIOUS);
        Intent iPlay = new Intent(Constants.CLICK_PLAY);
//        iPlay.setAction(Constants.CLICK_PLAY);
        Intent iPause = new Intent(Constants.CLICK_PAUSE);
//        iPause.setAction(Constants.CLICK_PAUSE);
        Intent iNext = new Intent(Constants.CLICK_NEXT);
//        iNext.setAction(Constants.CLICK_NEXT);
        Intent iClose = new Intent(Constants.CLICK_CLOSE);
//        iClose.setAction(Constants.CLICK_CLOSE);

        NotificationCompat.Builder build = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(track.getTitle())
                .setContentText(track.getArtist())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", PendingIntent.getBroadcast(this, 0, iPrevious, PendingIntent.FLAG_UPDATE_CURRENT))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_baseline_music_note_24)
                .setLargeIcon(Utils.drawableToBitmap(track.getM_iImage()))
                .setContentIntent(pendingIntent);
        if (isPlaying) {
            build.addAction(R.drawable.ic_baseline_pause_24, "Play", PendingIntent.getBroadcast(this, 0, iPlay, PendingIntent.FLAG_UPDATE_CURRENT));
        } else {
            build.addAction(R.drawable.ic_baseline_play_arrow_24, "Play", PendingIntent.getBroadcast(this, 0, iPause, PendingIntent.FLAG_UPDATE_CURRENT));
        }
        build.addAction(R.drawable.ic_baseline_skip_next_24, "Next", PendingIntent.getBroadcast(this, 0, iNext, PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(R.drawable.ic_baseline_close_24, "Close", PendingIntent.getBroadcast(this, 0, iClose, PendingIntent.FLAG_UPDATE_CURRENT));
        build.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2)
                .setMediaSession(mediaSession.getSessionToken()));
        startForeground(1, build.build());
    }

    public void initMediaPlayer() {
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnPreparedListener(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }
}