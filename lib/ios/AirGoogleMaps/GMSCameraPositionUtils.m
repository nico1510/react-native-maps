//
//  GMSCameraPositionUtils.m
//  Pods
//
//  Created by nico on 22.07.17.
//
//

#import <Foundation/Foundation.h>
#import "GMSCameraPositionUtils.h"
#import "RCTConvert+GMSMapViewType.h"


@implementation GMSCameraPositionUtils

// https://stackoverflow.com/a/14231286
+ (CLLocationCoordinate2D)getCenter:(GMSCoordinateBounds *)bounds {

    if(bounds.northEast.longitude >= bounds.southWest.longitude) {
        //Standard case
        return CLLocationCoordinate2DMake((bounds.southWest.latitude + bounds.northEast.latitude) / 2,
                                          (bounds.southWest.longitude + bounds.northEast.longitude) / 2);
    } else {
        //Region spans the international dateline
        return CLLocationCoordinate2DMake((bounds.southWest.latitude + bounds.northEast.latitude) / 2,
                                          (bounds.southWest.longitude + bounds.northEast.longitude + 360) / 2);
    }
}


+ (CGFloat)getBoundsZoomLevel:(GMSCoordinateBounds *)bounds
               withMapWidthPx:(CGFloat) mapWidthPx
              withMapHeightPx:(CGFloat) mapHeightPx {

    CGFloat WORLD_DP_HEIGHT = 256;
    CGFloat WORLD_DP_WIDTH = 256;
    CGFloat ZOOM_MAX = 21;
    CGFloat paddingVertical = 20;
    CGFloat paddingHorizontal = 20;
    CLLocationCoordinate2D ne = bounds.northEast;
    CLLocationCoordinate2D sw = bounds.southWest;

    double latFraction = ([[self class] latRad: ne.latitude] - [[self class] latRad: sw.latitude]) / M_PI;

    double lngDiff = ne.longitude - sw.longitude;
    double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;

    float latZoom = [[self class] zoom:(mapHeightPx - paddingVertical) withWorldPx:WORLD_DP_HEIGHT withFraction:latFraction];
    float lngZoom = [[self class] zoom:(mapWidthPx - paddingHorizontal) withWorldPx:WORLD_DP_WIDTH withFraction:lngFraction];

    float result = MIN(latZoom, lngZoom);
    return MIN(result, ZOOM_MAX);
}

+ (CGFloat) latRad:(CGFloat) lat {
    CGFloat sin = sin(lat * M_PI / 180);
    CGFloat radX2 = log((1 + sin) / (1 - sin)) / 2;
    return MAX(MIN(radX2, M_PI), -M_PI) / 2;
}

+ (CGFloat) zoom:(CGFloat) mapPx
     withWorldPx:(CGFloat) worldPx
    withFraction:(CGFloat) fraction {
    CGFloat LN2 = 0.6931471805599453;
    return log(mapPx / worldPx / fraction) / LN2;
}


@end
