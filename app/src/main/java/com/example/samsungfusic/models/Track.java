package com.example.samsungfusic.models;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class Track  {
    private String id;
    private String artist;
    private String title;
    private String m_sLocation;
    private Drawable m_iImage;

    public Track() {
    }

    public Track(String id, String artist, String title, String m_sLocation, Drawable m_iImage) {
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.m_sLocation = m_sLocation;
        this.m_iImage = m_iImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getM_sLocation() {
        return m_sLocation;
    }

    public void setM_sLocation(String m_sLocation) {
        this.m_sLocation = m_sLocation;
    }

    public Drawable getM_iImage() {
        return m_iImage;
    }

    public void setM_iImage(Drawable m_iImage) {
        this.m_iImage = m_iImage;
    }

    @NonNull
    @Override
    public String toString() {
        return this.title;
    }
}
