package com.jseed.redis.starter.geo;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Redis geospatial utilities
 *
 * @author cyrus
 * @date: 2024-07-10
 */
@AllArgsConstructor
public class JSeedRedisGeoImpl implements JSeedGeo {
    private final String DEFAULT_KEY_PREFIX = "GEO:";
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Long addGeo(String key, String member, String lon, String lat) {
        Point point = new Point(Double.parseDouble(lon), Double.parseDouble(lon));
        RedisGeoCommands.GeoLocation<String> geoLocation = new RedisGeoCommands.GeoLocation<>(member, point);
        return stringRedisTemplate.opsForGeo().add(DEFAULT_KEY_PREFIX + key, geoLocation);
    }

    @Override
    public List<GeoDTO> findNear(String key, String lon, String lat, Integer radius, Integer limit) {
        // Initialize the distance in meters.
        Distance distance = new Distance(radius, RedisGeoCommands.DistanceUnit.METERS);
        // Initialize the geospatial query options.
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs();
        // Limit the result count, include distances, and sort nearest first.
        if (Objects.nonNull(limit)) {
            args.limit(limit).includeDistance().sortAscending();
        }
        Point point = new Point(Double.parseDouble(lon), Double.parseDouble(lat));
        // Use the supplied coordinates as the center and the radius in meters.
        Circle circle = new Circle(point, distance);
        // Fetch the locations of nearby members.
        GeoResults<RedisGeoCommands.GeoLocation<String>> geoResult = stringRedisTemplate.opsForGeo().radius(DEFAULT_KEY_PREFIX + key, circle, args);
        // Populate the member data.
        return Optional.ofNullable(geoResult).map(GeoResults::getContent).orElse(Lists.newArrayList()).stream().map(it -> {
            RedisGeoCommands.GeoLocation<String> location = it.getContent();
            return new GeoDTO(location.getName(), String.valueOf(location.getPoint().getX()), String.valueOf(location.getPoint().getY()));
        }).collect(Collectors.toList());
    }
}
