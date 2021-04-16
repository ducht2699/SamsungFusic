package com.example.samsungfusic.view_models;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.samsungfusic.R;
import com.example.samsungfusic.activities.MainActivity;
import com.example.samsungfusic.databinding.ActivityMainBinding;
import com.example.samsungfusic.interfaces.ITrackClickHandler;
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
    private MusicServices musicServices;


    public MainActivityViewModel() {
        this.indicatorRepository = new IndicatorRepository();
    }

    public void createMusicServices() {
        this.musicServices = new MusicServices();
    }

    public void setMainBinding(ActivityMainBinding binding) {
        this.mainBinding = binding;
    }

    public void setIndicator(TabLayout tabLayout) {
        for (String x : indicatorRepository.getIndicators()) {
            tabLayout.addTab(tabLayout.newTab().setText(x));
        }
    }

    public void createTrackRepo(Context context) {
        tracksRepository = new TracksRepository(context);
    }

    public void initService(Context context) {

    }


    public void btnPlayClicked() {
    }


    public void updateUIPlayButton() {
        if (musicServices.isPlaying()) {
            mainBinding.btnPlayPause.setImageResource(R.drawable.ic_baseline_pause_24);
        } else {
            mainBinding.btnPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }

    public void btnNextClicked() {
        tracksRepository.changeNext();
        updateUIPlayButton();
    }

    public void btnPrevClicked() {
        tracksRepository.changePrevious();
        updateUIPlayButton();
    }

    public void stopMusic() {

    }

    public void closeNotification() {

    }

    public MutableLiveData<List<Track>> getTracksList() {
        return tracksRepository.getTrackList();
    }

    public void onTrackSelected(Track track) {
        tracksRepository.setCurrentTrack(track);
    }

    public MutableLiveData<Track> getCurrentTrack() {
        return tracksRepository.getCurrentTrack();
    }


}
