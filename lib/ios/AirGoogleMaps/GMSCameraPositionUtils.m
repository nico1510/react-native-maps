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
@end
