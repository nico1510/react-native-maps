package com.airbnb.android.react.maps;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by nico on 21.07.17.
 */

public class CameraPositionUtils {


    public static CameraPosition tiltedLatLngPosition(LatLng coordinate, float zoomLevel, float tilt, float bearing) {
        return new CameraPosition(coordinate, zoomLevel, tilt, bearing);
    }

    public static CameraPosition tiltedRegionPosition(LatLngBounds bounds, float tilt, float bearing) {
        return new CameraPosition(bounds.getCenter(), getBoundsZoomLevel(bounds, SizeReportingShadowNode.dimensions.get("width"), SizeReportingShadowNode.dimensions.get("height")), tilt, bearing);
    }

    private static float getBoundsZoomLevel(LatLngBounds bounds, float mapWidthPx, float mapHeightPx) {

        final int WORLD_DP_HEIGHT = 256;
        final int WORLD_DP_WIDTH = 256;
        final int ZOOM_MAX = 21;
        LatLng ne = bounds.northeast;
        LatLng sw = bounds.southwest;

        double latFraction = (latRad(ne.latitude) - latRad(sw.latitude)) / Math.PI;

        double lngDiff = ne.longitude - sw.longitude;
        double lngFraction = ((lngDiff < 0) ? (lngDiff + 360) : lngDiff) / 360;

        float latZoom = zoom(mapHeightPx, WORLD_DP_HEIGHT, latFraction);
        float lngZoom = zoom(mapWidthPx, WORLD_DP_WIDTH, lngFraction);

        float result = Math.min(latZoom, lngZoom);
        return Math.min(result, ZOOM_MAX);
    }

    private static double latRad(double lat) {
        double sin = Math.sin(lat * Math.PI / 180);
        double radX2 = Math.log((1 + sin) / (1 - sin)) / 2;
        return Math.max(Math.min(radX2, Math.PI), -Math.PI) / 2;
    }

    private static float zoom(double mapPx, double worldPx, double fraction) {
        final double LN2 = 0.6931471805599453;
        return (float) (Math.log(mapPx / worldPx / fraction) / LN2);
    }

    private static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

}
