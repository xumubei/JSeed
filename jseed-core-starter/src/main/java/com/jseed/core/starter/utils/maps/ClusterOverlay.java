package com.jseed.core.starter.utils.maps;

import com.jseed.core.starter.utils.maps.bean.*;
import com.jseed.core.starter.utils.maps.utils.AMapUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author cyrus
 * @date 2024/5/21
 */
@Slf4j
public class ClusterOverlay {

    /**
     * Zoom level from the client map: map.getZoom().
     */
    private static int zoom = 1;

    /**
     * Cluster size in pixels
     */
    private static int mClusterSize = 80;
    /**
     * Ground distance in meters represented by one pixel at the current zoom level.
     */
//    private static double mPXInMeters=58.619102;
    private static boolean mIsCanceled = false;

    /**
     * Cluster distance from the client: map.getResolution() * mClusterSize.
     */
    public static double getmClusterDistance(double mPXInMeters) {
        return mPXInMeters * mClusterSize;
    }

    public static List<Cluster> setOrgClusters(List<Map<String, Object>> list, double mPXInMeters, LatLngBounds visibleBounds, Integer zoom, boolean isLattice, boolean isClusterItems) {
        long oldNow = System.currentTimeMillis();
        log.info("开始时间" + oldNow);
        List<ClusterItem> mClusterItems = new ArrayList<>();
        List<Cluster> mClusters = new ArrayList<>();

        calculateOrgClusters(visibleBounds, list, mClusters, getmClusterDistance(mPXInMeters), zoom, isLattice, isClusterItems);
        long now = System.currentTimeMillis();
        long result = now - oldNow;
        log.info("map计算的总时间" + result);
        return mClusters;
    }


    public static List<Cluster> setClusters(List<Map<String, Object>> list, double mPXInMeters, LatLngBounds visibleBounds, Integer zoom, boolean isLattice, boolean isClusterItems) {
        long oldNow = System.currentTimeMillis();
        List<ClusterItem> mClusterItems = new ArrayList<>();
        List<Cluster> mClusters = new ArrayList<>();
        if (list != null) {
            list.forEach(data -> {
                if (data != null) {
                    Object latitudeObj = data.get("latitude");
                    Object longitudeObj = data.get("longitude");
                    if (latitudeObj != null && longitudeObj != null) {
                        try {
                            LatLng mLatLng = new LatLng(Double.parseDouble(latitudeObj.toString()), Double.parseDouble(longitudeObj.toString()));
                            ClusterItem clusterItem = new RegionItem(mLatLng, data);
                            mClusterItems.add(clusterItem);
                        } catch (NumberFormatException e) {
                            log.info("经纬度转换异常！id为{}", data.get("id"));
                            e.printStackTrace();
                        }
                    } else {
                        log.info("经纬度为空！id为{}", data.get("id"));
                    }
                }
            });
        }
        calculateClusters(visibleBounds, mClusterItems, mClusters, getmClusterDistance(mPXInMeters), zoom, isLattice, isClusterItems);
        long now = System.currentTimeMillis();
        long result = now - oldNow;
        log.info("点位聚合map计算的总时间{}毫秒", result);
        return mClusters;
    }

    public static MapResult setMapClusters(List<Map<String, Object>> list, double mPXInMeters, LatLngBounds visibleBounds, Integer zoom, boolean isLattice, boolean isClusterItems) {
        long oldNow = System.currentTimeMillis();
        List<ClusterItem> mClusterItems = new ArrayList<>();
        List<Cluster> mClusters = new ArrayList<>();
        ;
        list.forEach(data -> {
            LatLng mLatLng = new LatLng(Double.parseDouble(data.get("lat").toString()), Double.parseDouble(data.get("lng").toString()));
            ClusterItem clusterItem = new RegionItem(mLatLng, data);
            mClusterItems.add(clusterItem);
        });

        calculateClusters(visibleBounds, mClusterItems, mClusters, getmClusterDistance(mPXInMeters), zoom, isLattice, isClusterItems);
        long now = System.currentTimeMillis();
        long result = now - oldNow;
        log.info("result:" + result);
        MapResult mapResult = new MapResult();
        Integer total = 0;
        for (Cluster mCluster : mClusters) {
            total += mCluster.getCount();
        }
        mapResult.setClusters(mClusters);
        mapResult.setTotal(total);
        return mapResult;
    }

