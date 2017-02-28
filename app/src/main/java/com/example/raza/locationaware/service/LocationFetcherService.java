package com.example.raza.locationaware.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.raza.locationaware.GetAccurateLocationApplication;
import com.example.raza.locationaware.location.LocationManagerInterface;
import com.example.raza.locationaware.location.SmartLocationManager;
import com.google.android.gms.location.LocationRequest;

import java.util.Timer;
import java.util.TimerTask;


public class LocationFetcherService extends Service implements LocationManagerInterface {

    private static final String TAG = LocationFetcherService.class.getSimpleName();
    private static final long SERVICE_PERIOD = (5) * 1000;

    private SmartLocationManager mLocationManager;
    private Timer mTimer;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initLocationFetching();

        Log.i(TAG, "Location Fetching Service Started");

        return START_STICKY;
    }

    public class LocationBinder extends Binder {
        public LocationFetcherService getService() {
            return LocationFetcherService.this;
        }
    }

    public void initLocationFetching() {
        try {
            if (getApplicationContext() != null) {
                mLocationManager = new SmartLocationManager(getApplicationContext(), SmartLocationManager.USE_UPDATE_TIME_GPS, this, SmartLocationManager.ALL_PROVIDERS, LocationRequest.PRIORITY_HIGH_ACCURACY, 10 * 1000, 1 * 1000, SmartLocationManager.LOCATION_PROVIDER_RESTRICTION_NONE, SmartLocationManager.ANY_API); // init location manager
//                mLocationManager = new SmartLocationManager(getApplicationContext(), SmartLocationManager.USE_UPDATE_TIME_GPS, this, SmartLocationManager.ALL_PROVIDERS, LocationRequest.PRIORITY_HIGH_ACCURACY, 10 * 1000, 1 * 1000, SmartLocationManager.LOCATION_PROVIDER_RESTRICTION_NONE, SmartLocationManager.ANY_API); // init location manager
            }
            mLocationManager.startLocationFetching();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timerTask.cancel();
        Log.i(TAG, "Location Fetching Service Destroyed");
    }

    @Override
    public void locationFetched(Location mLocation, Location oldLocation, String time, String locationProvider) {
        GetAccurateLocationApplication.mCurrentLocation = mLocation;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mTimer = new Timer();
        mTimer.schedule(timerTask, 20000, SERVICE_PERIOD);
    }

    public static TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Log.e(TAG, "lat : " + GetAccurateLocationApplication.mCurrentLocation.getLatitude() + " lng : " + GetAccurateLocationApplication.mCurrentLocation.getLongitude());
        }
    };
}
