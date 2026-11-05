package com.jseed.oss.starter.config;

import com.jseed.oss.starter.props.OssProperties;
import com.jseed.oss.starter.rule.BladeOssRule;
import com.jseed.oss.starter.rule.OssRule;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Object storage configuration
 *
 * @author cyrus
 */
@AutoConfiguration
@AllArgsConstructor
@EnableConfigurationProperties(OssProperties.class)
public class OssConfiguration {

	@Bean
	@ConditionalOnMissingBean(OssRule.class)
	public OssRule ossRule() {
		return new BladeOssRule();
	}

}
