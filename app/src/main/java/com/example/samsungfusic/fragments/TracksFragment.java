package com.example.samsungfusic.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samsungfusic.adapters.TracksAdapter;
import com.example.samsungfusic.databinding.FragmentTracksBinding;
import com.example.samsungfusic.interfaces.ITrackClickHandler;
import com.example.samsungfusic.models.Track;
import com.example.samsungfusic.view_models.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;

public class TracksFragment extends Fragment {
    private ITrackClickHandler trackClickHandler;
    public TracksFragment(ITrackClickHandler trackClickHandler) {
        this.trackClickHandler = trackClickHandler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentTracksBinding mBinding = FragmentTracksBinding.inflate(inflater, container, false);
        MainActivityViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        List<Track> trackList = new ArrayList<>();
        TracksAdapter tracksAdapter = new TracksAdapter(trackList, getContext(), trackClickHandler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mBinding.rcvTrackList.setLayoutManager(linearLayoutManager);
        mBinding.rcvTrackList.setAdapter(tracksAdapter);
        viewModel.getTracksList().observe(getViewLifecycleOwner(), new Observer<List<Track>>() {
            @Override
            public void onChanged(List<Track> tracks) {
                mBinding.prgLoading.setVisibility(View.GONE);
                trackList.addAll(tracks);
                tracksAdapter.notifyDataSetChanged();
            }
        });
        mBinding.rcvTrackList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager ln = (LinearLayoutManager) mBinding.rcvTrackList.getLayoutManager();
                int itemCount = ln.getItemCount();
                int lastItem = ln.findLastCompletelyVisibleItemPosition();

            }
        });
        return mBinding.getRoot();
    }
}