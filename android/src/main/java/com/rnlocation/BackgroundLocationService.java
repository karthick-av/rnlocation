//package com.rnlocation;
// import android.Manifest;
// import android.annotation.SuppressLint;
// import android.app.NotificationChannel;
// import android.app.NotificationManager;
// import android.app.PendingIntent;
// import android.app.Service;
// import android.content.Context;
// import android.content.Intent;
// import android.content.pm.PackageManager;
// import android.location.Location;
// import android.location.LocationListener;
// import android.location.LocationManager;
// import android.os.Build;
// import android.os.Bundle;
// import android.os.IBinder;
// import android.util.Log;
// import android.content.res.Resources;

// import androidx.annotation.Nullable;
// import androidx.core.app.ActivityCompat;
// import androidx.core.app.NotificationCompat;

// public class BackgroundLocationService extends Service {
//     private static final int TWO_MINUTES = 1000 * 60 * 1;
//     public LocationManager locationManager;
//     public MyLocationListener listener;
//     public Location previousBestLocation = null;

//     Context context;

//     Intent intent;
//     int counter = 0;

//     @Nullable
//     @Override
//     public IBinder onBind(Intent intent) {
//         return null;
//     }

//     @Override
//     public void onCreate() {
//         super.onCreate();
//         // intent = new Intent(BROADCAST_ACTION);
//         context = this;
//     }

//     @Override
//     public void onDestroy() {
//         // handler.removeCallbacks(sendUpdatesToUI);
//         super.onDestroy();
//         Log.d("STOP_SERVICE ==>", "DONE");
//         locationManager.removeUpdates(listener);
//         stopForeground(true);

//     }

//     @SuppressLint("MissingPermission")
//     @Override
//     public int onStartCommand(Intent intent, int flags, int startId) {
//         Log.d("onStartCommand", "onStartCommand: ");
//         try {
//             Log.d("onStartCommand == > 1", "onStartCommand:  ==> 1");

//             locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//             Log.d("onStartCommand == > 2", "onStartCommand:  ==> 2");
//             listener = new MyLocationListener();
//             Log.d("onStartCommand == > 3", "onStartCommand:  ==> 3");

//             locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
//             locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);





//         } catch (Exception ex) {
//             Log.d("onStartCommand == > ERR", "onStartCommand:  ==> ERR");

//             Log.d("Error", String.valueOf(ex));

//             ex.printStackTrace();
//         }
//         if (intent != null) {
//             String action = intent.getAction();
//             if (action != null) {
//                 if (action.equals(AppConstants.ACTION_START_LOCATION_SERVICE)) {
//                     startLocationService();
//                 } else if (action.equals(AppConstants.ACTION_STOP_LOCATION_SERVICE)) {
//                     stopLocationService();
//                 }
//             }
//         }
//         return super.onStartCommand(intent, flags, startId);
//     }

//     /** Checks whether two providers are the same */
//     private boolean isSameProvider(String provider1, String provider2) {
//         if (provider1 == null) {
//             return provider2 == null;
//         }
//         return provider1.equals(provider2);
//     }

//     public Class getMainActivityClass() {
//         String packageName = context.getPackageName();
//         Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
//         String className = launchIntent.getComponent().getClassName();
//         try {
//             return Class.forName(className);
//         } catch (ClassNotFoundException e) {
//             e.printStackTrace();
//             return null;
//         }
//     }
//     private void startLocationService() {
//         Class intentClass = getMainActivityClass();
//         String channelId = "background_notification_channel";
//         String TITLE = "Location tracking";
//         String BODY = "This app keeps track of your location in the background.";
//         Resources res = context.getResources();
//         String packageName = context.getPackageName();

//         NotificationManager notificationManager =
//                 (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//         Intent resultIntent = new Intent(context, intentClass);
//         resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

//         PendingIntent pendingIntent = PendingIntent.getActivity(
//                 context,
//                 0,
//                 resultIntent,
//                 PendingIntent.FLAG_IMMUTABLE
//         );
//         Intent actionIntent = new Intent(context, NotificationActionReceiver.class);
//         actionIntent.setAction("STOP_TRACKING"); // Customize with your action name
//         PendingIntent actionPendingIntent =
//                 PendingIntent.getBroadcast(context, 0, actionIntent, PendingIntent.FLAG_IMMUTABLE);