    /**
     * @param visibleBounds visible map bounds supplied by the client
     * @param list          all map points
     * @param mClusters     resulting clusters
     * @param isLattice     true to use grid and distance, false to use distance only
     * @return
     */
    private static void calculateOrgClusters(LatLngBounds visibleBounds, List<Map<String, Object>> list, List<Cluster> mClusters, double mClusterDistance, Integer zoom, boolean isLattice, boolean isClusterItems) {
        mIsCanceled = false;
        mClusters.clear();
        ClusterItem clusterItem = new RegionItem();
        LatLng mLatLng;
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> data = list.get(i);
            mLatLng = new LatLng(Double.parseDouble(data.get("lat").toString()), Double.parseDouble(data.get("lng").toString()));
            clusterItem = new RegionItem(mLatLng, data);
            if (mIsCanceled) {
                return;
            }
            LatLng latlng = clusterItem.getPosition();
            if (visibleBounds.contains(latlng)) {
                Cluster cluster = null;
                if (isLattice) {
                    cluster = getClusterFangGe(latlng, mClusters);
                } else {
                    cluster = getCluster(latlng, mClusters, mClusterDistance, zoom);
                }

                if (cluster != null) {
                    cluster.addCount();
                    if (isClusterItems) {
                        cluster.addClusterItem(clusterItem);
                    }
                } else {
                    cluster = new Cluster(latlng);
                    cluster.setClusterItemsed(isClusterItems);
                    mClusters.add(cluster);
                    cluster.addClusterItem(clusterItem);
                    cluster.addCount();
                    // Set the bounds around the center point.
                    if (isLattice) {
                        LatLngBounds latLngBounds = AMapUtils.getAround(latlng, mClusterDistance);
                        cluster.setLatLngBounds(latLngBounds);
                    }
                }
            }
        }
    }

    /**
     * @param visibleBounds  visible map bounds supplied by the client
     * @param mClusterItems  all map points
     * @param mClusters      resulting clusters
     * @param isLattice      true to use grid and distance, false to use distance only
     * @param isClusterItems whether to include details for individual cluster members
     * @return
     */
    private static void calculateClusters(LatLngBounds visibleBounds, List<ClusterItem> mClusterItems, List<Cluster> mClusters, double mClusterDistance, Integer zoom, boolean isLattice, boolean isClusterItems) {
        mIsCanceled = false;
        mClusters.clear();
        for (ClusterItem clusterItem : mClusterItems) {
            if (mIsCanceled) {
                return;
            }
            LatLng latlng = clusterItem.getPosition();
            if (visibleBounds.contains(latlng)) {
                Cluster cluster = null;
                if (isLattice) {
                    cluster = getClusterFangGe(latlng, mClusters);
                } else {
                    cluster = getCluster(latlng, mClusters, mClusterDistance, zoom);
                }
                if (cluster != null) {
                    cluster.addCount();
                    if ((isClusterItems && cluster.getCount() == 1) || zoom > 17) {
                        cluster.addClusterItem(clusterItem);
                    }
                } else {
                    cluster = new Cluster(latlng);
                    cluster.setClusterItemsed(isClusterItems);
                    mClusters.add(cluster);
                    cluster.addClusterItem(clusterItem);
                    cluster.addCount();
                    // Set the bounds around the center point.
                    if (isLattice) {
                        LatLngBounds latLngBounds = AMapUtils.getAround(latlng, mClusterDistance);
                        cluster.setLatLngBounds(latLngBounds);
                    }
                }
            } else {
                log.info("visibleBounds:{}", "不在可见范围内");
            }
        }
    }

    /**
     * Distance-based clustering
     * Finds a cluster the point can join, or returns null.
     *
     * @param latLng           point to test for cluster membership
     * @param clusters         all candidate clusters
     * @param mClusterDistance cluster distance threshold
     * @return
     */
    private static Cluster getCluster(LatLng latLng, List<Cluster> clusters, Double mClusterDistance, Integer zoom) {
        for (Cluster cluster : clusters) {
            LatLng clusterCenterPoint = cluster.getCenterLatLng();
            double distance = AMapUtils.calculateLineDistance(latLng, clusterCenterPoint);
            if (distance < mClusterDistance && zoom < 18) {
                return cluster;
            }
        }
        return null;
    }

    /**
     * Grid and distance-based clustering
     * Finds a cluster the point can join, or returns null.
     *
     * @param latLng   point to test for cluster membership
     * @param clusters all candidate clusters
     * @return
     */
    private static Cluster getClusterLattice(LatLng latLng, List<Cluster> clusters, Integer zoom) {
        Cluster result = null;
        Double resultDistance = 0D;
        for (Cluster cluster : clusters) {
            LatLng clusterCenterPoint = cluster.getCenterLatLng();
            if (cluster.getLatLngBounds().contains(latLng)) {
                double distance = AMapUtils.calculateLineDistance(latLng, clusterCenterPoint);
                if (result == null) {
                    result = cluster;
                    resultDistance = distance;
                } else {
                    if (distance < resultDistance && zoom < 19) {
                        result = cluster;
                        resultDistance = distance;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Grid and distance-based clustering
     * Finds a cluster the point can join, or returns null.
     *
     * @param latLng   point to test for cluster membership
     * @param clusters all candidate clusters
     * @return
     */
    private static Cluster getClusterFangGe(LatLng latLng, List<Cluster> clusters) {
        for (Cluster cluster : clusters) {
            if (cluster.getLatLngBounds().contains(latLng)) {
                return cluster;
            }
        }
        return null;
    }

    /**
     * Adds one point to the existing clusters.
     *
     * @param clusterItem
     */
    private static void calculateSingleCluster(ClusterItem clusterItem, LatLngBounds visibleBounds, List<Cluster> mClusters, Integer zoom, Double mClusterDistance) {
        LatLng latlng = clusterItem.getPosition();
        if (!visibleBounds.contains(latlng)) {
            return;
        }
        Cluster cluster = getCluster(latlng, mClusters, mClusterDistance, zoom);
        if (cluster != null) {
            cluster.addClusterItem(clusterItem);
        } else {
            cluster = new Cluster(latlng);
            mClusters.add(cluster);
            cluster.addClusterItem(clusterItem);

        }
    }
}