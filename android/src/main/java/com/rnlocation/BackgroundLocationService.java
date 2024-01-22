package com.rnlocation;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableNativeMap;

public class BackgroundLocationService extends Service {
    private LocationManager locationManager;
    private LocationListener GPSlistener;

    private LocationListener Networklistener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            GPSlistener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {


                    Log.d("BACKGROUND LOCATION", String.valueOf(location));

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


                    Log.d("BACKGROUND LOCATION", String.valueOf(location));

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


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(AppConstants.ACTION_START_LOCATION_SERVICE)) {
                    startLocationService();
                } else if (action.equals(AppConstants.ACTION_STOP_LOCATION_SERVICE)) {
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private void startLocationService() {
        String channelId = "background_notification_channel";
        String TITLE = "test";
        String BODY = "test";

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_IMMUTABLE
        );
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(),
                channelId
        );
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(TITLE);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText(BODY);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null
                    && notificationManager.getNotificationChannel(channelId) == null) {

                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        "Location Service",
                        NotificationManager.IMPORTANCE_HIGH
                );

                notificationChannel.setDescription("location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000, 10, GPSlistener
        );
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000, 10, Networklistener
        );
        startForeground(AppConstants.LOCATION_SERVICE_ID, builder.build());
    }


    private  void stopLocationService(){
                if(locationManager != null){
                    locationManager.removeUpdates(GPSlistener);
                    locationManager.removeUpdates(Networklistener);
                }
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
             stopForeground(true);
         }else{
                          Intent intent = new Intent(getApplicationContext(), BackgroundLocationService.class);
             intent.setAction(AppConstants.ACTION_STOP_LOCATION_SERVICE);
             getApplicationContext().stopService(intent);

         }
         stopSelf();


    }

}
