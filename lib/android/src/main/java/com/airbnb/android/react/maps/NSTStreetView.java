package com.airbnb.android.react.maps;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
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

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;


public class NSTStreetView extends StreetViewPanoramaView implements OnStreetViewPanoramaReadyCallback {

    private StreetViewPanorama panorama;
    private  NSTStreetViewManager manager;
    private final ThemedReactContext context;
    private LifecycleEventListener lifecycleListener;
    private boolean paused = false;
    private boolean destroyed = false;
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

    /*
        onDestroy is final method so I can't override it.
    */
    public synchronized void doDestroy() {
        if (destroyed) {
            return;
        }
        destroyed = true;

        if (lifecycleListener != null && context != null) {
            context.removeLifecycleEventListener(lifecycleListener);
            lifecycleListener = null;
        }
        if (!paused) {
            onPause();
            paused = true;
        }
        onDestroy();
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
        this.panorama = panorama;

        if (coordinate != null && bearing != null) {
            this.panorama.setPosition(coordinate);
            this.panorama.animateTo(StreetViewPanoramaCamera.builder().bearing(bearing.floatValue()).tilt(0).build(), 200);
        }

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

        final PublishSubject <Params> notifier = PublishSubject.create();
        this.panorama.setOnStreetViewPanoramaCameraChangeListener(new StreetViewPanorama.OnStreetViewPanoramaCameraChangeListener() {
            @Override
            public void onStreetViewPanoramaCameraChange(StreetViewPanoramaCamera camera) {
                if(camera == null || panorama.getLocation() == null || panorama.getLocation().position == null)  return;
                Params params = new Params(panorama.getLocation().position.latitude, panorama.getLocation().position.longitude, camera.bearing, panorama.getLocation().panoId != null);
                notifier.onNext(params);
            }
        });

        notifier.debounce(500, TimeUnit.MILLISECONDS).subscribe(new Consumer<Params>() {
            @Override
            public void accept(Params p) throws Exception {
                emitPositionChangeEvent(p.latitude, p.longitude, p.bearing, p.isStreetviewAvailable);
            }
        });

        lifecycleListener = new LifecycleEventListener() {
            @Override
            public void onHostResume() {
                synchronized (NSTStreetView.this) {
                    if (!destroyed) {
                        NSTStreetView.this.onResume();
                    }
                    paused = false;
                }
            }

            @Override
            public void onHostPause() {
                synchronized (NSTStreetView.this) {
                    if (!destroyed) {
                        NSTStreetView.this.onPause();
                    }
                    paused = true;
                }
            }

            @Override
            public void onHostDestroy() {
                NSTStreetView.this.doDestroy();
            }
        };

        context.addLifecycleEventListener(lifecycleListener);
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
            animateToBearing(bearing.floatValue(), 150);
        }
    }

    public void animateToBearing(float bearing, long duration) {
        if(this.panorama != null) {
            this.panorama.animateTo(StreetViewPanoramaCamera.builder().bearing(bearing).build(), duration);
        }
    }

    public static class Params {

        double latitude;
        double longitude;
        float bearing;
        boolean isStreetviewAvailable;

        public Params(double latitude, double longitude, float bearing, boolean isStreetviewAvailable) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.bearing = bearing;
            this.isStreetviewAvailable = isStreetviewAvailable;
        }
    }

}
