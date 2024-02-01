// #import <React/RCTBridgeModule.h>
// #import <React/RCTViewManager.h>

#import <React/RCTBridgeModule.h>
#import <React/RCTConvert.h>
#import <CoreLocation/CoreLocation.h>


@interface LocationModule : NSObject <RCTBridgeModule, CLLocationManagerDelegate>

@end