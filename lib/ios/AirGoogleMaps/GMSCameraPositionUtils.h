//
//  GMSCameraPositionUtils.h
//  Pods
//
//  Created by nico on 22.07.17.
//
//

#import <Foundation/Foundation.h>
#import "RCTConvert+GMSMapViewType.h"

@interface GMSCameraPositionUtils : NSObject
+ (CLLocationCoordinate2D)getCenter:(GMSCoordinateBounds *)bounds;

+ (CGFloat)getBoundsZoomLevel:(GMSCoordinateBounds *)bounds
               withMapWidthPx:(CGFloat) mapWidthPx
              withMapHeightPx:(CGFloat) mapHeightPx;
@end
