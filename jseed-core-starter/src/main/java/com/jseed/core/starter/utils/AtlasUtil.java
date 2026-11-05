package com.jseed.core.starter.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.jseed.core.starter.enums.ResponseStatusEnum;
import com.jseed.core.starter.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Finds the center of a geographic area.
 *
 * @author cyrus
 * @date: 2024-07-08
 */
public class AtlasUtil {

    /**
     * Each entry in points contains a longitude and latitude, for example points = ['104.390740,30.86660', '104.386963,30.861961', 'longitude,latitude'].
     *
     * @param points
     * @return center coordinates as [longitude, latitude]
     */
    public static Pair<String, String> getPointsCenter(List<String> points) {
        //Number of coordinate points
        int pointNum = points.size();
        double x = 0, y = 0, z = 0;
        for (int i = 0; i < points.size(); i++) {
            if (StringUtils.isBlank(points.get(i))) {
                pointNum--;
                continue;
            }
            String[] point = points.get(i).split(",");
            if (point.length < 2) {
                pointNum--;
                continue;
            }
            double lat, lng, x1, y1, z1;
            lng = Double.parseDouble(point[0]) * Math.PI / 180;
            lat = Double.parseDouble(point[1]) * Math.PI / 180;
            x1 = Math.cos(lat) * Math.cos(lng);
            y1 = Math.cos(lat) * Math.sin(lng);
            z1 = Math.sin(lat);
            x += x1;
            y += y1;
            z += z1;
        }
        x = x / pointNum;
        y = y / pointNum;
        z = z / pointNum;
        double tmpLng = Math.atan2(y, x);
        double tmpLat = Math.atan2(z, Math.sqrt(x * x + y * y));
        return Pair.of(String.valueOf(tmpLng * 180 / Math.PI), String.valueOf(tmpLat * 180 / Math.PI));
    }

    /**
     * Computes the distance between two points, rounded to the nearest meter.
     *
     * @param start starting point
     * @param end   ending point
     * @return
     */
    public static long calculateDistanceByHaversine(String start, String end) {
        return calculateDistanceByHaversine(start, end, 0, RoundingMode.HALF_UP).longValue();
    }

    /**
     * Adds the distances for all point pairs, rounded to the nearest meter.
     *
     * @param points list of start and end point pairs
     * @return
     */
    public static long calculateDistanceByHaversine(List<Pair<String, String>> points) {
        if (CollectionUtil.isEmpty(points)) {
            throw new BusinessException(ResponseStatusEnum.BAD_REQUEST, "非法坐标值");
        }
        return points.stream().mapToLong(it -> calculateDistanceByHaversine(it.getLeft(), it.getRight())).sum();
    }

    /**
     * Adds the distances for all point pairs in meters.
     *
     * @param points       list of start and end point pairs
     * @param scale        number of decimal places, defaulting to zero
     * @param roundingMode rounding mode for decimal places
     * @return
     */
    public static BigDecimal calculateDistanceByHaversine(List<Pair<String, String>> points, int scale, RoundingMode roundingMode) {
        if (CollectionUtil.isEmpty(points)) {
            throw new BusinessException(ResponseStatusEnum.BAD_REQUEST, "非法坐标值");
        }
        return points.stream()
                .map(it -> calculateDistanceByHaversine(it.getLeft(), it.getRight(), scale, roundingMode))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Computes the distance between two points in meters.
     *
     * @param start        starting point
     * @param end          ending point
     * @param scale        number of decimal places, defaulting to zero
     * @param roundingMode rounding mode for decimal places
     * @return
     */
    public static BigDecimal calculateDistanceByHaversine(String start, String end, int scale, RoundingMode roundingMode) {
        if (StringUtils.isBlank(start) || StringUtils.isBlank(end)) {
            throw new BusinessException(ResponseStatusEnum.BAD_REQUEST, "非法坐标值");
        }
        final String[] startSplit = start.split(",");
        final String[] endSplit = end.split(",");
        if (startSplit.length != NumberUtils.INTEGER_TWO || endSplit.length != NumberUtils.INTEGER_TWO) {
            throw new BusinessException(ResponseStatusEnum.BAD_REQUEST, "非法坐标值");
        }
        double sourceLongitude = Double.parseDouble(startSplit[0]);
        double sourceLatitude = Double.parseDouble(startSplit[1]);
        double targetLongitude = Double.parseDouble(endSplit[0]);
        double targetLatitude = Double.parseDouble(endSplit[1]);
        //Earth radius
        double r = 6378137;
        sourceLatitude = sourceLatitude * 3.141593 / 180.0;
        targetLatitude = targetLatitude * 3.141593 / 180.0;
        double a = sourceLatitude - targetLatitude;
        double b = (sourceLongitude - targetLongitude) * 3.141593 / 180.0;
        double sa2 = Math.sin(a / 2.0);
        double sb2 = Math.sin(b / 2.0);
        double d = 2 * r * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(sourceLatitude) * Math.cos(targetLatitude) * sb2 * sb2));
        return BigDecimal.valueOf(d).setScale(scale, roundingMode);
    }
}
