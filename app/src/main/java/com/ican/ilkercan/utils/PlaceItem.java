package com.ican.ilkercan.utils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ilkercan on 14/02/2018.
 */

public class PlaceItem {

    private String mName;
    private LatLng mLatLng;
    private String mKey;

    public String getKey() {
        return mKey;
    }

    public void setKey(String mKey) {
        this.mKey = mKey;
    }

    public PlaceItem(String name, LatLng place, String key)
    {
        mName = name;
        mLatLng = place;
        mKey = key;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public LatLng getLatLng() {
        return mLatLng;
    }

    public void setLatLng(LatLng mLatLng) {
        this.mLatLng = mLatLng;
    }
}
