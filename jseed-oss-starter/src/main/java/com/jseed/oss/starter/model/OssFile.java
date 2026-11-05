package com.jseed.oss.starter.model;

import lombok.Data;

import java.util.Date;

/**
 * OssFile
 *
 * @author cyrus
 * @date: 2024-09-21
 */
@Data
public class OssFile {
	/**
	 * File URL
	 */
	private String link;
	/**
	 * File name
	 */
	private String name;
	/**
	 * Object hash
	 */
	public String hash;
	/**
	 * File size
	 */
	private long length;
	/**
	 * Upload time
	 */
	private Date putTime;
	/**
	 * Content type
	 */
	private String contentType;
}
