package com.jseed.core.starter.utils.maps.utils;
import com.jseed.core.starter.utils.maps.ClusterOverlay;
import com.jseed.core.starter.utils.maps.bean.Cluster;
import com.jseed.core.starter.utils.maps.bean.LatLngBounds;
import com.jseed.core.starter.utils.maps.dto.MapPointPolymerizationDto;

import java.util.List;
import java.util.Map;
/**
 * @author cyrus
 * @date 2024/5/21
 */
public class MapClusterUtil {

    /**
     * Creates clusters from the supplied map data.
     *
     * @param bounds        string representation of the map bounds, usually supplied by the client
     *                      The string contains the southwest and northeast corners separated by a semicolon.
     *                      For example: "southwestLng,southwestLat;northeastLng,northeastLat".
     * @param mPXInMeters   ground distance in meters per pixel at the current zoom level
     *                      This value determines the clustering distance.
     * @param data          list of maps, each describing a point to cluster
     *                      Each map must contain "latitude" and "longitude" values.
     * @param zoom          map zoom level used to calculate clusters
     * @param isLattice     true to use grid and distance, false to use distance only
     * @param isClusterItems true to include details for individual cluster members
     * @return              list of clusters
     */
    public static List<Cluster> getClusters(String bounds, double mPXInMeters, List<Map<String, Object>> data, int zoom, boolean isLattice, boolean isClusterItems) {
        // Parses the bounds string into a LatLngBounds object.
        LatLngBounds visibleBounds = AMapUtils.boundsToLatLngBounds(bounds);
        // Calls ClusterOverlay.setClusters to build the clusters.
        return ClusterOverlay.setClusters(data, mPXInMeters, visibleBounds, zoom, isLattice, isClusterItems);
    }


    /**
     * Creates clusters from the supplied map data.
     * @param dto map clustering parameters
     * @param data list of maps, each describing a point to cluster
     * @return
     */
    public static List<Cluster> getClusters(MapPointPolymerizationDto dto, List<Map<String, Object>> data) {
        return getClusters(dto.getBounds(), dto.getInMeters(),data, dto.getZoom(), false,true);
    }

}
