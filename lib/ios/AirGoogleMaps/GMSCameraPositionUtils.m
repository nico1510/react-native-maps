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
- (CLLocationCoordinate2D)getCenter:(GMSCoordinateBounds *)bounds {

    CGFloat neLatitude = bounds.northEast.latitude * M_PI / 180;
    CGFloat swLatitude = bounds.southWest.latitude * M_PI / 180;

    CGFloat neLongitude = bounds.northEast.longitude * M_PI / 180;
    CGFloat swLongitude = bounds.southWest.longitude * M_PI / 180;


    CGFloat x = (cos(neLatitude) * cos(neLongitude)) + (cos(swLatitude) * cos(swLongitude)) / 2;
    CGFloat y = (cos(neLatitude) * sin(neLongitude)) + (cos(swLatitude) * sin(swLongitude)) / 2;
    CGFloat z = (sin(neLatitude) + sin(swLatitude)) / 2;

    CGFloat centralSquareRoot = sqrt(x * x + y * y);
    CGFloat centralLatitude = atan2(z, centralSquareRoot) * 180 / M_PI;
    CGFloat centralLongitude = atan2(y, x) * 180 / M_PI;

    return CLLocationCoordinate2DMake(centralLatitude, centralLongitude);

}
@end
