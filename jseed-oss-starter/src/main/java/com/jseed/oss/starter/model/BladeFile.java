package com.jseed.oss.starter.model;

import lombok.Data;

/**
 * BladeFile
 *
 * @author cyrus
 * @date: 2024-09-21
 */
@Data
public class BladeFile {
	/**
	 * File URL
	 */
	private String link;
	/**
	 * Host URL
	 */
	private String domain;
	/**
	 * File name
	 */
	private String name;
	/**
	 * Original file name
	 */
	private String originalName;
}
