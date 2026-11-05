package com.jseed.core.starter.utils.maps.bean;


/**
 * @author cyrus
 * @date 2024/5/18
 */
public class LatLngBounds {
    /** Southwest corner coordinates */
    private LatLng southwest;
    /** Northeast corner coordinates */
    private LatLng northeast;

    public LatLngBounds(LatLng northeast, LatLng southwest) {
        this.southwest = southwest;
        this.northeast = northeast;
    }

    public LatLngBounds() {
    }


    public boolean contains(LatLng latLng) {
        if (latLng == null) {
            return false;
        } else if (this.northeast != null && this.southwest != null) {
            return this.checkLatitude(latLng.latitude) && this.chechkLongitude(latLng.longitude);
        } else {
            return false;
        }

    }

    private boolean checkLatitude(double latitude) {
        return this.southwest.latitude <= latitude && latitude <= this.northeast.latitude;
    }
    private boolean chechkLongitude(double longitude) {
        if (this.southwest.longitude <= this.northeast.longitude) {
            return this.southwest.longitude <= longitude && longitude <= this.northeast.longitude;
        } else {
            return this.southwest.longitude <= longitude || longitude <= this.northeast.longitude;
        }
    }
    public LatLng getSouthwest() {
        return southwest;
    }

    public void setSouthwest(LatLng southwest) {
        this.southwest = southwest;
    }

    public LatLng getNortheast() {
        return northeast;
    }

    public void setNortheast(LatLng northeast) {
        this.northeast = northeast;
    }

}
