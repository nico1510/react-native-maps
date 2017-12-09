//
//  NSTStreetView.m
//  react-native-google-maps
//
//  Created by nico on 09.12.17.
//

#import <Foundation/Foundation.h>
#import "NSTStreetView.h"

@implementation NSTStreetView

#pragma mark GMSPanoramaViewDelegate

- (void)didChangePosition:(CLLocationDegrees) latitude andLongitude:(CLLocationDegrees)longitude andBearing:(CGFloat) bearing andIsStreetviewAvailable:(BOOL)isStreetviewAvailable
{
  if (!self.onPositionChange) {
    return;
  }
  self.onPositionChange(@{
                           @"position": @{
                               @"latitude": @(latitude),
                               @"longitude": @(longitude),
                               @"bearing": @(bearing),
                               @"isStreetviewAvailable": @(isStreetviewAvailable),
                               }
                           });
}

@end
