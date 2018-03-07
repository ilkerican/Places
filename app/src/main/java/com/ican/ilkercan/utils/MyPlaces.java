package com.ican.ilkercan.utils;

import java.util.ArrayList;

/**
 * Created by ilkercan on 14/02/2018.
 */

public class MyPlaces {

    private ArrayList<PlaceItem> mPlaces;

    public MyPlaces()
    {
        mPlaces = new ArrayList<>();
    }

    public void AddPlace(PlaceItem placeItem)
    {
        mPlaces.add(placeItem);
    }

    public ArrayList<PlaceItem> getPlaces() {
        return mPlaces;
    }

    public void DeletePlace(String key)
    {
        int len = mPlaces.size();

        if(len > 0)
        {
            for(int i = 0; i < len; i ++)
            {
                if(((PlaceItem)(mPlaces.get(i))).getKey().toString().equals(key))
                {
                    mPlaces.remove(i);
                    break;
                }
            }
        }
    }
}
