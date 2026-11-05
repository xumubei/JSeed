package com.jseed.oss.starter.model;

import com.jseed.oss.starter.utils.Func;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

/**
 * Map with fluent operations
 *
 * @author cyrus
 */
public class Kv extends LinkedCaseInsensitiveMap<Object> {

	private Kv() {
		super();
	}

	/**
	 * Creates a Kv instance.
	 *
	 * @return Kv
	 */
	public static Kv init() {
		return new Kv();
	}

	/**
	 * Creates a Kv instance.
	 *
	 * @return Kv
	 */
	public static Kv create() {
		return new Kv();
	}

	public static <K, V> HashMap<K, V> newMap() {
		return new HashMap<>(16);
	}

	/**
	 * Sets an entry.
	 *
	 * @param attr  entry key
	 * @param value value to wrap
	 * @return this instance
	 */
	public Kv set(String attr, Object value) {
		this.put(attr, value);
		return this;
	}

	/**
	 * Sets the entry if both key and value are non-null.
	 *
	 * @param attr  entry key
	 * @param value value to wrap
	 * @return this instance
	 */
	public Kv setIgnoreNull(String attr, Object value) {
		if (null != attr && null != value) {
			set(attr, value);
		}
		return this;
	}

	public Object getObj(String key) {
		return super.get(key);
	}

	/**
	 * Gets a value of the requested type.
	 *
	 * @param <T>          value type
	 * @param attr         field name
	 * @param defaultValue fallback value
	 * @return field value
	 */
	public <T> T get(String attr, T defaultValue) {
		final Object result = get(attr);
		return (T) (result != null ? result : defaultValue);
	}

	/**
	 * Gets a value of the requested type.
	 *
	 * @param attr field name
	 * @return field value
	 */
	public String getStr(String attr) {
		return Func.toStr(get(attr), null);
	}

	/**
	 * Gets a value of the requested type.
	 *
	 * @param attr field name
	 * @return field value
	 */
	public Integer getInt(String attr) {
		return Func.toInt(get(attr), -1);
	}

	/**
	 * Gets a value of the requested type.
	 *
	 * @param attr field name
	 * @return field value
	 */
	public Long getLong(String attr) {
		return Func.toLong(get(attr), -1L);
	}

	/**
	 * Gets a value of the requested type.
	 *
	 * @param attr field name
	 * @return field value
	 */
	public Float getFloat(String attr) {
		return Func.toFloat(get(attr), null);
	}

	public Double getDouble(String attr) {
		return Func.toDouble(get(attr), null);
	}


	/**
	 * Gets a value of the requested type.
	 *
	 * @param attr field name
	 * @return field value
	 */
	public Boolean getBool(String attr) {
		return Func.toBoolean(get(attr), null);
	}

	/**
	 * Gets a value of the requested type.
	 *
	 * @param attr field name
	 * @return field value
	 */
	public byte[] getBytes(String attr) {
		return get(attr, null);
	}

	/**
	 * Gets a value of the requested type.
	 *
	 * @param attr field name
	 * @return field value
	 */
	public Date getDate(String attr) {
		return get(attr, null);
	}

	/**
	 * Gets a value of the requested type.
	 *
	 * @param attr field name
	 * @return field value
	 */
	public Time getTime(String attr) {
		return get(attr, null);
	}

	/**
	 * Gets a value of the requested type.
	 *
	 * @param attr field name
	 * @return field value
	 */
	public Timestamp getTimestamp(String attr) {
		return get(attr, null);
	}

	/**
	 * Gets a value of the requested type.
	 *
	 * @param attr field name
	 * @return field value
	 */
	public Number getNumber(String attr) {
		return get(attr, null);
	}

	@Override
	public Kv clone() {
		Kv clone = new Kv();
		clone.putAll(this);
		return clone;
	}

}
