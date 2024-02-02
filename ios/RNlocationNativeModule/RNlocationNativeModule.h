//
//  RNlocationNativeModule.h
//  RNlocationNativeModule
//
//  Created by VeeTee Technologies on 02/02/24.
//

//#import <Foundation/Foundation.h>

//@interface RNlocationNativeModule : NSObject

//@end

#import <React/RCTBridgeModule.h>
#import <React/RCTConvert.h>
#import <CoreLocation/CoreLocation.h>

@interface RNlocationNativeModule : NSObject <RCTBridgeModule, CLLocationManagerDelegate>

@end
