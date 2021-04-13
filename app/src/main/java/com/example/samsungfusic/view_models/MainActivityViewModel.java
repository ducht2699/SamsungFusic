package com.example.samsungfusic.view_models;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModel;

import com.example.samsungfusic.R;
import com.example.samsungfusic.databinding.ActivityMainBinding;
import com.example.samsungfusic.models.Track;
import com.example.samsungfusic.repository.IndicatorRepository;
import com.example.samsungfusic.repository.TracksRepository;
import com.example.samsungfusic.services.MusicServices;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import static com.example.samsungfusic.Utils.Constants.CHANNEL_ID;

public class MainActivityViewModel extends ViewModel {
    private IndicatorRepository indicatorRepository;
    private TracksRepository tracksRepository;
    private ActivityMainBinding mainBinding;
    private Context context;
    private Track currentTrack;
    private MusicServices musicSrv;
    private Intent playIntent;
    private List<Track> trackList;
    private boolean musicBound = false;

    public void setMainBinding(ActivityMainBinding mainBinding) {
        this.mainBinding = mainBinding;
    }

    public List<Track> getTrackList() {
        return trackList;
    }

    public MainActivityViewModel() {
        this.indicatorRepository = new IndicatorRepository();
    }

    public void createTrackRepo(Context context) {
        this.context = context;
        this.tracksRepository = new TracksRepository(context);
        this.trackList = tracksRepository.getTrackList();
    }

    public void setIndicator(TabLayout tabLayout) {
        for (String x : indicatorRepository.getIndicators()) {
            tabLayout.addTab(tabLayout.newTab().setText(x));
        }
    }

    public void onTrackClicked(Track track) {
        this.currentTrack = track;
        musicSrv.playTrack(currentTrack, context);
        updateUI();
    }

    public void updateUI() {
        updateUITrack();
        updateUIPlayButton();
    }

    private void updateUITrack() {
        mainBinding.imvTrackImage.setImageDrawable(currentTrack.getM_iImage());
        mainBinding.tvArtist.setText(currentTrack.getArtist());
        mainBinding.tvSongName.setText(currentTrack.getTitle());
    }

    public void initService(Context context) {
        if (playIntent == null) {
            playIntent = new Intent(context, MusicServices.class);
            context.bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            context.startService(playIntent);
        }
    }

    public ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicServices.MusicBinder binder = (MusicServices.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setTrackList(tracksRepository.getTrackList());
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };


    public void btnPlayClicked() {
        if (musicSrv == null) return;
        if (musicSrv.isPlaying()) {
            musicSrv.pause();
        } else {
            musicSrv.play();
        }
        updateUIPlayButton();
        musicSrv.createNotification(currentTrack, musicSrv.isPlaying());
    }


    public void updateUIPlayButton() {
        if (musicSrv.isPlaying()) {
            mainBinding.btnPlayPause.setImageResource(R.drawable.ic_baseline_pause_24);
        } else {
            mainBinding.btnPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }

    public void btnNextClicked() {
        currentTrack = tracksRepository.getNextTrack(currentTrack);
        musicSrv.playTrack(currentTrack, context);
        musicSrv.createNotification(currentTrack, true);
        updateUI();
    }

    public void btnPrevClicked() {
        currentTrack = tracksRepository.getPrevTrack(currentTrack);
        musicSrv.playTrack(currentTrack, context);
        musicSrv.createNotification(currentTrack, true);
        updateUI();
    }

    public void stopMusic() {
        musicSrv.onDestroy();
    }

    public void closeNotification() {
        musicSrv.pause();
        updateUIPlayButton();
        musicSrv.onDestroy();
        updateUI();
    }
}
