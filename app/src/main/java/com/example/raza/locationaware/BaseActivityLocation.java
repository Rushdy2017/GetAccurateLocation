package com.example.raza.locationaware;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.raza.locationaware.activity.LocationActivity;
import com.example.raza.locationaware.location.LocationManagerInterface;
import com.example.raza.locationaware.location.SmartLocationManager;
import com.google.android.gms.location.LocationRequest;

import static android.R.string.ok;

/**
 * Created by Syed Raza Mehdi Naqvi on 8/16/2016.
 */

public class BaseActivityLocation extends AppCompatActivity implements LocationManagerInterface {


    public SmartLocationManager mLocationManager;
    public LocationFetcherService mLocationFetcherService;
    private static final int REQUEST_FINE_LOCATION = 1;
    private Activity mCurrentActivity;
    private int count = 0;


    @Override
    public void locationFetched(Location mLocation, Location oldLocation, String time, String locationProvider) {
        // storing it on application level
        GetAccurateLocationApplication.mCurrentLocation = mLocation;
        GetAccurateLocationApplication.oldLocation = oldLocation;
        GetAccurateLocationApplication.locationProvider = locationProvider;
        GetAccurateLocationApplication.locationTime = time;
    }

    public void instatntiate(Activity mActivity){
        mLocationManager = new SmartLocationManager(getApplicationContext(), mActivity, SmartLocationManager.USE_UPDATE_TIME_GPS, this, SmartLocationManager.ALL_PROVIDERS, LocationRequest.PRIORITY_HIGH_ACCURACY, 10 * 1000, 1 * 1000, SmartLocationManager.LOCATION_PROVIDER_RESTRICTION_NONE, SmartLocationManager.ANY_API); // init location manager
    }

    public void initLocationFetching(Activity mActivity) {
        mCurrentActivity = mActivity;
        // ask permission for M
        // if (mLocationManager.askLocationPermission())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showLocationPermission();
        } else {
            instatntiate(mActivity);
        }
    }

    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, LocationFetcherService.class);
        //serviceIntent.setAction()
        startService(serviceIntent);
       /* if (mLocationManager != null) {
            mLocationManager.startLocationFetching();
            //mLocationManager.abortLocationFetching();
        }*/
    }

    protected void onResume(){
        super.onResume();
        /*int permissionCheck = ContextCompat.checkSelfPermission(mCurrentActivity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            *//*if (ActivityCompat.shouldShowRequestPermissionRationale(mCurrentActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showExplanation("Permission Needed", "Rationale", Manifest.permission.READ_PHONE_STATE, REQUEST_FINE_LOCATION);
            } else {*//*
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION);
            //}
        }else{
            mLocationManager = new SmartLocationManager(getApplicationContext(), BaseActivityLocation.this, this, SmartLocationManager.ALL_PROVIDERS, LocationRequest.PRIORITY_HIGH_ACCURACY, 10 * 1000, 1 * 1000, SmartLocationManager.LOCATION_PROVIDER_RESTRICTION_NONE, SmartLocationManager.ONLY_GOOGLE_API);
        }*/
        showLocationPermission();
        //mLocationManager = new SmartLocationManager(getApplicationContext(), BaseActivityLocation.this, this, SmartLocationManager.ALL_PROVIDERS, LocationRequest.PRIORITY_HIGH_ACCURACY, 10 * 1000, 1 * 1000, SmartLocationManager.LOCATION_PROVIDER_RESTRICTION_NONE, SmartLocationManager.ONLY_GOOGLE_API); // init location manager
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent serviceIntent = new Intent(this, LocationFetcherService.class);
        stopService(serviceIntent);
        LocationFetcherService.timerTask.cancel();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    protected void onStop() {
        super.onStop();
        if (mLocationManager != null)
            mLocationManager.abortLocationFetching();
    }

    @Override
    protected void onPause() {
        super.onPause();
       /* if (mLocationManager != null)
            mLocationManager.pauseLocationFetching();*/
    }


    private void showLocationPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(mCurrentActivity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            /*if (ActivityCompat.shouldShowRequestPermissionRationale(mCurrentActivity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showExplanation("Permission Needed", "Rationale", Manifest.permission.READ_PHONE_STATE, REQUEST_FINE_LOCATION);
            } else {*/
                requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION);
            //}
        } else {
            //Toast.makeText(mCurrentActivity, "Permission (already) Granted!", Toast.LENGTH_SHORT).show();
            instatntiate(mCurrentActivity);
            //mLocationManager.startLocationFetching();
        }
    }

    private void showExplanation(String title, String message, final String permission, final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mCurrentActivity);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(mCurrentActivity, new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    instatntiate(BaseActivityLocation.this);
                    mLocationManager.startLocationFetching();
                    Toast.makeText(BaseActivityLocation.this, "Permission Granted!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(BaseActivityLocation.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    count++;
                    if(count == 3){
                        AlertDialog.Builder builder = new AlertDialog.Builder(mCurrentActivity);
                        builder.setTitle("Warning");
                        builder.setMessage("Click Ok to go to permission settings Or click cancel to close");
                        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startInstalledAppDetailsActivity(mCurrentActivity);
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).show();
                    }else{
                        showLocationPermission();
                    }
                }
        }
    }

    public void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(intent);
    }
}