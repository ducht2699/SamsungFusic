package com.example.samsungfusic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.samsungfusic.databinding.TrackItemBinding;
import com.example.samsungfusic.interfaces.ITrackClickHandler;
import com.example.samsungfusic.models.Track;
import java.util.List;

public class TracksAdapter extends RecyclerView.Adapter<TracksAdapter.ViewHolder>{
    private List<Track> tracks;
    private Context context;
    private ITrackClickHandler iTrackClickHandler;

    public TracksAdapter(List<Track> tracks, Context context, ITrackClickHandler iTrackClickHandler) {
        this.tracks = tracks;
        this.context = context;
        this.iTrackClickHandler = iTrackClickHandler;
    }

    @NonNull
    @Override
    public TracksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        TrackItemBinding trackItemBinding = TrackItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(trackItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TracksAdapter.ViewHolder holder, int position) {
        Track track = tracks.get(position);
        holder.bind(track, position);
        holder.trackItemBinding.rllSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iTrackClickHandler.onTrackSelected(track);
            }
        });

    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TrackItemBinding trackItemBinding;

        public ViewHolder(TrackItemBinding binding) {
            super(binding.getRoot());
            this.trackItemBinding = binding;
        }

        public void bind(Track track, int pos) {
            if (tracks.size() == 1) {
                trackItemBinding.topLine.setVisibility(View.GONE);
                trackItemBinding.bottomLine.setVisibility(View.GONE);
            } else if (pos == 0 || pos != tracks.size() - 1) {
                trackItemBinding.topLine.setVisibility(View.GONE);
            } else if (pos == tracks.size() - 1){
                trackItemBinding.bottomLine.setVisibility(View.GONE);
            }
            trackItemBinding.tvSongName.setText(track.getTitle());
            trackItemBinding.tvArtist.setText(track.getArtist());
            trackItemBinding.imvSongImage.setImageDrawable(track.getM_iImage());
        }
    }
}
