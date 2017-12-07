package com.airbnb.android.react.maps;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;

import android.content.Context;
import android.support.annotation.NonNull;

public class NSTStreetView extends StreetViewPanoramaView implements OnStreetViewPanoramaReadyCallback {

    private StreetViewPanorama panorama;
    private  NSTStreetViewManager manager;
    private final ThemedReactContext context;
    private Boolean allGesturesEnabled = true;
    private LatLng coordinate = null;
    private Double bearing = null;

    private static boolean contextHasBug(Context context) {
        return context == null ||
                context.getResources() == null ||
                context.getResources().getConfiguration() == null;
    }

    private static Context getNonBuggyContext(ThemedReactContext reactContext,
                                              ReactApplicationContext appContext) {
        Context superContext = reactContext;
        if (!contextHasBug(appContext.getCurrentActivity())) {
            superContext = appContext.getCurrentActivity();
        } else if (contextHasBug(superContext)) {
            // we have the bug! let's try to find a better context to use
            if (!contextHasBug(reactContext.getCurrentActivity())) {
                superContext = reactContext.getCurrentActivity();
            } else if (!contextHasBug(reactContext.getApplicationContext())) {
                superContext = reactContext.getApplicationContext();
            } else {
                // ¯\_(ツ)_/¯
            }
        }
        return superContext;
    }

    public NSTStreetView(ThemedReactContext context, ReactApplicationContext appContext, NSTStreetViewManager manager) {
        super(getNonBuggyContext(context, appContext));
        this.manager = manager;
        this.context = context;
        super.onCreate(null);
        super.onResume();
        super.getStreetViewPanoramaAsync(this);
    }

    @Override
    public void onStreetViewPanoramaReady(final StreetViewPanorama panorama) {
        final NSTStreetView self = this;
        this.panorama = panorama;
        this.panorama.setPanningGesturesEnabled(allGesturesEnabled);
        this.panorama.setOnStreetViewPanoramaChangeListener(new StreetViewPanorama.OnStreetViewPanoramaChangeListener() {
            @Override
            public void onStreetViewPanoramaChange(StreetViewPanoramaLocation location) {
                if(location == null || panorama == null || panorama.getPanoramaCamera() == null) {
                    emitPositionChangeEvent(0, 0, 0, false);
                    return;
                }
                emitPositionChangeEvent(location.position.latitude, location.position.longitude, panorama.getPanoramaCamera().bearing, location.panoId != null);
            }
        });
        this.panorama.setOnStreetViewPanoramaCameraChangeListener(new StreetViewPanorama.OnStreetViewPanoramaCameraChangeListener() {
            @Override
            public void onStreetViewPanoramaCameraChange(StreetViewPanoramaCamera camera) {
                if(camera == null || panorama.getLocation() == null || panorama.getLocation().position == null)  return;
                emitPositionChangeEvent(panorama.getLocation().position.latitude, panorama.getLocation().position.longitude, camera.bearing, panorama.getLocation().panoId != null);
            }
        });
        if (coordinate != null && bearing != null) {
            this.panorama.setPosition(coordinate);
            this.panorama.animateTo(StreetViewPanoramaCamera.builder().bearing(bearing.floatValue()).tilt(0).build(), 200);
        }
    }

    @NonNull
    private void emitPositionChangeEvent(double latitude, double longitude, double bearing, boolean isStreetviewAvailable) {
        WritableMap position = new WritableNativeMap();
        position.putDouble("latitude", latitude);
        position.putDouble("longitude", longitude);
        position.putDouble("bearing", bearing);
        position.putBoolean("isStreetviewAvailable", isStreetviewAvailable);
        WritableMap event = Arguments.createMap();
        event.putMap("position", position);
        manager.pushEvent(context, this, "onPositionChange", event);
    }

    public void setAllGesturesEnabled(boolean allGesturesEnabled) {
        // Saving to local variable as panorama may not be ready yet (async)
        this.allGesturesEnabled = allGesturesEnabled;
    }

    public void setCoordinate(ReadableMap coordinate) {

        if (coordinate == null ) return;
        Double lng = coordinate.getDouble("longitude");
        Double lat = coordinate.getDouble("latitude");
        Double bearing = coordinate.getDouble("bearing");

        // Saving to local variable as panorama may not be ready yet (async)
        this.coordinate = new LatLng(lat, lng);
        this.bearing = bearing;

        if(this.panorama != null) {
            this.panorama.setPosition(this.coordinate, 100);
            this.panorama.animateTo(StreetViewPanoramaCamera.builder().bearing(bearing.floatValue()).build(), 150);
        }
    }

}
