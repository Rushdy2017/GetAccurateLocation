package com.example.raza.locationaware;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.example.raza.locationaware.location.LocationManagerInterface;
import com.example.raza.locationaware.location.SmartLocationManager;
import com.example.raza.locationaware.service.LocationFetcherService;
import com.google.android.gms.location.LocationRequest;

import static android.R.string.ok;

/**
 * Created by Syed Raza Mehdi Naqvi on 8/16/2016.
 */

public class BaseActivityLocation extends BaseActivity implements LocationManagerInterface {

    private static final int REQUEST_FINE_LOCATION = 1;
    public SmartLocationManager mLocationManager;
    private Activity mCurrentActivity;
    private int count = 0;


    @Override
    public void locationFetched(Location mLocation, Location oldLocation, String time, String locationProvider) {
        GetAccurateLocationApplication.locationProvider = locationProvider;
        GetAccurateLocationApplication.mCurrentLocation = mLocation;
    }

    public void instantiate(Activity mActivity) {
        mLocationManager = new SmartLocationManager(getApplicationContext(), mActivity, SmartLocationManager.USE_UPDATE_TIME_GPS, this, SmartLocationManager.ALL_PROVIDERS, LocationRequest.PRIORITY_HIGH_ACCURACY, 10 * 1000, 1 * 1000, SmartLocationManager.LOCATION_PROVIDER_RESTRICTION_NONE, SmartLocationManager.ANY_API); // init location manager
    }

    public void initLocationFetching(Activity mActivity) {
        mCurrentActivity = mActivity;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showLocationPermission();
        } else {
            instantiate(mActivity);
        }
    }

    public void startLocationFetchingService() {
        Intent serviceIntent = new Intent(this, LocationFetcherService.class);
        startService(serviceIntent);
    }

    public void stopLocationFetchingService() {
        Intent serviceIntent = new Intent(this, LocationFetcherService.class);
        stopService(serviceIntent);
        LocationFetcherService.timerTask.cancel();
    }

    protected void onStart() {
        super.onStart();

    }

    protected void onResume() {
        super.onResume();
        showLocationPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onStop() {
        super.onStop();
        try {
            if (mLocationManager != null)
                mLocationManager.abortLocationFetching();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private void showLocationPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(mCurrentActivity, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_FINE_LOCATION);
        } else {
            instantiate(mCurrentActivity);
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
                    instantiate(BaseActivityLocation.this);
                    mLocationManager.startLocationFetching();
                    Toast.makeText(BaseActivityLocation.this, "Permission Granted!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(BaseActivityLocation.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    count++;
                    if (count == 3) {
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
                    } else {
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