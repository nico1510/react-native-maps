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
#import <GoogleMaps/GoogleMaps.h>
#import "NSTStreetViewManager.h"
#import "NSTStreetView.h"

@interface NSTStreetViewManager() <GMSPanoramaViewDelegate>

@end

@implementation NSTStreetViewManager

RCT_EXPORT_MODULE()

RCT_CUSTOM_VIEW_PROPERTY(coordinate, CLLocationCoordinate, GMSPanoramaView) {
    if (json == nil) return;

  [view moveNearCoordinate:[RCTConvert CLLocationCoordinate2D:json]];
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
  [streetView didChangePosition:coordinate.latitude andLongitude:coordinate.longitude andBearing:panoramaView.camera.orientation.heading andIsStreetviewAvailable:panorama != (id)[NSNull null]];
}

- (void) panoramaView:(GMSPanoramaView *)panoramaView error:(NSError *)error onMoveNearCoordinate:(CLLocationCoordinate2D)coordinate {
  NSTStreetView *streetView = (NSTStreetView *)panoramaView;
  [streetView didChangePosition:coordinate.latitude andLongitude:coordinate.longitude andBearing:panoramaView.camera.orientation.heading andIsStreetviewAvailable:NO];
}

@end
