package com.example.samsungfusic.fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samsungfusic.R;
import com.example.samsungfusic.adapters.TracksAdapter;
import com.example.samsungfusic.databinding.FragmentTracksBinding;
import com.example.samsungfusic.models.Track;
import com.example.samsungfusic.view_models.MainActivityViewModel;

import java.util.List;

public class TracksFragment extends Fragment {

    public TracksFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentTracksBinding mBinding = FragmentTracksBinding.inflate(inflater, container, false);
        MainActivityViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        viewModel.createTrackRepo(requireActivity().getApplicationContext());

        TracksAdapter tracksAdapter = new TracksAdapter(viewModel.getTrackList(), getContext(), viewModel);
        mBinding.rcvTrackList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mBinding.rcvTrackList.setAdapter(tracksAdapter);
        tracksAdapter.notifyDataSetChanged();
        return mBinding.getRoot();
    }
}