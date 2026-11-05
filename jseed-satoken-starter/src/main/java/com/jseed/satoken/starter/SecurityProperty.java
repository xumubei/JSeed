package com.jseed.satoken.starter;

import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Security settings
 *
 * @author cyrus
 */
@Data
@ConfigurationProperties(prefix = "security")
public class SecurityProperty {

    /**
     * Excluded paths
     */
    private List<String> excludes = Lists.newArrayList();


}
