# rnlocation

Geolocation service for Android using native modules

## Installation

```sh
npm install https://github.com/karthick-av/rnlocation.git
```

```sh
yarn add https://github.com/karthick-av/rnlocation.git
```

## Android post install

For Android you need to define the location permissions on `AndroidManifest.xml`.

```xml
<!-- Define ACCESS_FINE_LOCATION if you will use enableHighAccuracy=true  -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

<!-- Define ACCESS_COARSE_LOCATION if you will use enableHighAccuracy=false  -->
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>

 <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<!-- 
Apps that target Android 9 (API level 28) or higher and use foreground services need to request the FOREGROUND_SERVICE in the app manifest, as shown in the following code snippet. This is a normal permission, so the system automatically grants it to the requesting app. -->
 <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />

<!-- Allows an application to receive the Intent.ACTION_BOOT_COMPLETED that is broadcast after the system finishes booting -->
 <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```

##### android/app/src/main/AndroidManifest.
** Make sure to add an after ```</activity>```
```xml
  <service
           android:name="com.rnlocation.BackgroundLocationService"
             android:enabled="true"
            android:exported="false"
            />

 <receiver
            android:name="com.rnlocation.NotificationActionReceiver"
            android:enabled="true"
            android:exported="false" />
```


## API

### function `RNlocation.getCurrentPostion(LocationConfig)`

**Parameters:**
   - [`LocationConfig`](#object-locationconfig): Configuration object to determine how to get the user current location.

**Return:**
   - `Promise<`[`Location`](#object-location)`>`: Promise thats resolve to a Location object.

### Object `LocationConfig`

**Properties:**
   - `timeout`: The max time (in milliseconds) that you want to wait to receive a location. Default: `60000` (60 seconds)
  - `gps_timeout`: The max time (in milliseconds) that you want to wait to receive a GPS location. Default: `80000` (8 seconds)

 
     
     ###### Note: The timeout must be greater than the GPS timeout.

### Object `Location`

**Properties:**
   - `latitude`: The latitude, in degrees.
   - `longitude`: The longitude, in degrees.
   - `altitude`: The altitude if available, in meters above the WGS 84 reference ellipsoid.
   - `accuracy`: The estimated horizontal accuracy of this location, radial, in meters.
   - `speed`: The speed if it is available, in meters/second over ground.
   - `time`: The UTC time of this fix, in milliseconds since January 1, 1970.
   - `bearing`: *(Android only)* The bearing, in degrees.
   - `provider`: *(Android only)* The name of the provider that generated this fix.


## Usage

```js
import RNLocation from 'rnlocation';

// ...

   RNlocation.getCurrentPostion({timeout: 30000, gps_timeout: 7000}).then((res) => {
    
     })
```

### GPS

This method determines whether the GPS is enabled or not.(return boolean value)


```js

await RNLocation.isGPSenabled();
```
This method determines whether to prompt a GPS alert if needed.




```js

RNLocation.promptGPSIfNeeded();
```


### Permission


This method determines whether to request permission if needed. (return boolean value)


```js

await RNLocation.requestPermissionIfNeeded();
```



### Background Location Service

##### This service fetch your current location and send it to the server when the app is closed or not.

This method determines whether the service is running or not.(return boolean value)

```js

await RNLocation.isServiceRunning();
```

This method is used to start the background location service.


```js

 RNLocation.startBackgroundService();
```



This method is used to stop the background location service.


```js

 RNLocation.StopBackgroundService();
```
## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
