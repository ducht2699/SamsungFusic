package com.example.samsungfusic.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.provider.MediaStore;

import androidx.lifecycle.MutableLiveData;

import com.example.samsungfusic.R;
import com.example.samsungfusic.models.Track;

import java.util.ArrayList;
import java.util.List;

public class TracksRepository {
    private Context context;
    private MutableLiveData<List<Track>> trackList;
    private MutableLiveData<Track> currentTrack;


    public TracksRepository(Context context) {

        this.context = context;
        this.trackList = new MutableLiveData<>();
        this.currentTrack = new MutableLiveData<>();
        new AllSong().execute();
    }

    public void setCurrentTrack(Track track) {
        currentTrack.setValue(track);
        saveLastTrack();
    }

    public MutableLiveData<Track> getCurrentTrack() {
        return currentTrack;
    }

    private void saveLastTrack() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("lastTrack", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("idLastTrack", currentTrack.getValue().getId());
        editor.apply();
    }

    private String getLastTrackId() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("lastTrack", Context.MODE_PRIVATE);
        return sharedPreferences.getString("idLastTrack", null);
    }

    public MutableLiveData<List<Track>> getTrackList() {
        return trackList;
    }

    public void changePrevious() {
        for (int i = 0; i < trackList.getValue().size(); i++) {
            if (currentTrack.getValue().getId().equals(trackList.getValue().get(i).getId())) {
                if (i == 0) {
                    currentTrack.setValue(trackList.getValue().get(trackList.getValue().size() - 1));
                } else {
                    currentTrack.setValue(trackList.getValue().get(i - 1));
                }
                saveLastTrack();
                break;
            }
        }
    }


    public void changeNext() {
        for (int i = 0; i < trackList.getValue().size(); i++) {
            if (currentTrack.getValue().getId().equals(trackList.getValue().get(i).getId())) {
                if (i == trackList.getValue().size() - 1) {
                    currentTrack.setValue(trackList.getValue().get(0));
                } else {
                    currentTrack.setValue(trackList.getValue().get(i + 1));
                }
                saveLastTrack();
                break;
            }
        }
    }

    public class AllSong extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
            String[] projection = {
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA
            };

            Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null);
            List<Track> tracks = new ArrayList<>();
            trackList.postValue(tracks);
            while (cursor.moveToNext()) {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(cursor.getString(3));
                byte[] bytes = mmr.getEmbeddedPicture();
                Drawable drawable = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    drawable = context.getDrawable(R.drawable.ic_baseline_music_note_24);
                }
                if (bytes != null) {
                    drawable = new BitmapDrawable(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                }
                Track track = new Track(cursor.getString(0)
                        , cursor.getString(1)
                        , cursor.getString(2)
                        , cursor.getString(3)
                        , drawable);
                tracks.add(track);
                if (getLastTrackId() != null && track.getId().equals(getLastTrackId())) {
                    currentTrack.postValue(track);
                }
            }
            return null;
        }
    }
}
