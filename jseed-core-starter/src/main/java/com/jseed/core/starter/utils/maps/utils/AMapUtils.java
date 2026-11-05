package com.jseed.core.starter.utils.maps.utils;

import com.jseed.core.starter.utils.maps.bean.LatLng;
import com.jseed.core.starter.utils.maps.bean.LatLngBounds;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.FastMath;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.PI;

/**
 * @author cyrus
 * @date 2024/5/18
 */
public class AMapUtils {

    /**
     * Converts a map bounds string to a SQL spatial range.
     *
     * @param latLngBounds
     * @return
     */
    public static String geomFromText(LatLngBounds latLngBounds) {
        String westSouth = latLngBounds.getSouthwest().longitude + " " + latLngBounds.getSouthwest().latitude;
        String westNorth = latLngBounds.getSouthwest().longitude + " " + latLngBounds.getNortheast().latitude;
        String eastNorth = latLngBounds.getNortheast().longitude + " " + latLngBounds.getNortheast().latitude;
        String eastSouth = latLngBounds.getNortheast().longitude + " " + latLngBounds.getSouthwest().latitude;
        // Southwest, northwest, northeast, southeast, southwest.
        String result = "Polygon((" + westSouth + "," + westNorth + "," + eastNorth + "," + eastSouth + "," + westSouth + "))";
        return result;
    }

    /**
     * Converts a map bounds string to a SQL spatial range.
     *
     * @param bounds
     * @return
     */
    public static String geomFromText(String bounds) {
        return geomFromText(boundsToLatLngBounds(bounds));
    }

    /**
     * Parses the map bounds string.
     *
     * @param bounds map.getBounds(),
     * @return
     */
    public static LatLngBounds boundsToLatLngBounds(String bounds) {
        LatLng northeast = new LatLng(Double.parseDouble(bounds.split(";")[0].split(",")[1]), Double.parseDouble(bounds.split(";")[0].split(",")[0]));
        LatLng southwest = new LatLng(Double.parseDouble(bounds.split(";")[1].split(",")[1]), Double.parseDouble(bounds.split(";")[1].split(",")[0]));
        // Bounds from the client map: map.getBounds().
        return new LatLngBounds(northeast, southwest);
    }

    /**
     * Computes the distance between two points.
     *
     * @param * @param null
     * @return
     */
    public static double calculateLineDistance(LatLng source, LatLng target) {
        if (source != null && target != null) {
            try {
                double sourceLongitude = source.longitude;
                double sourceLatitude = source.latitude;
                double targetLongitude = target.longitude;
                double targetLatitude = target.latitude;
                double a, b, R;
                //Earth radius
                R = 6378137;
                sourceLatitude = sourceLatitude * 3.141593 / 180.0;
                targetLatitude = targetLatitude * 3.141593 / 180.0;
                a = sourceLatitude - targetLatitude;
                b = (sourceLongitude - targetLongitude) * 3.141593 / 180.0;
                double d;
                double sa2, sb2;
                sa2 = Math.sin(a / 2.0);
                sb2 = Math.sin(b / 2.0);
                d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(sourceLatitude) * Math.cos(targetLatitude) * sb2 * sb2));
                return BigDecimal.valueOf(d).setScale(4, BigDecimal.ROUND_DOWN).doubleValue();
            } catch (Throwable var30) {
                var30.printStackTrace();
                return 0.0;
            }
        } else {
            try {
                throw new RuntimeException("非法坐标值");
            } catch (Exception e) {
                e.printStackTrace();
                return 0.0;
            }
        }
    }

    public static double calculateLineDistance(double lon1,double lat1, double lon2 ,double lat2) {
        return BigDecimal.valueOf(calculateLineDistance(new LatLng(lat1,lon1), new LatLng(lat2,lon2))).setScale(0, RoundingMode.HALF_UP).doubleValue();
    }
    /**
     * Computes the northeast and southwest corners of a grid cell from its center and distance.
     *
     * @param source
     * @param raidus distance in meters
     * @return
     */
    public static LatLngBounds getAround(LatLng source, Double raidus) {
        Map map = new HashMap();
        Double latitude = source.latitude;
        Double longitude = source.longitude;

        Double degree = (24901 * 1609) / 360.0;
        double raidusMile = raidus;

        Double dpmLat = 1 / degree;
        Double radiusLat = dpmLat * raidusMile;
        Double minLat = latitude - radiusLat;
        Double maxLat = latitude + radiusLat;

        Double mpdLng = degree * Math.cos(latitude * (PI / 180));
        Double dpmLng = 1 / mpdLng;
        Double radiusLng = dpmLng * raidusMile;
        Double minLng = longitude - radiusLng;
        Double maxLng = longitude + radiusLng;
        LatLng northeast = new LatLng(maxLat, maxLng);
        LatLng southwest = new LatLng(minLat, minLng);
        LatLngBounds latLngBounds = new LatLngBounds(northeast, southwest);
        return latLngBounds;
    }

    /**
     * Computes the Haversine distance between two points in meters.
     * @return
     */
    public static double haversine(double startLat, double startLng, double endLat, double endLng) {
        final double earthRadius = 6378137; // in meters
        double dLat = toRadians(endLat - startLat);
        double dLng = toRadians(endLng - startLng);
        double a = FastMath.sin(dLat / 2) * FastMath.sin(dLat / 2) +
                FastMath.cos(Math.toRadians(startLat)) * FastMath.cos(Math.toRadians(endLat)) *
                        FastMath.sin(dLng / 2) * FastMath.sin(dLng / 2);
        double c = 2 * FastMath.atan2(FastMath.sqrt(a), FastMath.sqrt(1 - a));
        return earthRadius * c;
    }

    public static double toRadians(double angdeg) {
        return angdeg / 180.0 * 3.141593;
    }
    /**
     * Computes the Haversine distance between two points in meters.
     *
     * @param latAndLon1
     * @param latAndLon2
     * @return
     */
    public static Long calculateLineDistance(String latAndLon1, String latAndLon2) {
        if (StringUtils.isBlank(latAndLon1) || StringUtils.isBlank(latAndLon2)) {
            throw new RuntimeException("非法坐标值");
        }
        final String[] split = latAndLon1.split(",");
        final String[] split1 = latAndLon2.split(",");
        if (split.length != 2 || split1.length != 2) {
            throw new RuntimeException("非法坐标值");
        }
        LatLng start = new LatLng(Double.parseDouble(split[1]), Double.parseDouble(split[0]));
        LatLng end = new LatLng(Double.parseDouble(split1[1]),  Double.parseDouble(split1[0]));
        return BigDecimal.valueOf(calculateLineDistance(start, end)).setScale(0, BigDecimal.ROUND_HALF_UP).longValue();
    }

    public static void main(String[] args) {

    }
}