//         int AppIcon = res.getIdentifier("ic_launcher", "mipmap", packageName);
//         NotificationCompat.Builder builder = new NotificationCompat.Builder(
//                 context,
//                 channelId
//         );
//         builder.setSmallIcon(AppIcon);
//         builder.setContentTitle(TITLE);
//         builder.setDefaults(NotificationCompat.DEFAULT_ALL);
//         builder.setContentText(BODY);
//         builder.setContentIntent(pendingIntent);
//         builder.addAction(AppIcon, "Stop tracking", actionPendingIntent);

//         builder.setAutoCancel(false);
//         builder.setPriority(NotificationCompat.PRIORITY_MAX);

//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//             if (notificationManager != null
//                     && notificationManager.getNotificationChannel(channelId) == null) {

//                 NotificationChannel notificationChannel = new NotificationChannel(
//                         channelId,
//                         "Location Service",
//                         NotificationManager.IMPORTANCE_HIGH
//                 );

//                 notificationChannel.setDescription("location service");
//                 notificationManager.createNotificationChannel(notificationChannel);
//             }
//         }
// //        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
// //            // TODO: Consider calling
// //            //    ActivityCompat#requestPermissions
// //            // here to request the missing permissions, and then overriding
// //            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
// //            //                                          int[] grantResults)
// //            // to handle the case where the user grants the permission. See the documentation
// //            // for ActivityCompat#requestPermissions for more details.
// //            return;
// //        }

// //
// //        locationManager.requestLocationUpdates(
// //                LocationManager.GPS_PROVIDER,
// //                5000, 10, GPSlistener
// //        );
// //        locationManager.requestLocationUpdates(
// //                LocationManager.NETWORK_PROVIDER,
// //                5000, 10, Networklistener
// //        );
//         //      startForeground(AppConstants.LOCATION_SERVICE_ID, builder.build());

//         startForeground(AppConstants.LOCATION_SERVICE_ID, builder.build());

//     }


//     private  void stopLocationService(){
//         if(locationManager != null){
//             Log.d("STOP", "stopLocationService: ");

//             Log.v("STOP_SERVICE", "DONE");
//             locationManager.removeUpdates(listener);
//         }
//         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
//             stopForeground(true);
//             Intent intent = new Intent(context, BackgroundLocationService.class);
//             intent.setAction(AppConstants.ACTION_STOP_LOCATION_SERVICE);
//             context.stopService(intent);
//         }else{
//             Intent intent = new Intent(context, BackgroundLocationService.class);
//             intent.setAction(AppConstants.ACTION_STOP_LOCATION_SERVICE);
//             context.stopService(intent);

//         }
//         stopSelf();


//     }

//     public class MyLocationListener implements LocationListener{

//         public void onLocationChanged(final Location loc)
//         {
//             Log.d("**********", "Location changed  " + loc.getLatitude() + "  " + loc.getLongitude());

//         }

//         public void onProviderDisabled(String provider)
//         {
//         }


//         public void onProviderEnabled(String provider)
//         {
//         }


//         public void onStatusChanged(String provider, int status, Bundle extras)
//         {

//         }

//     }
// }

