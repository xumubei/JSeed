package com.jseed.oss.starter.props;

import com.jseed.oss.starter.model.Kv;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MinIO settings
 *
 * @author cyrus
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "oss")
public class OssProperties {

	/**
	 * Whether object storage is enabled
	 */
	private Boolean enabled;

	/** Allow anonymous reads for buckets created by this starter. */
	private boolean publicRead;

	/**
	 * Object storage provider name
	 */
	private String name;

	/**
	 * Whether tenant mode is enabled
	 */
	private Boolean tenantMode;

	/**
	 * Object storage service URL
	 */
	private String endpoint;

	/**
	 * Externally accessible endpoint URL
	 */
	private String transformEndpoint;

	/**
	 * Access key identifying the account
	 */
	private String accessKey;

	/**
	 * Secret key authenticating the account
	 */
	private String secretKey;

	/**
	 * Default bucket name
	 */
	private String bucketName = "jseed";

	/**
	 * Custom properties
	 */
	private Kv args;

}
