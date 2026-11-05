package com.jseed.core.starter.utils.maps.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cyrus
 * @date 2024/5/18
 */
public class Cluster implements Serializable {

    private static final long serialVersionUID = -5395248007484149223L;

    private Long id;

    /** Cluster center */
    private LatLng mLatLng;
    /** Cluster members */
    private List<ClusterItem> mClusterItems;
    /**
     * Whether to show cluster members
     */
    private boolean clusterItemsed;
    private int count;
    /** Cluster grid bounds */
    @JsonIgnore
    private LatLngBounds latLngBounds;
    public Cluster() {
    }
    public Cluster(LatLng latLng) {
        mLatLng = latLng;
        mClusterItems = new ArrayList<ClusterItem>();
        latLngBounds=new LatLngBounds();
        clusterItemsed=false;
        count=0;
    }

    public Cluster(LatLng latLng, long id) {
        mLatLng = latLng;
        mClusterItems = new ArrayList<ClusterItem>();
        latLngBounds=new LatLngBounds();
        clusterItemsed=false;
        count=0;
        id=id;
    }
    public void addCount() {
        count++;
    }
    public LatLngBounds getLatLngBounds() {
        return latLngBounds;
    }

    public void setLatLngBounds(LatLngBounds latLngBounds) {
        this.latLngBounds = latLngBounds;
    }

    public void addClusterItem(ClusterItem clusterItem) {
        mClusterItems.add(clusterItem);
    }

    int getClusterCount() {
        return mClusterItems.size();
    }

    public List<ClusterItem> getmClusterItems() {
        if(clusterItemsed){
            return mClusterItems;
        }else{
            return null;
        }
    }

    public void setmClusterItems(List<ClusterItem> mClusterItems) {
        this.mClusterItems = mClusterItems;
    }

    public void setClusterItemsed(boolean clusterItemsed) {
        this.clusterItemsed = clusterItemsed;
    }

    public LatLng getCenterLatLng() {
        return mLatLng;
    }

    List<ClusterItem> getClusterItems() {
        return mClusterItems;
    }

    public int getCount() {
        if(count!=0){
            return count;
        }
        return mClusterItems.size();
    }
   /* public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }*/
}