//package com.location;
//
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.IBinder;
//import android.util.Log;
//import androidx.annotation.Nullable;
//import androidx.core.app.ActivityCompat;
//import androidx.core.app.NotificationCompat;
//
//public class BackgroundLocationService extends Service {
//    private static final int TWO_MINUTES = 1000 * 60 * 1;
//    public LocationManager locationManager;
//    public MyLocationListener listener;
//    public Location previousBestLocation = null;
//
//    Context context;
//
//    Intent intent;
//    int counter = 0;
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        // intent = new Intent(BROADCAST_ACTION);
//        context = this;
//    }
//
//    @Override
//    public void onDestroy() {
//        // handler.removeCallbacks(sendUpdatesToUI);
//        super.onDestroy();
//        Log.v("STOP_SERVICE ==>", "DONE");
//        locationManager.removeUpdates(listener);
//        stopForeground(true);
//
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        try {
//            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            listener = new MyLocationListener();
//            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
//
//            }
//
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        if (intent != null) {
//            String action = intent.getAction();
//            if (action != null) {
//                if (action.equals(AppConstants.ACTION_START_LOCATION_SERVICE)) {
//                    startLocationService();
//                } else if (action.equals(AppConstants.ACTION_STOP_LOCATION_SERVICE)) {
//                    stopLocationService();
//                }
//            }
//        }
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    /** Checks whether two providers are the same */
//    private boolean isSameProvider(String provider1, String provider2) {
//        if (provider1 == null) {
//            return provider2 == null;
//        }
//        return provider1.equals(provider2);
//    }
//
//    public Class getMainActivityClass() {
//        String packageName = context.getPackageName();
//        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
//        String className = launchIntent.getComponent().getClassName();
//        try {
//            return Class.forName(className);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//    private void startLocationService() {
//        Class intentClass = getMainActivityClass();
//        String channelId = "background_notification_channel";
//        String TITLE = "Location tracking";
//        String BODY = "This app keeps track of your location in the background.";
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        Intent resultIntent = new Intent(context, intentClass);
//        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(
//                context,
//                0,
//                resultIntent,
//                PendingIntent.FLAG_IMMUTABLE
//        );
//        Intent actionIntent = new Intent(context, NotificationActionReceiver.class);
//        actionIntent.setAction("STOP_TRACKING"); // Customize with your action name
//        PendingIntent actionPendingIntent =
//                PendingIntent.getBroadcast(context, 0, actionIntent, PendingIntent.FLAG_IMMUTABLE);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(
//                context,
//                channelId
//        );
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setContentTitle(TITLE);
//        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
//        builder.setContentText(BODY);
//        builder.setContentIntent(pendingIntent);
//        builder.addAction(R.mipmap.ic_launcher, "Stop tracking", actionPendingIntent);
//
//        builder.setAutoCancel(false);
//        builder.setPriority(NotificationCompat.PRIORITY_MAX);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            if (notificationManager != null
//                    && notificationManager.getNotificationChannel(channelId) == null) {
//
//                NotificationChannel notificationChannel = new NotificationChannel(
//                        channelId,
//                        "Location Service",
//                        NotificationManager.IMPORTANCE_HIGH
//                );
//
//                notificationChannel.setDescription("location service");
//                notificationManager.createNotificationChannel(notificationChannel);
//            }
//        }
////        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////            // TODO: Consider calling
////            //    ActivityCompat#requestPermissions
////            // here to request the missing permissions, and then overriding
////            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
////            //                                          int[] grantResults)
////            // to handle the case where the user grants the permission. See the documentation
////            // for ActivityCompat#requestPermissions for more details.
////            return;
////        }
//
////
////        locationManager.requestLocationUpdates(
////                LocationManager.GPS_PROVIDER,
////                5000, 10, GPSlistener
////        );
////        locationManager.requestLocationUpdates(
////                LocationManager.NETWORK_PROVIDER,
////                5000, 10, Networklistener
////        );
//  //      startForeground(AppConstants.LOCATION_SERVICE_ID, builder.build());
//
//        startForeground(AppConstants.LOCATION_SERVICE_ID, builder.build());
//
//    }
//
//
//    private  void stopLocationService(){
//        if(locationManager != null){
//            Log.d("STOP", "stopLocationService: ");
//
//            Log.v("STOP_SERVICE", "DONE");
//            locationManager.removeUpdates(listener);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
//            stopForeground(true);
//            Intent intent = new Intent(context, BackgroundLocationService.class);
//            intent.setAction(AppConstants.ACTION_STOP_LOCATION_SERVICE);
//            context.stopService(intent);
//        }else{
//            Intent intent = new Intent(context, BackgroundLocationService.class);
//            intent.setAction(AppConstants.ACTION_STOP_LOCATION_SERVICE);
//            context.stopService(intent);
//
//        }
//        stopSelf();
//
//
//    }
//
//    public class MyLocationListener implements LocationListener{
//
//        public void onLocationChanged(final Location loc)
//        {
//            Log.i("**********", "Location changed  " + loc.getLatitude() + "  " + loc.getLongitude());
//
//        }
//
//        public void onProviderDisabled(String provider)
//        {
//        }
//
//
//        public void onProviderEnabled(String provider)
//        {
//        }
//
//
//        public void onStatusChanged(String provider, int status, Bundle extras)
//        {
//
//        }
//
//    }
//}
