//
//  NSTStreetView.h
//  Pods
//
//  Created by nico on 09.12.17.
//

#import <React/RCTComponent.h>
#import <GoogleMaps/GoogleMaps.h>

@interface RNTMapView: GMSPanoramaView

@property (nonatomic, copy) RCTBubblingEventBlock onPositionChange;

@end
