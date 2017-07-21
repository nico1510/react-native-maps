package com.airbnb.android.react.maps;

import android.util.DisplayMetrics;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by nico on 21.07.17.
 */

public class CameraPositionUtils {


    public static CameraPosition tiltedLatLngPosition(LatLng coordinate, float zoomLevel, int tilt, int bearing) {
        return new CameraPosition(coordinate, zoomLevel, tilt, bearing);
    }

    public static CameraPosition tiltedRegionPosition(LatLngBounds bounds, int tilt, int bearing, DisplayMetrics displayMetrics) {
        return new CameraPosition(bounds.getCenter(), getBoundsZoomLevel(bounds, SizeReportingShadowNode.dimensions.get("width"), SizeReportingShadowNode.dimensions.get("height"), displayMetrics), tilt, bearing);
    }

    private static int getBoundsZoomLevel(LatLngBounds bounds, float mapWidthPx, float mapHeightPx, DisplayMetrics displayMetrics) {

        final int WORLD_DP_HEIGHT = 256;
        final int WORLD_DP_WIDTH = 256;
        final int ZOOM_MAX = 21;
        LatLng ne = bounds.northeast;
        LatLng sw = bounds.southwest;

        double latFraction = (latRad(ne.latitude) - latRad(sw.latitude)) / Math.PI;

        double lngDiff = ne.longitude - sw.longitude;
        double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;

        double latZoom = zoom(mapHeightPx, WORLD_DP_HEIGHT * displayMetrics.scaledDensity, latFraction);
        double lngZoom = zoom(mapWidthPx, WORLD_DP_WIDTH * displayMetrics.scaledDensity, lngFraction);

        int result = Math.min((int) latZoom, (int) lngZoom);
        return Math.min(result, ZOOM_MAX);
    }

    private static double latRad(double lat) {
        double sin = Math.sin(lat * Math.PI / 180);
        double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
        return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
    }

    private static double zoom(double mapPx, double worldPx, double fraction) {
        final double LN2 = 0.6931471805599453;
        return Math.floor(Math.log(mapPx / worldPx / fraction) / LN2);
    }

}
