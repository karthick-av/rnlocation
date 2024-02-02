//
//  RNlocationNativeModule.m
//  RNlocationNativeModule
//
//  Created by VeeTee Technologies on 02/02/24.
//

//#import "RNlocationNativeModule.h"

//@implementation RNlocationNativeModule

//@end


#import <Foundation/Foundation.h>
#import "RNlocationNativeModule.h"
#import <React/RCTLog.h>

@implementation RNlocationNativeModule: NSObject

RCT_EXPORT_MODULE(RNlocationNativeModule);

NSTimer* mTimer;
CLLocationManager* mLocationManager;
RCTPromiseResolveBlock mResolve;
RCTPromiseRejectBlock mReject;
double mTimeout;


RCT_EXPORT_METHOD(requestPermissionIfNeeded: promise: (RCTPromiseResolveBlock) resolve
                  rejecter: (RCTPromiseRejectBlock) reject) {
  
  
 // @try {
    CLLocationManager *locationManager = [[CLLocationManager alloc] init];
    locationManager.delegate = self;
    if ([CLLocationManager locationServicesEnabled]) {
      CLAuthorizationStatus status = [CLLocationManager authorizationStatus];
      if(status == kCLAuthorizationStatusAuthorizedWhenInUse || status == kCLAuthorizationStatusAuthorizedAlways){
        resolve(@YES);
       
      }else{
        [locationManager requestWhenInUseAuthorization];
        resolve(false);
        
      }
    }else{
      [locationManager requestWhenInUseAuthorization];
      resolve(false);
    }
    }

//else{
  //  }
  //}
  /*@catch (NSException *exception) {
    resolve(false);
        
      }*/
//}

RCT_EXPORT_METHOD(getCurrentPosition: (NSDictionary*) options
                  promise: (RCTPromiseResolveBlock) resolve
                  rejecter: (RCTPromiseRejectBlock) reject) {
    dispatch_async(dispatch_get_main_queue(), ^{
        @try {
            [self cancelPreviousRequest];
            
            if (@available(iOS 14.0, *)) {
                // should use locationManagerDidChangeAuthorization
            } else {
                if (![CLLocationManager locationServicesEnabled]) {
                    [[NSException
                      exceptionWithName:@"Unavailable"
                      reason:@"Location service is unavailable"
                      userInfo:nil]
                     raise];
                }
            }
            
            double timeout = [RCTConvert double:options[@"timeout"]];
            
            CLLocationManager *locationManager = [[CLLocationManager alloc] init];
            locationManager.delegate = self;
            locationManager.distanceFilter = kCLDistanceFilterNone;
            locationManager.desiredAccuracy = kCLLocationAccuracyBest;
            
            mResolve = resolve;
            mReject = reject;
            mLocationManager = locationManager;
            mTimeout = timeout;
          [locationManager startUpdatingLocation];
            //[self startUpdatingLocation];
        }
        @catch (NSException *exception) {
            NSMutableDictionary * info = [NSMutableDictionary dictionary];
            [info setValue:exception.name forKey:@"ExceptionName"];
            [info setValue:exception.reason forKey:@"ExceptionReason"];
            [info setValue:exception.callStackReturnAddresses forKey:@"ExceptionCallStackReturnAddresses"];
            [info setValue:exception.callStackSymbols forKey:@"ExceptionCallStackSymbols"];
            [info setValue:exception.userInfo forKey:@"ExceptionUserInfo"];
            
            NSError *error = [[NSError alloc] initWithDomain:@"Location not available." code:1 userInfo:info];
            reject(@"UNAVAILABLE", @"Location not ===> available", error);
        }
    });
}



- (void) locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray<CLLocation *> *)locations {
    if (locations.count > 0 && mResolve != nil) {
        CLLocation* location = locations[0];
        
        NSDictionary* locationResult = @{
            @"latitude": @(location.coordinate.latitude),
            @"longitude": @(location.coordinate.longitude),
            @"altitude": @(location.altitude),
            @"speed": @(location.speed),
            @"accuracy": @(location.horizontalAccuracy),
            @"time": @(location.timestamp.timeIntervalSince1970 * 1000),
            @"verticalAccuracy": @(location.verticalAccuracy),
            @"course": @(location.course),
        };
        
        mResolve(locationResult);
    }
    [self clearReferences];
}

- (void) locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error {
    if (mReject != nil) {
        mReject(@"UNAVAILABLE", @"Location not available", error);
    }
    [self clearReferences];
}

- (void) runTimeout:(id)sender {
    if (mReject != nil) {
        mReject(@"TIMEOUT", @"Location timed out", nil);
    }
    [self clearReferences];
}

/*
- (void)locationManager:(CLLocationManager *)manager didChangeAuthorizationStatus:(CLAuthorizationStatus)status {
    if ([self isAuthorized]) {
        [self startUpdatingLocation];
    } else if ([self isAuthorizationDenied]) {
        mReject(@"UNAUTHORIZED", @"Authorization denied", nil);
        [self clearReferences];
    }
}*/

- (void) clearReferences {
    if (mTimer != nil) {
        [mTimer invalidate];
    }
    if (mLocationManager != nil) {
        [mLocationManager stopUpdatingLocation];
    }
    mResolve = nil;
    mReject = nil;
    mLocationManager = nil;
    mTimer = nil;
    mTimeout = 0;
}

- (void) cancelPreviousRequest {
    if (mLocationManager != nil) {
        mReject(@"CANCELLED", @"Location cancelled by another request", nil);
    }
    [self clearReferences];
}

@end



// #import <React/RCTBridgeModule.h>

// @interface RCT_EXTERN_MODULE(RNlocation, NSObject)

// RCT_EXTERN_METHOD(multiply:(float)a withB:(float)b
//                  withResolver:(RCTPromiseResolveBlock)resolve
//                  withRejecter:(RCTPromiseRejectBlock)reject)

// + (BOOL)requiresMainQueueSetup
// {
//   return NO;
// }

// @end
