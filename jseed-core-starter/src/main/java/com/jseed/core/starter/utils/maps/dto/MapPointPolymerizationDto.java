package com.jseed.core.starter.utils.maps.dto;

import lombok.Data;

/**
 * @author cyrus
 * @date 2024/5/18
 */
@Data
public class MapPointPolymerizationDto {

    /**
     * Opposite corners of the visible map area
     */
    private String bounds;

    /**
     * Pixels
     */
    private Double inMeters;

    /**
     * Map zoom level
     */
    private Integer zoom;
}
