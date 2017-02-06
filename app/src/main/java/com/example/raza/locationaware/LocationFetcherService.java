package com.example.raza.locationaware;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.example.raza.locationaware.location.LocationManagerInterface;
import com.example.raza.locationaware.location.SmartLocationManager;
import com.google.android.gms.location.LocationRequest;

import java.util.Timer;
import java.util.TimerTask;



public class LocationFetcherService extends Service implements LocationManagerInterface {

    private SmartLocationManager mLocationManager;
    private BaseActivityLocation mBal;
    private Activity mActivity;
    private double lat;
    private double lon;
    private static final long SERVICE_PERIOD = (5) * 1000;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initLocationFetching();
        mBal = new BaseActivityLocation();
        //mBal.initLocationFetching(ge);

        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();

        return START_STICKY;
    }

    public class LocationBinder extends Binder {
        public LocationFetcherService getService() {
            return LocationFetcherService.this;
        }
    }

    public void initLocationFetching() {
        try {
            if (mActivity != null) {
                //mLocationManager = new SmartLocationManager(mContext, mActivity, this, SmartLocationManager.ALL_PROVIDERS, LocationRequest.PRIORITY_HIGH_ACCURACY, 10 * 1000, 1 * 1000, SmartLocationManager.LOCATION_PROVIDER_RESTRICTION_NONE); // init location manager
                mBal.instatntiate(mActivity);
            }
            //mLocationManager.startLocationFetching();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*@Override
    public void onDestroy() {
        super.onDestroy();
        timerTask.cancel();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        *//*if (Constants.HARDCODE)
            *//*
    }*/

    @Override
    public void locationFetched(Location mLocation, Location oldLocation, String time, String locationProvider) {
        //AntiSmugglingApp.mCurrentLocation = mLocation;
        lat = mLocation.getLatitude();
        lon = mLocation.getLongitude();
    }

    private Timer mTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        mTimer = new Timer();
        mTimer.schedule(timerTask, 20000, SERVICE_PERIOD);
    }

    static TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            //Globals.getUsage().initWebRequestPatrolPath(AntiSmugglingApp.mCurrentLocation.getLatitude(), AntiSmugglingApp.mCurrentLocation.getLongitude(), new SharedPreferencesEditor(getApplication()));
            //Toast.makeText(getApplicationContext(), "Lat: " + lat + ", Long: " + lon , Toast.LENGTH_SHORT).show();
        }
    };
}
