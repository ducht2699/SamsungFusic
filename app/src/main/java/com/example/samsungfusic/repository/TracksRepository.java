package com.example.samsungfusic.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.example.samsungfusic.R;
import com.example.samsungfusic.Utils.INotifyAdapter;
import com.example.samsungfusic.fragments.TracksFragment;
import com.example.samsungfusic.models.Track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TracksRepository {
    private List<Track> trackList;
    private Context context;
    private Track currentTrack;

    private MutableLiveData<Track> trackMutableLiveData;

    public TracksRepository(Context context) {
        this.context = context;
        this.trackList = new ArrayList<>();
        this.trackMutableLiveData = new MutableLiveData<>();
        getAllSong();
    }

    public MutableLiveData<Track> getTrackMutableLiveData() {
        return trackMutableLiveData;
    }

    public Track getCurrentTrack() {
        return currentTrack;
    }

    public List<Track> getTrackList() {
        return trackList;
    }

    public void getAllSong() {
        new AllSong().execute();
//        getFromLocal();
    }

    private Track findTrackByID(String id) {
        for (Track track : trackList) {
            if (id.equals(track.getId())) {
                return track;
            }
        }
        throw new IllegalArgumentException();
    }

    public Track getNextTrack(Track currentTrack) {
        for (int i = 0; i < trackList.size(); i++) {
            if (trackList.get(i).getId().equals(currentTrack.getId())) {
                return trackList.get(i + 1);
            }
        }
        throw new IllegalArgumentException();
    }

    public Track getPrevTrack(Track currentTrack) {
        for (int i = 0; i < trackList.size(); i++) {
            if (trackList.get(i).getId().equals(currentTrack.getId())) {
                if (i == 0) {
                    return trackList.get(trackList.size() - 1);
                } else {
                    return trackList.get(i - 1);
                }
            }
        }
        throw new IllegalArgumentException();
    }

    private class AllSong extends AsyncTask<Void, Track, Void> {

        @Override
        protected void onProgressUpdate(Track... values) {
//            trackList.add(values[0]);
            trackMutableLiveData.postValue(values[0]);
        }

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
                        , cursor.getString(3), drawable);

//                trackList.add(track);
                publishProgress(track);
            }
            Log.d("TAGGG", trackList.size() + "");
            return null;
        }
    }





}
