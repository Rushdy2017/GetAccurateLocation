package com.example.raza.locationaware.location;

import android.location.Location;

/**
 * Created by Syed Raza Mehdi Naqvi on 8/10/2016.
 */
public interface LocationManagerInterface {
    String TAG = LocationManagerInterface.class.getSimpleName();

    void locationFetched(Location mLocation, Location oldLocation, String time, String locationProvider);

}
