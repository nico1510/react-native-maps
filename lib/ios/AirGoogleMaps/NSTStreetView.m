//
//  NSTStreetView.m
//  react-native-google-maps
//
//  Created by nico on 09.12.17.
//

#import <Foundation/Foundation.h>
#import "NSTStreetView.h"

@implementation NSTStreetView

GMSMarker *marker;

#pragma mark GMSPanoramaViewDelegate

- (void)didChangePosition:(CLLocationDegrees) latitude andLongitude:(CLLocationDegrees)longitude andBearing:(CGFloat) bearing andIsStreetviewAvailable:(NSNumber*)isStreetviewAvailable
{
    if (!self.onPositionChange) {
        return;
    }
    id event = (isStreetviewAvailable != nil) ? @{
                                                  @"position": @{
                                                          @"latitude": @(latitude),
                                                          @"longitude": @(longitude),
                                                          @"bearing": @(bearing),
                                                          @"isStreetviewAvailable": @([isStreetviewAvailable boolValue]),
                                                          }
                                                  } : @{
                                                        @"position": @{
                                                                @"latitude": @(latitude),
                                                                @"longitude": @(longitude),
                                                                @"bearing": @(bearing),
                                                                }
                                                        };
    self.onPositionChange(event);
}

- (void)updateMarker:(CLLocationDegrees) latitude andLongitude:(CLLocationDegrees)longitude
{
    if(marker != nil) {
        marker.panoramaView = nil;
    }
    CLLocationCoordinate2D position = {latitude, longitude};
    marker = [GMSMarker markerWithPosition:position];
    marker.icon = [GMSMarker markerImageWithColor:[UIColor redColor]];
    marker.panoramaView = self;
}

@end
