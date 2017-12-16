//
//  NSTStreetView.h
//  Pods
//
//  Created by nico on 09.12.17.
//

#import <React/RCTComponent.h>
#import <GoogleMaps/GoogleMaps.h>

@interface NSTStreetView: GMSPanoramaView

@property (nonatomic, copy) RCTBubblingEventBlock onPositionChange;

- (void)didChangePosition:(CLLocationDegrees) latitude andLongitude:(CLLocationDegrees)longitude andBearing:(CGFloat) bearing andIsStreetviewAvailable:(NSNumber*)isStreetviewAvailable;
- (void)updateMarker:(CLLocationDegrees) latitude andLongitude:(CLLocationDegrees)longitude;

@end
