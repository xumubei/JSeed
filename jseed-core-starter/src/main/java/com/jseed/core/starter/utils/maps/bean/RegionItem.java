package com.jseed.core.starter.utils.maps.bean;


import java.util.Map;

/**
 * @author cyrus
 * @date 2024/5/18
 */
public class RegionItem implements ClusterItem {
    private LatLng mLatLng;
    /** Map data */
    private Map<String, Object> mapData;

    @Override
    public LatLng getPosition() {
        return mLatLng;
    }

    public RegionItem() {
    }

    public RegionItem(LatLng mLatLng, Map<String, Object> mapData) {
        this.mLatLng = mLatLng;
        this.mapData = mapData;
    }

    public LatLng getmLatLng() {
        return mLatLng;
    }

    public void setmLatLng(LatLng mLatLng) {
        this.mLatLng = mLatLng;
    }

    public Map<String, Object> getMapData() {
        return mapData;
    }

    public void setMapData(Map<String, Object> mapData) {
        this.mapData = mapData;
    }
}
