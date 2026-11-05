package com.jseed.redis.starter.geo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Geospatial data transfer object
 *
 * @author cyrus
 * @date: 2024-07-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeoDTO {
    @Schema(description = "坐标成员标识")
    private String member;
    @Schema(description = "经度")
    private String longitude;
    @Schema(description = "纬度")
    private String latitude;

}
