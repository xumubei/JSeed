package com.jseed.redis.starter.geo;

import java.util.List;

/**
 * Geospatial operations
 *
 * @author cyrus
 * @date:  2024-07-10
 */
public interface JSeedGeo {

    /**
     * Adds coordinates for a location.
     *
     * @param key    Redis geospatial key
     * @param member member identifier
     * @param lon    longitude
     * @param lat    latitude
     * @return
     */
    Long addGeo(String key, String member, String lon, String lat);

    /**
     * Finds members within the given radius of the supplied coordinates.
     *
     * @param key    Redis geospatial key
     * @param lon    longitude
     * @param lat    latitude
     * @param radius radius in meters
     * @param limit  maximum number of results
     * @return
     */
    List<GeoDTO> findNear(String key, String lon, String lat, Integer radius, Integer limit);

}
