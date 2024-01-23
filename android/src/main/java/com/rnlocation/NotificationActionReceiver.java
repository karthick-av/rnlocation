package com.rnlocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationActionReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    if ("STOP_TRACKING".equals(intent.getAction())) {
      Intent stopintent = new Intent(context, BackgroundLocationService.class);
      stopintent.setAction(AppConstants.ACTION_STOP_LOCATION_SERVICE);
      context.stopService(stopintent);
      Toast.makeText(context,"Stop Location Service",Toast.LENGTH_SHORT).show();

    }
  }
}
