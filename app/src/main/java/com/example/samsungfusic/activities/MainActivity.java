package com.example.samsungfusic.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.samsungfusic.R;
import com.example.samsungfusic.Utils.Constants;
import com.example.samsungfusic.adapters.view_pager_adapter.MainContentViewPagerAdapter;
import com.example.samsungfusic.broadcasts.MusicReceiver;
import com.example.samsungfusic.databinding.ActivityMainBinding;
import com.example.samsungfusic.interfaces.ITrackClickHandler;
import com.example.samsungfusic.models.Track;
import com.example.samsungfusic.view_models.MainActivityViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.Tab;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog.Builder;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks;


public  class MainActivity extends AppCompatActivity implements OnClickListener, PermissionCallbacks, ITrackClickHandler {
    private ActivityMainBinding mBinding;
    private MainActivityViewModel mViewModel;

    public boolean onCreateOptionsMenu(@NotNull Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    protected void onDestroy() {
        mViewModel.closeNotification();
        if(!Realm.getDefaultInstance().isClosed()) {
            Realm.getDefaultInstance().close();
        }
        super.onDestroy();
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initPermission();
        initComponent();
        initObserver();
        initTabLayout();
        initViewPager();
        initNavigationButtons();
        initServices();
        initReceiver();
    }

    private void initObserver() {
        mViewModel.getCurrentTrack().observe(this, track -> {
            mBinding.setTrack(track);
            Log.d(Constants.DEBUG_LIVEDATA, track.toString());
        });
    }


    private void initReceiver() {
        MusicReceiver musicReceiver = new MusicReceiver() {
            public void onReceive(@NotNull Context context, @NotNull Intent intent) {
                if (intent.getAction().equals(Constants.CLICK_PREVIOUS)) {
                    mViewModel.btnPrevClicked();
                }

                if (intent.getAction().equals(Constants.CLICK_PLAY)) {
                    mViewModel.btnPlayClicked();
                }

                if (intent.getAction().equals(Constants.CLICK_PAUSE)) {
                    mViewModel.btnPlayClicked();
                }

                if (intent.getAction().equals(Constants.CLICK_NEXT)) {
                    mViewModel.btnNextClicked();
                }

                if (intent.getAction().equals(Constants.CLICK_CLOSE)) {
                    mViewModel.closeNotification();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.CLICK_PREVIOUS);
        intentFilter.addAction(Constants.CLICK_PLAY);
        intentFilter.addAction(Constants.CLICK_PAUSE);
        intentFilter.addAction(Constants.CLICK_NEXT);
        intentFilter.addAction(Constants.CLICK_CLOSE);
        registerReceiver(musicReceiver, intentFilter);
    }

    private  void initServices() {
        mViewModel.initService(getApplicationContext());
    }

    private  void initNavigationButtons() {
        mBinding.btnPlayPause.setOnClickListener(this);
        mBinding.btnNext.setOnClickListener(this);
        mBinding.btnPrevious.setOnClickListener(this);
    }

    private  void initViewPager() {
        MainContentViewPagerAdapter adapter = new MainContentViewPagerAdapter(getSupportFragmentManager(), this);
        mBinding.vpgPlayList.setAdapter(adapter);
        mBinding.vpgPlayList.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mBinding.tloIndicator));
        mBinding.vpgPlayList.setCurrentItem(3);
        mBinding.vpgPlayList.setOffscreenPageLimit(Constants.FOLDERS + 1);
    }

    private  void initTabLayout() {
        mViewModel.setIndicator(mBinding.tloIndicator);
        mBinding.tloIndicator.addOnTabSelectedListener((new OnTabSelectedListener() {
            public void onTabSelected(@NotNull Tab tab) {
                mBinding.vpgPlayList.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(@NotNull Tab tab) {
            }

            public void onTabReselected(@NotNull Tab tab) {
            }
        }));
    }

    private  void initComponent() {
        mBinding.tvSongName.setSelected(true);
        mBinding.tvArtist.setSelected(true);
        setTitle("SAMSUNG Fusic");
        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        mViewModel.setMainBinding(mBinding);
        mViewModel.createTrackRepo(getApplicationContext());
        mViewModel.createMusicServices();
    }

    public void onClick(@NotNull View v) {
        switch (v.getId()) {
            case R.id.btn_previous:
                mViewModel.btnPrevClicked();
                break;
            case R.id.btn_play_pause:
                mViewModel.btnPlayClicked();
                break;
            case R.id.btn_next:
                mViewModel.btnNextClicked();
        }
    }

    @AfterPermissionGranted(123)
    private  void initPermission() {
        String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WAKE_LOCK};
        if (!EasyPermissions.hasPermissions( this,  perms)) {
            EasyPermissions.requestPermissions( this, "We need permissions because this and that", 123,  perms);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void onPermissionsGranted(int requestCode, @NotNull List perms) {
    }

    public void onPermissionsDenied(int requestCode, @NotNull List perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied( this, perms)) {
            (new Builder( this)).build().show();
        }

    }

    @Override
    public void onTrackSelected(Track track) {
        mViewModel.onTrackSelected(track);
    }
}
