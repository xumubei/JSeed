package com.jseed.oss.starter.rule;

/**
 * Object storage naming rules
 *
 * @author cyrus
 */
public interface OssRule {

	/**
	 * Resolves the bucket name.
	 *
	 * @param bucketName bucket name
	 * @return String
	 */
	String bucketName(String bucketName);

	/**
	 * Resolves the object name.
	 *
	 * @param originalFilename file name
	 * @return String
	 */
	String fileName(String originalFilename);

}
