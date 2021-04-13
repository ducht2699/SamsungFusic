package com.example.samsungfusic.repository;

import java.util.ArrayList;
import java.util.List;

public class IndicatorRepository {
    private List<String> indicators;
    public IndicatorRepository() {
        this.indicators = new ArrayList<>();
        setIndicators();
    }

    public List<String> getIndicators() {
        return indicators;
    }

    private void setIndicators() {
        indicators.clear();
        indicators.add("Spotify");
        indicators.add("Favourites");
        indicators.add("Playlists");
        indicators.add("Tracks");
        indicators.add("Albums");
        indicators.add("Artists");
        indicators.add("Folders");
    }
}
