package com.example.samsungfusic.adapters.view_pager_adapter;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.samsungfusic.Utils.Constants;
import com.example.samsungfusic.fragments.AlbumsFragment;
import com.example.samsungfusic.fragments.ArtistFragment;
import com.example.samsungfusic.fragments.FavouriteFragment;
import com.example.samsungfusic.fragments.FolderFragment;
import com.example.samsungfusic.fragments.PlaylistFragment;
import com.example.samsungfusic.fragments.SpotifyFragment;
import com.example.samsungfusic.fragments.TracksFragment;
import com.example.samsungfusic.view_models.MainActivityViewModel;

public class MainContentViewPagerAdapter extends FragmentStatePagerAdapter {

    public MainContentViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case Constants.SPOTIFY:
                return new SpotifyFragment();
            case Constants.FAVOURITE:
                return new FavouriteFragment();
            case Constants.PLAYLISTS:
                return new PlaylistFragment();
            case Constants.TRACKS:
                return new TracksFragment();
            case Constants.ALBUMS:
                return new AlbumsFragment();
            case Constants.ARTISTS:
                return new ArtistFragment();
            case Constants.FOLDERS:
                return new FolderFragment();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public int getCount() {
        return Constants.FOLDERS + 1;
    }
}
