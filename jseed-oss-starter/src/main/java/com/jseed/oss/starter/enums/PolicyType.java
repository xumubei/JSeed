package com.jseed.oss.starter.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * MinIO access policies
 *
 * @author cyrus
 * @date: 2024-09-21
 */
@Getter
@AllArgsConstructor
public enum PolicyType {

	/**
	 * Read only
	 */
	READ("read", "只读"),

	/**
	 * Write only
	 */
	WRITE("write", "只写"),

	/**
	 * Read and write
	 */
	READ_WRITE("read_write", "读写");

	/**
	 * Type
	 */
	private final String type;
	/**
	 * Description
	 */
	private final String policy;

}
