package com.jseed.core.starter.utils.maps.bean;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author cyrus
 * @date 2024/5/13
 */
public class LatLng implements Serializable {

    private static final long serialVersionUID = 4819282815800659205L;
    /** Latitude */
    public double latitude;
    /** Longitude */
    public double longitude;


    public LatLng(double latitude, double longitude) {
        this(latitude, longitude, true);
    }

    public LatLng(double latitude, double longitude, boolean isCheck) {
        if (isCheck) {
            double longitudeBottom = -180.0D;
            double longitudeTop = 180.0D;
            double latitudeBottom = -90.0D;
            double latitudeTop = 90.0D;
            if (longitudeBottom <= longitude && longitude < longitudeTop) {
                this.longitude = longitude;
            } else {
                this.longitude = ((longitude - 180.0D) % 360.0D + 360.0D) % 360.0D - 180.0D;
            }

            if (latitude < latitudeBottom || latitude > latitudeTop) {
                try {
                    System.out.println(latitude+",,,"+longitude);
                    throw new RuntimeException("非法坐标值");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            this.latitude = Math.max(latitudeBottom, Math.min(latitudeTop, latitude));
        } else {
            this.latitude = latitude;
            this.longitude = longitude;
        }

    }
    @Override
    public LatLng clone() {
        return new LatLng(this.latitude, this.longitude);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LatLng latLng = (LatLng) o;
        return Double.compare(latLng.latitude, latitude) == 0 &&
                Double.compare(latLng.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
