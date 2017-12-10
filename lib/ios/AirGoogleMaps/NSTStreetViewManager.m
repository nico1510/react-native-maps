//
//  NSTStreetViewManager.m
//  react-native-streetview
//
//  Created by Amit Palomo on 26/04/2017.
//  Copyright © 2017 Nester.co.il.
//

#import <Foundation/Foundation.h>
#import <React/RCTViewManager.h>
#import <React/RCTConvert+CoreLocation.h>
#import <React/RCTBridge.h>
#import <React/RCTUIManager.h>
#import <React/RCTEventDispatcher.h>
#import <React/RCTConvert.h>
#import <React/UIView+React.h>
#import <GoogleMaps/GoogleMaps.h>
#import "NSTStreetViewManager.h"
#import "NSTStreetView.h"

@interface NSTStreetViewManager() <GMSPanoramaViewDelegate>

@end

@implementation NSTStreetViewManager

RCT_EXPORT_MODULE()

RCT_CUSTOM_VIEW_PROPERTY(coordinate, CLLocationCoordinate, GMSPanoramaView) {
  if (json == nil) return;
  [view moveNearCoordinate:[RCTConvert CLLocationCoordinate2D:json] radius:100];
  [view updateCamera:[GMSPanoramaCameraUpdate setHeading:[RCTConvert CGFloat:json[@"bearing"]]] animationDuration:0.5];
}

RCT_EXPORT_VIEW_PROPERTY(allGesturesEnabled, BOOL)

- (UIView *)view {
  NSTStreetView *panoView = [NSTStreetView new];
  panoView.delegate = self;
  return panoView;
}

RCT_EXPORT_VIEW_PROPERTY(onPositionChange, RCTBubblingEventBlock)

- (void)panoramaView:(GMSPanoramaView *)panoramaView didMoveToPanorama:(GMSPanorama *)panorama nearCoordinate:(CLLocationCoordinate2D)coordinate {
  NSTStreetView *streetView = (NSTStreetView *)panoramaView;
  [streetView updateMarker:coordinate.latitude andLongitude:coordinate.longitude];
  [streetView didChangePosition:coordinate.latitude andLongitude:coordinate.longitude andBearing:panoramaView.camera.orientation.heading andIsStreetviewAvailable:panorama != (id)[NSNull null]];
}

RCT_EXPORT_METHOD(animateToBearing:(nonnull NSNumber *)reactTag withBearing:(CGFloat)bearing withDuration:(CGFloat)duration)
{
  [self.bridge.uiManager addUIBlock:^(__unused RCTUIManager *uiManager, NSDictionary<NSNumber *, UIView *> *viewRegistry) {
    id view = viewRegistry[reactTag];
    if (![view isKindOfClass:[NSTStreetView class]]) {
      RCTLogError(@"Invalid view returned from registry, expecting NSTStreetView, got: %@", view);
    } else {
      NSTStreetView *streetView = (NSTStreetView *)view;
      [streetView updateCamera:[GMSPanoramaCameraUpdate setHeading:bearing] animationDuration:duration/1000];
    }
  }];
}

- (void) panoramaView:(GMSPanoramaView *)panoramaView didMoveCamera:(GMSPanoramaCamera *)camera {
  NSTStreetView *streetView = (NSTStreetView *)panoramaView;
  CLLocationCoordinate2D position = panoramaView.panorama.coordinate;
    BOOL isStreetviewAvailable = panoramaView.panorama != (id)[NSNull null];
  id params = @{
                @"streetView" : streetView,
                @"latitude" : [NSNumber numberWithDouble:position.latitude],
                @"longitude" : [NSNumber numberWithDouble:position.longitude],
                @"bearing" : [NSNumber numberWithDouble:camera.orientation.heading],
                @"isStreetviewAvailable" : [NSNumber numberWithBool:isStreetviewAvailable],
              };
    [NSObject cancelPreviousPerformRequestsWithTarget:self];
    [self performSelector:@selector(reportCameraMoveComplete:) withObject:params afterDelay:0.5];
}

- (void) reportCameraMoveComplete:(id)info
{
  NSTStreetView *streetView = info[@"streetView"];
  NSNumber *latitude = info[@"latitude"];
  NSNumber *longitude = info[@"longitude"];
  NSNumber *bearing = info[@"bearing"];
  BOOL isStreetviewAvailable = info[@"isStreetviewAvailable"];
  [streetView didChangePosition:[latitude doubleValue] andLongitude:[longitude doubleValue] andBearing:[bearing doubleValue] andIsStreetviewAvailable:isStreetviewAvailable];
}

- (void) panoramaView:(GMSPanoramaView *)panoramaView error:(NSError *)error onMoveNearCoordinate:(CLLocationCoordinate2D)coordinate {
  NSTStreetView *streetView = (NSTStreetView *)panoramaView;
  [streetView didChangePosition:coordinate.latitude andLongitude:coordinate.longitude andBearing:panoramaView.camera.orientation.heading andIsStreetviewAvailable:NO];
}

@end
