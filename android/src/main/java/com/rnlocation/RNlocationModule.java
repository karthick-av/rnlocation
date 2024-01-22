package com.rnlocation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableNativeMap;

import java.util.Timer;
import java.util.TimerTask;

//@ReactModule(name = RNlocationModule.NAME)
public class RNlocationModule extends ReactContextBaseJavaModule {
  private LocationManager locationManager;


  private LocationListener GPSlistener;

  private LocationListener Networklistener;

  private boolean isSingleUpdate = false;



  private Timer timer;


  private Timer gpstimer;

  private boolean Requested = false;

  RNlocationModule(ReactApplicationContext context) {
    super(context);
    try {
      locationManager = (LocationManager) context.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);


    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Override
  public String getName() {
    return AppConstants.MODULE_NAME;
  }

    @ReactMethod
    public  void isGPSenabled(Promise promise){
        boolean hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    promise.resolve(hasGps);
    }

    
    @ReactMethod
    public void requestPermissionIfNeeded(Promise promise) {
        String[] permissions = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (ActivityCompat.checkSelfPermission(
                getReactApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getReactApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(getCurrentActivity(), permissions, 123);
            promise.resolve(false);
        } else {
            promise.resolve(true);
        }
    }

    @ReactMethod
    public boolean locationPermission() {
        String[] permissions = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (ActivityCompat.checkSelfPermission(
                getReactApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getReactApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(getCurrentActivity(), permissions, 123);
            return false;
        } else {
            return true;
        }
    }


    @ReactMethod
    public void promptGPSIfNeeded() {
        AppCompatActivity activity = (AppCompatActivity) getCurrentActivity();
        if (activity == null) return;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getCurrentActivity());
        // Setting Dialog Title
        alertDialog.setTitle(AppConstants.DIALOG_TITLE);

        // Setting Dialog Message
        alertDialog.setMessage(AppConstants.DIALOG_MESSAGE);

        // On pressing Settings button
        alertDialog.setPositiveButton(
                AppConstants.POSITIVE_BUTTON,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        activity.startActivity(intent);
                    }


                });

        alertDialog.setNegativeButton(
                AppConstants.NEGATIVE_BUTTON,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }


                });

        alertDialog.show();
    }

    public void removeGPSLocationUpdates() {

        if (timer != null) {
            timer.cancel();
        }

        if (gpstimer != null) {
            gpstimer.cancel();
        }


        locationManager.removeUpdates(GPSlistener);
    }


    public void removeNetworkLocationUpdates() {
        Requested = false;
        if (timer != null) {
            timer.cancel();
        }
        if (gpstimer != null) {
            gpstimer.cancel();
        }


        locationManager.removeUpdates(Networklistener);
    }


    private WritableNativeMap convertLocation(Location location, String property) {
        WritableNativeMap resultLocation = new WritableNativeMap();
        resultLocation.putString("provider", location.getProvider());
        resultLocation.putDouble("latitude", location.getLatitude());
        resultLocation.putDouble("longitude", location.getLongitude());
        resultLocation.putDouble("accuracy", location.getAccuracy());
        resultLocation.putDouble("altitude", location.getAltitude());
        resultLocation.putDouble("speed", location.getSpeed());
        resultLocation.putDouble("bearing", location.getBearing());
        resultLocation.putDouble("time", location.getTime());
        resultLocation.putString("property", property);
        return resultLocation;
    }

    private void getLastKnownLocation(Promise promise) {
        if (ActivityCompat.checkSelfPermission(getReactApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getReactApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location gpsLastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.d("gpsLastKnownLocation", String.valueOf(gpsLastKnownLocation));
            if (gpsLastKnownLocation != null) {
                WritableNativeMap resultLocation = convertLocation(gpsLastKnownLocation, "GPS-lastknown Location");

                promise.resolve(resultLocation);
            } else {
                Location networkLastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Log.d("netlocation", String.valueOf(networkLastKnownLocation));
                if (networkLastKnownLocation != null) {
                    WritableNativeMap resultLocation = convertLocation(networkLastKnownLocation, "Network -lastknown Location");
                    promise.resolve(resultLocation);
                } else {
                    Log.d("timout null", "getLastKnownLocation: ");
                    promise.reject("Location request timed out");
                }
            }
        } else {
            Log.d("timout permission", "getLastKnownLocation: ");

            promise.reject("Location request timed out");
        }

    }

    @ReactMethod
    public void getCurrentPosition(ReadableMap options, Promise promise) {
        try {
            int timout = AppConstants.DEFAULT_TIMOUT;
            int gps_timeout = AppConstants.GPS_DEFAULT_TIMEOUT;
            if (options.hasKey("timeout")) {
                int config_timout = options.getInt("timeout");
                timout = config_timout;
            }
            if (options.hasKey("gps_timeout")) {
                int gps_config_timout = options.getInt("gps_timeout");
                gps_timeout = gps_config_timout;
            }
             if(timout <= 10000) {
                 promise.reject("timeout must be greater than 10 seconds");
                 return;
             }
             if(gps_timeout >= timout){
                 promise.reject("timeout must be greater than GPS timeout");
                 return;

             }
            if (Requested) {
                promise.reject("You have already requested Please wait until it is complete.");
                return;
            }
            if (locationPermission()) {
                Log.d("timout", String.valueOf(timout));

                boolean hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (!hasGps && !hasNetwork) {
                    promptGPSIfNeeded();
                    promise.reject("GPS is required");
                    return;
                }
                if (hasGps || hasNetwork) {
                    Requested = true;
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                removeGPSLocationUpdates();
                                removeNetworkLocationUpdates();
                                // getLastKnownLocation(promise);
                                promise.reject("Loction request timed out");
                            } catch (Exception ex) {

                                promise.reject("something went wrong");
                                ex.printStackTrace();
                            }
                        }
                    }, timout);

                    isSingleUpdate = true;


                    GPSlistener = new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {

                            if (isSingleUpdate) {
                                WritableNativeMap resultLocation = convertLocation(location, "GPS location");
                                promise.resolve(resultLocation);

                                isSingleUpdate = false;
                            }
                            removeGPSLocationUpdates();
                            removeNetworkLocationUpdates();

                            Log.d("LOCATION POSITION", String.valueOf(location));

                        }

                        @Override
                        public void onProviderEnabled(@NonNull String provider) {

                        }

                        @Override
                        public void onProviderDisabled(@NonNull String provider) {

                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                            Log.d("GPS status provider", provider);
                            Log.d("GPS status status", String.valueOf(status));

                        }

                    };
                    Networklistener = new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {

                            if (isSingleUpdate) {
                                WritableNativeMap resultLocation = convertLocation(location, "Network location");
                                promise.resolve(resultLocation);

                                isSingleUpdate = false;
                            }
                            removeNetworkLocationUpdates();
                            removeGPSLocationUpdates();

                            Log.d("LOCATION POSITION", String.valueOf(location));

                        }

                        @Override
                        public void onProviderEnabled(@NonNull String provider) {

                        }

                        @Override
                        public void onProviderDisabled(@NonNull String provider) {

                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                            Log.d("NETWORK status provider", provider);
                            Log.d("NETWORK status status", String.valueOf(status));

                        }

                    };

                    if (hasGps) {


                        if (ActivityCompat.checkSelfPermission(getReactApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getReactApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            promise.reject("Permission is required");
                            return;
                        }
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                0L, 0F, GPSlistener
                        );

                        //  locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, GPSlistener, null);

                    }

                    gpstimer = new Timer();
                    gpstimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                if (hasNetwork) {
                                    if (ActivityCompat.checkSelfPermission(getReactApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getReactApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                                        locationManager.requestLocationUpdates(
                                                LocationManager.NETWORK_PROVIDER,
                                                0L, 0F,
                                                Networklistener);

                                        //     locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, Networklistener, null);

                                    }
                                    }
                                    }
                                });


                            } catch (Exception ex) {
                                promise.reject("something went wrong");
                                ex.printStackTrace();

                            }
                        }
                    }, 7000);




                }
            } else {
                promise.reject("Permission is required");
            }
        }

        catch(Exception ex){
            promise.reject("Something went wrong");
        }
    }


