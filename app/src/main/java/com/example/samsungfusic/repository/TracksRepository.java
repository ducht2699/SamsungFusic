package com.example.samsungfusic.repository;

import android.content.Context;
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
    private List<Track> trackList;
    private MutableLiveData<List<Track>> tempTracks;
    private MutableLiveData<Track> currentTrack;

    public TracksRepository(Context context) {
        this.context = context;
        trackList = new ArrayList<>();
        tempTracks = new MutableLiveData<>();
        this.currentTrack = new MutableLiveData<>();
        new AllSong().execute();
    }

    public void setCurrentTrack(Track track) {
        this.currentTrack.setValue(track);
    }

    public MutableLiveData<Track> getCurrentTrack() {
        return currentTrack;
    }

    public MutableLiveData<List<Track>> getTempTracks() {
        return tempTracks;
    }

    public void changePrevious() {
        for (int i = 0; i < trackList.size(); i++) {
            if (currentTrack.getValue().getId().equals(trackList.get(i).getId())) {
                if  (i == 0) {
                    currentTrack.setValue(trackList.get(trackList.size() - 1));
                } else {
                    currentTrack.setValue(trackList.get(i - 1));
                }
                break;
            }
        }
    }

//    public List<Track> loadMoreTracks() {
//        for (int i = 0; i < trackList.size(); i++) {
//            if (trackList.get(i).getId().equals(tempTracks.get(tempTracks.size() - 1).getId())) {
//                tempTracks.clear();
//                if (trackList.size() - 1 - i < 10) {
//                    tempTracks.addAll(trackList.subList(i + 1, trackList.size() - 1));
//                } else {
//                    tempTracks.addAll(trackList.subList(i + 1, i + 1 + 10));
//                }
//            }
//        }
//        return tempTracks;
//    }

    public void changeNext() {
        for (int i = 0; i < trackList.size(); i++) {
            if (currentTrack.getValue().getId().equals(trackList.get(i).getId())) {
                if  (i == trackList.size() - 1) {
                    currentTrack.setValue(trackList.get(0));
                } else {
                    currentTrack.setValue(trackList.get(i + 1));
                }
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
                trackList.add(track);
                if (trackList.size() == 8)
                    tempTracks.postValue(trackList);
                if (trackList.size() == 2) {
                    currentTrack.postValue(trackList.get(1));
                }
            }
            return null;
        }
    }
}
