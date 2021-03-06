package com.airbnb.android.react.maps;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class RegionChangeEvent extends Event<RegionChangeEvent> {
  private final LatLngBounds bounds;
  private  final double bearing;
  private final boolean continuous;

  public RegionChangeEvent(int id, LatLngBounds bounds, double bearing, boolean continuous) {
    super(id);
    this.bounds = bounds;
    this.bearing = bearing;
    this.continuous = continuous;
  }

  @Override
  public String getEventName() {
    return "topChange";
  }

  @Override
  public boolean canCoalesce() {
    return false;
  }

  @Override
  public void dispatch(RCTEventEmitter rctEventEmitter) {

    WritableMap event = new WritableNativeMap();
    event.putBoolean("continuous", continuous);

    WritableMap region = new WritableNativeMap();
    LatLng center = bounds.getCenter();
    Double longitudeDelta = bounds.northeast.longitude - bounds.southwest.longitude;
    region.putDouble("latitude", center.latitude);
    region.putDouble("longitude", center.longitude);
    region.putDouble("latitudeDelta", bounds.northeast.latitude - bounds.southwest.latitude);
    region.putDouble("longitudeDelta", longitudeDelta < 0 ? longitudeDelta + 360 : longitudeDelta);
    region.putDouble("bearing", bearing);
    event.putMap("region", region);

    rctEventEmitter.receiveEvent(getViewTag(), getEventName(), event);
  }
}
