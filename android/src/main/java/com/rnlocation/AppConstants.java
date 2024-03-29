package com.rnlocation;

public class AppConstants {

  public static final String DIALOG_TITLE = "GPS Required";
  public static final String DIALOG_MESSAGE = "Kindly turn on GPS.  this is required Use this app.";

  public static final String POSITIVE_BUTTON = "Settings";
  public static final String NEGATIVE_BUTTON = "Cancel";

  public  static final String MODULE_NAME = "RNlocationNativeModule";


  public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;


  public static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

  public static final int DEFAULT_TIMOUT = 1000 * 60;

    public  static final int GPS_DEFAULT_TIMEOUT = 1000 * 8;


    static final int LOCATION_SERVICE_ID = 175;
    static  final String ACTION_START_LOCATION_SERVICE = "startLocationService";
    static  final String ACTION_STOP_LOCATION_SERVICE = "stopLocationService";

}
