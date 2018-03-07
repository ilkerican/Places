package com.ican.ilkercan.utils;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by ilkercan on 14/02/2018.
 */

public class ShowPlace {

    private String mName;
    private String mKey;
    private String mAddress;
    private String mDuration;
    private String mDistance;

    public LatLng getLatLng() {
        return mLatLng;
    }

    public void setLatLng(LatLng mLatLng) {
        this.mLatLng = mLatLng;
    }

    private LatLng mLatLng;

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String mKey) {
        this.mKey = mKey;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getDuration() {
        return mDuration;
    }

    public void setDuration(String mDuration) {
        this.mDuration = mDuration;
    }

    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String mDistance) {
        this.mDistance = mDistance;
    }
}