@ReactMethod
    public void isServiceRunning(Promise promise) {
        ActivityManager activityManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            activityManager = (ActivityManager)  getReactApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        }
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (BackgroundLocationService.class.getName().equals(service.service.getClassName())) {
                    if(service.foreground){
                       promise.resolve(true);
                    }
                }
            }

            promise.resolve(false);
        }
    promise.resolve(false);
    }

    public boolean isServiceLocationRunning() {
        ActivityManager activityManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            activityManager = (ActivityManager)  getReactApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        }
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (BackgroundLocationService.class.getName().equals(service.service.getClassName())) {
                    if(service.foreground){
                        return true;
                    }
                }
            }
            return  false;
        }
        return  false;
    }


    @ReactMethod
    public void startBackgroundService() {
        if (ActivityCompat.checkSelfPermission(getReactApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getReactApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getReactApplicationContext(), android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (!isServiceLocationRunning()) {

            Intent intent = new Intent(getReactApplicationContext(), BackgroundLocationService.class);
            intent.setAction(AppConstants.ACTION_START_LOCATION_SERVICE);
            getReactApplicationContext().startService(intent);
            Toast.makeText(getReactApplicationContext(), "Start Location Service", Toast.LENGTH_SHORT).show();
        }
    }





    @ReactMethod
    public  void StopBackgroundService(){
        if(isServiceLocationRunning()){
            Intent intent = new Intent(getReactApplicationContext(), BackgroundLocationService.class);
            intent.setAction(AppConstants.ACTION_STOP_LOCATION_SERVICE);
            getReactApplicationContext().startService(intent);
            Toast.makeText(getReactApplicationContext(),"Stop Location Service",Toast.LENGTH_SHORT).show();
        }
    }



}
