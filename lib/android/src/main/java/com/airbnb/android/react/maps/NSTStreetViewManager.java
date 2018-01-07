//
//  NSTStreetViewManager.java
//  react-native-streetview
//
//  Created by Amit Palomo on 26/04/2017.
//  Copyright © 2017 Nester.co.il.
//

package com.airbnb.android.react.maps;

import android.view.View;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.Map;

import javax.annotation.Nullable;

public class NSTStreetViewManager extends ViewGroupManager<NSTStreetView> {

    public static final String REACT_CLASS = "NSTStreetView";
    private final ReactApplicationContext appContext;

    private static final int ANIMATE_TO_BEARING = 1;

    public NSTStreetViewManager(ReactApplicationContext context) {
        this.appContext = context;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected NSTStreetView createViewInstance(ThemedReactContext themedReactContext) {
        return new NSTStreetView(themedReactContext, appContext, this);
    }

    @ReactProp(name = "allGesturesEnabled", defaultBoolean = false)
    public void setAllGesturesEnabled(NSTStreetView view, boolean allGesturesEnabled) {
        view.setAllGesturesEnabled(allGesturesEnabled);
    }

    @ReactProp(name = "coordinate")
    public void setCoordinate(NSTStreetView view, ReadableMap coordinate) {
        view.setCoordinate(coordinate);
    }

    void pushEvent(ThemedReactContext context, View view, String name, WritableMap data) {
        context.getJSModule(RCTEventEmitter.class)
                .receiveEvent(view.getId(), name, data);
    }

    @Override
    @Nullable
    public Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of(
                "onPositionChange", MapBuilder.of("registrationName", "onPositionChange")
        );
    }

    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "animateToBearing", ANIMATE_TO_BEARING
        );
    }

    @Override
    public void receiveCommand(NSTStreetView view, int commandId, @Nullable ReadableArray args) {
        switch (commandId) {
            case ANIMATE_TO_BEARING:
                Double bearing = args.getDouble(0);
                Integer duration = args.getInt(1);
                view.animateToBearing(bearing.floatValue(), duration);
                break;
        }
    }

    @Override
    public void onDropViewInstance(NSTStreetView view) {
        view.doDestroy();
        super.onDropViewInstance(view);
    }
}
