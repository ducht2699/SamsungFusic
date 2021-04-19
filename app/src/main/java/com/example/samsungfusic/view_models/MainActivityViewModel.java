//package com.example.samsungfusic.view_models;
//
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.content.ServiceConnection;
//import android.os.Build;
//import android.os.IBinder;
//
//import androidx.lifecycle.LifecycleOwner;
//import androidx.lifecycle.MutableLiveData;
//import androidx.lifecycle.ViewModel;
//
//import com.example.samsungfusic.R;
//import com.example.samsungfusic.databinding.ActivityMainBinding;
//import com.example.samsungfusic.models.Track;
//import com.example.samsungfusic.repository.IndicatorRepository;
//import com.example.samsungfusic.repository.TracksRepository;
//import com.example.samsungfusic.services.MusicServices;
//import com.example.samsungfusic.services.MusicServices.MusicBinder;
//import com.google.android.material.tabs.TabLayout;
//
//import java.util.List;
//
//public class MainActivityViewModel extends ViewModel {
//    private IndicatorRepository indicatorRepository;
//    private TracksRepository tracksRepository;
//    private ActivityMainBinding mainBinding;
//    private MusicServices musicServices;
//    private Intent playIntent;
//    private boolean musicBound = false;
//    private boolean isFirstPlay = true;
//    private boolean isPlaying = false;
//
//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            MusicBinder musicBinder = (MusicBinder) service;
//            musicServices = musicBinder.getServices();
//            musicBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            musicBound = false;
//        }
//    };
//
//    public MainActivityViewModel() {
//        this.indicatorRepository = new IndicatorRepository();
//    }
//
//    public void createMusicServices(Context context) {
//        if (playIntent == null) {
//            playIntent = new Intent(context, MusicServices.class);
//            context.bindService(playIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(playIntent);
//            }
//        }
//    }
//
//    public void setMainBinding(ActivityMainBinding binding) {
//        this.mainBinding = binding;
//    }
//
//    public void setIndicator(TabLayout tabLayout) {
//        for (String x : indicatorRepository.getIndicators()) {
//            tabLayout.addTab(tabLayout.newTab().setText(x));
//        }
//    }
//
//    public void createTrackRepo(Context context) {
//        tracksRepository = new TracksRepository(context);
//    }
//
//
//    public void btnPlayClicked() {
//        if (isPlaying) {
//            musicServices.pause();
//            isPlaying = false;
//        } else {
//            musicServices.play();
//            isPlaying = true;
//        }
//        updateUIPlayButton();
//    }
//
//
//    public void updateUIPlayButton() {
//        if (isPlaying) {
//            mainBinding.btnPlayPause.setImageResource(R.drawable.ic_baseline_pause_24);
//        } else {
//            mainBinding.btnPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
//        }
//    }
//
//    public void btnNextClicked() {
//        tracksRepository.changeNext();
//        updateUIPlayButton();
//    }
//
//    public void btnPrevClicked() {
//        tracksRepository.changePrevious();
//        updateUIPlayButton();
//    }
//
//    public void closeNotification() {
//        musicServices.onDestroy();
//    }
//
//    public MutableLiveData<List<Track>> getTracksList() {
//        return tracksRepository.getTrackList();
//    }
//
//    public void onTrackSelected(Track track) {
//        tracksRepository.setCurrentTrack(track);
//    }
//
//    public void initObserver(LifecycleOwner owner) {
//        tracksRepository.getCurrentTrack().observe(owner, track -> {
//            mainBinding.setTrack(track);
//            if (musicServices != null)
//                musicServices.setTrack(track, isFirstPlay);
//            else
//                isFirstPlay = false;
//            updateUIPlayButton();
//        });
//    }
//}


package com.example.samsungfusic.view_models;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.samsungfusic.R;
import com.example.samsungfusic.databinding.ActivityMainBinding;
import com.example.samsungfusic.models.Track;
import com.example.samsungfusic.repository.IndicatorRepository;
import com.example.samsungfusic.repository.TracksRepository;
import com.example.samsungfusic.services.MusicServices;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class MainActivityViewModel extends ViewModel {
    private IndicatorRepository indicatorRepository;
    private TracksRepository tracksRepository;
    private ActivityMainBinding mainBinding;
    private Context context;
    private MusicServices musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;

    public void setMainBinding(ActivityMainBinding mainBinding) {
        this.mainBinding = mainBinding;
    }

    public MainActivityViewModel() {
        this.indicatorRepository = new IndicatorRepository();
    }

    public void createTrackRepo(Context context) {
        this.context = context;
        this.tracksRepository = new TracksRepository(context);
    }

    public void setIndicator(TabLayout tabLayout) {
        for (String x : indicatorRepository.getIndicators()) {
            tabLayout.addTab(tabLayout.newTab().setText(x));
        }
    }

    public MutableLiveData<List<Track>> getTrackList() {
        return tracksRepository.getTrackList();
    }

    public void onTrackClicked(Track track) {
        tracksRepository.setCurrentTrack(track);
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
        musicSrv.postNotify(tracksRepository.getCurrentTrack().getValue(), musicSrv.isPlaying());
        updateUIPlayButton();
    }


    public void updateUIPlayButton() {
        if (musicSrv != null && musicSrv.isPlaying()) {
            mainBinding.btnPlayPause.setImageResource(R.drawable.ic_baseline_pause_24);
        } else {
            mainBinding.btnPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }

    public void btnNextClicked() {
        tracksRepository.changeNext();
    }

    public void btnPrevClicked() {
        tracksRepository.changePrevious();
    }


    public void closeNotification() {
        musicSrv.pause();
        updateUIPlayButton();
        musicSrv.onDestroy();
    }

    public void initObserver(LifecycleOwner owner) {
        tracksRepository.getCurrentTrack().observe(owner, track -> {
            mainBinding.setTrack(track);
            if (musicSrv != null) {
                musicSrv.playTrack(track, context);
            }
            updateUIPlayButton();
        });
    }
}