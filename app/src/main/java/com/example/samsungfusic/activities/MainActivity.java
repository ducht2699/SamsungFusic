package com.example.samsungfusic.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.viewpager.widget.ViewPager;

import com.example.samsungfusic.R;
import com.example.samsungfusic.Utils.Constants;
import com.example.samsungfusic.adapters.view_pager_adapter.MainContentViewPagerAdapter;
import com.example.samsungfusic.broadcasts.MusicReceiver;
import com.example.samsungfusic.databinding.ActivityMainBinding;
import com.example.samsungfusic.view_models.MainActivityViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.Tab;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import kotlin.jvm.internal.Intrinsics;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog.Builder;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks;


public final class MainActivity extends AppCompatActivity implements OnClickListener, PermissionCallbacks {
    private ActivityMainBinding mBinding;
    private MainActivityViewModel mViewModel;
    private MusicReceiver musicReceiver;

    public boolean onCreateOptionsMenu(@NotNull Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initComponent();
        initPermission();
        initTabLayout();
        initViewPager();
        initNavigationButtons();
        initServices();
        initReceiver();
    }

    private final void initReceiver() {
        this.musicReceiver = new MusicReceiver() {
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

    private final void initServices() {
        mViewModel.initService(getApplicationContext());
    }

    private final void initNavigationButtons() {
        mBinding.btnPlayPause.setOnClickListener((OnClickListener) this);
        mBinding.btnNext.setOnClickListener((OnClickListener) this);
        mBinding.btnPrevious.setOnClickListener((OnClickListener) this);
    }

    private final void initViewPager() {
        mBinding.vpgPlayList.setAdapter(new MainContentViewPagerAdapter(getSupportFragmentManager()));
        mBinding.vpgPlayList.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mBinding.tloIndicator));
        mBinding.vpgPlayList.setCurrentItem(3);
    }

    private final void initTabLayout() {
        mViewModel.setIndicator(mBinding.tloIndicator);
        mBinding.tloIndicator.addOnTabSelectedListener((OnTabSelectedListener) (new OnTabSelectedListener() {
            public void onTabSelected(@NotNull Tab tab) {
                mBinding.vpgPlayList.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(@NotNull Tab tab) {
            }

            public void onTabReselected(@NotNull Tab tab) {
            }
        }));
    }

    private final void initComponent() {
        mBinding.tvSongName.setSelected(true);
        mBinding.tvArtist.setSelected(true);
        setTitle((CharSequence) "SAMSUNG Fusic");
        mViewModel = (MainActivityViewModel) (new ViewModelProvider((ViewModelStoreOwner) this)).get(MainActivityViewModel.class);
        mViewModel.setMainBinding(mBinding);
    }

    public void onClick(@NotNull View v) {
        switch (v.getId()) {
            case R.id.btn_previous:
                mViewModel.btnPlayClicked();
                break;
            case R.id.btn_play_pause:
                mViewModel.btnNextClicked();
                break;
            case R.id.btn_next:
                mViewModel.btnPrevClicked();
        }

    }

    @AfterPermissionGranted(123)
    private final void initPermission() {
        String[] perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions((Context) this, (String[]) Arrays.copyOf(perms, perms.length))) {
            EasyPermissions.requestPermissions((Activity) this, "We need permissions because this and that", 123, (String[]) Arrays.copyOf(perms, perms.length));
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    public void onPermissionsGranted(int requestCode, @NotNull List perms) {
    }

    public void onPermissionsDenied(int requestCode, @NotNull List perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied((Activity) this, perms)) {
            (new Builder((Activity) this)).build().show();
        }

    }


}
