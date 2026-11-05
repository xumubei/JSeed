package com.jseed.core.starter.utils.maps.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author cyrus
 * @date 2024/5/18
 */
public class MapResult implements Serializable {

    private static final long serialVersionUID = -3704006355756554713L;
    private Integer total;
    private List<Cluster> clusters;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<Cluster> getClusters() {
        return clusters;
    }

    public void setClusters(List<Cluster> clusters) {
        this.clusters = clusters;
    }
}
