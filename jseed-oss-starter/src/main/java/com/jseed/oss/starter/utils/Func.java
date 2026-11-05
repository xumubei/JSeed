package com.jseed.oss.starter.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.base.Charsets;
import org.springframework.beans.BeansException;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.function.Supplier;

/**
 * Convenience methods that delegate to the underlying utilities.
 *
 * @author cyrus
 */
public class Func {

	/**
	 * Checks that the specified object reference is not {@code null}. This
	 * method is designed primarily for doing parameter validation in methods
	 * and constructors, as demonstrated below:
	 * <blockquote><pre>
	 * public Foo(Bar bar) {
	 *     this.bar = $.requireNotNull(bar);
	 * }
	 * </pre></blockquote>
	 *
	 * @param obj the object reference to check for nullity
	 * @param <T> the type of the reference
	 * @return {@code obj} if not {@code null}
	 * @throws NullPointerException if {@code obj} is {@code null}
	 */
	public static <T> T requireNotNull(T obj) {
		return Objects.requireNonNull(obj);
	}

	/**
	 * Checks that the specified object reference is not {@code null} and
	 * throws a customized {@link NullPointerException} if it is. This method
	 * is designed primarily for doing parameter validation in methods and
	 * constructors with multiple parameters, as demonstrated below:
	 * <blockquote><pre>
	 * public Foo(Bar bar, Baz baz) {
	 *     this.bar = $.requireNotNull(bar, "bar must not be null");
	 *     this.baz = $.requireNotNull(baz, "baz must not be null");
	 * }
	 * </pre></blockquote>
	 *
	 * @param obj     the object reference to check for nullity
	 * @param message detail message to be used in the event that a {@code
	 *                NullPointerException} is thrown
	 * @param <T>     the type of the reference
	 * @return {@code obj} if not {@code null}
	 * @throws NullPointerException if {@code obj} is {@code null}
	 */
	public static <T> T requireNotNull(T obj, String message) {
		return Objects.requireNonNull(obj, message);
	}

	/**
	 * Checks that the specified object reference is not {@code null} and
	 * throws a customized {@link NullPointerException} if it is.
	 *
	 * <p>Unlike the method {@link #requireNotNull(Object, String)},
	 * this method allows creation of the message to be deferred until
	 * after the null check is made. While this may confer a
	 * performance advantage in the non-null case, when deciding to
	 * call this method care should be taken that the costs of
	 * creating the message supplier are less than the cost of just
	 * creating the string message directly.
	 *
	 * @param obj             the object reference to check for nullity
	 * @param messageSupplier supplier of the detail message to be
	 *                        used in the event that a {@code NullPointerException} is thrown
	 * @param <T>             the type of the reference
	 * @return {@code obj} if not {@code null}
	 * @throws NullPointerException if {@code obj} is {@code null}
	 * @since 1.8
	 */
	public static <T> T requireNotNull(T obj, Supplier<String> messageSupplier) {
		return Objects.requireNonNull(obj, messageSupplier);
	}

	/**
	 * Returns {@code true} if the provided reference is {@code null} otherwise
	 * returns {@code false}.
	 * <p>
	 * This method exists to be used as a
	 * {@link java.util.function.Predicate}, {@code filter($::isNull)}
	 *
	 * @param obj a reference to be checked against {@code null}
	 * @return {@code true} if the provided reference is {@code null} otherwise
	 * {@code false}
	 * @see java.util.function.Predicate
	 * @since 1.8
	 */
	public static boolean isNull(@Nullable Object obj) {
		return Objects.isNull(obj);
	}

	/**
	 * Returns {@code true} if the provided reference is non-{@code null}
	 * otherwise returns {@code false}.
	 * <p>
	 * This method exists to be used as a
	 * {@link java.util.function.Predicate}, {@code filter($::notNull)}
	 *
	 * @param obj a reference to be checked against {@code null}
	 * @return {@code true} if the provided reference is non-{@code null}
	 * otherwise {@code false}
	 * @see java.util.function.Predicate
	 * @since 1.8
	 */
	public static boolean notNull(@Nullable Object obj) {
		return Objects.nonNull(obj);
	}

	/**
	 * Lowercases the first letter.
	 *
	 * @param str string
	 * @return {String}
	 */
	public static String firstCharToLower(String str) {
		return StringUtil.lowerFirst(str);
	}

	/**
	 * Uppercases the first letter.
	 *
	 * @param str string
	 * @return {String}
	 */
	public static String firstCharToUpper(String str) {
		return StringUtil.upperFirst(str);
	}

	/**
	 * Check whether the given {@code CharSequence} contains actual <em>text</em>.
	 * <p>More specifically, this method returns {@code true} if the
	 * {@code CharSequence} is not {@code null}, its length is greater than
	 * 0, and it contains at least one non-whitespace character.
	 * <pre class="code">
	 * $.isBlank(null)		= true
	 * $.isBlank("")		= true
	 * $.isBlank(" ")		= true
	 * $.isBlank("12345")	= false
	 * $.isBlank(" 12345 ")	= false
	 * </pre>
	 *
	 * @param cs the {@code CharSequence} to check (may be {@code null})
	 * @return {@code true} if the {@code CharSequence} is not {@code null},
	 * its length is greater than 0, and it does not contain whitespace only
	 * @see Character#isWhitespace
	 */
	public static boolean isBlank(@Nullable final CharSequence cs) {
		return StringUtil.isBlank(cs);
	}

	/**
	 * <p>Checks if a CharSequence is not empty (""), not null and not whitespace only.</p>
	 * <pre>
	 * $.isNotBlank(null)	= false
	 * $.isNotBlank("")		= false
	 * $.isNotBlank(" ")	= false
	 * $.isNotBlank("bob")	= true
	 * $.isNotBlank("  bob  ") = true
	 * </pre>
	 *
	 * @param cs the CharSequence to check, may be null
	 * @return {@code true} if the CharSequence is
	 * not empty and not null and not whitespace
	 * @see Character#isWhitespace
	 */
	public static boolean isNotBlank(@Nullable final CharSequence cs) {
		return StringUtil.isNotBlank(cs);
	}

	/**
	 * Checks whether any value is blank.
	 *
	 * @param css CharSequence
	 * @return boolean
	 */
	public static boolean isAnyBlank(final CharSequence... css) {
		return StringUtil.isAnyBlank(css);
	}

	/**
	 * Checks whether all values are non-blank.
	 *
	 * @param css CharSequence
	 * @return boolean
	 */
	public static boolean isNoneBlank(final CharSequence... css) {
		return StringUtil.isNoneBlank(css);
	}


	/**
	 * Determine whether the given object is empty:
	 * i.e. {@code null} or of zero length.
	 *
	 * @param obj the object to check
	 * @return whether the array is empty
	 */
	public static boolean isEmpty(@Nullable Object obj) {
		return ObjectUtil.isEmpty(obj);
	}

	/**
	 * Determine whether the given object is not empty:
	 * i.e. {@code null} or of zero length.
	 *
	 * @param obj the object to check
	 * @return whether the value is non-empty
	 */
	public static boolean isNotEmpty(@Nullable Object obj) {
		return !ObjectUtil.isEmpty(obj);
	}

	/**
	 * Determine whether the given array is empty:
	 * i.e. {@code null} or of zero length.
	 *
	 * @param array the array to check
	 * @return whether the array is empty
	 */
	public static boolean isEmpty(@Nullable Object[] array) {
		return ObjectUtil.isEmpty(array);
	}

	/**
	 * Checks whether the array is non-empty.
	 *
	 * @param array array to check
	 * @return whether the array is non-empty
	 */
	public static boolean isNotEmpty(@Nullable Object[] array) {
		return ObjectUtil.isNotEmpty(array);
	}

	/**
	 * Checks whether any object is empty.
	 *
	 * @param os objects to check
	 * @return boolean
	 */
	public static boolean hasEmpty(Object... os) {
		for (Object o : os) {
			if (isEmpty(o)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether all objects are empty.
	 *
	 * @param os objects to check
	 * @return boolean
	 */
	public static boolean allEmpty(Object... os) {
		for (Object o : os) {
			if (!isEmpty(o)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Compares two objects for equality.<br>
	 * Either of the following conditions is sufficient:<br>
	 *
	 * @param obj1 first object
	 * @param obj2 second object
	 * @return whether the objects are equal
	 */
	public static boolean equals(Object obj1, Object obj2) {
		return Objects.equals(obj1, obj2);
	}

	/**
	 * Converts the value to a trimmed string.
	 *
	 * @param str string
	 * @return String
	 */
	public static String toStr(Object str) {
		return toStr(str, "");
	}

	/**
	 * Converts the value to a trimmed string.
	 *
	 * @param str          string
	 * @param defaultValue fallback value
	 * @return String
	 */
	public static String toStr(Object str, String defaultValue) {
		if (null == str) {
			return defaultValue;
		}
		return String.valueOf(str);
	}

	/**
	 * Converts the value to a trimmed string, preserving empty strings.
	 *
	 * @param str          string
	 * @param defaultValue fallback value
	 * @return {String}
	 */
	public static String toStrWithEmpty(Object str, String defaultValue) {
		if (null == str || str.equals(StringPool.NULL) || str.equals(StringPool.EMPTY)) {
			return defaultValue;
		}
		return String.valueOf(str);
	}

	/**
	 * Checks whether the string represents a number.
	 *
	 * @param cs the CharSequence to check, may be null
	 * @return {boolean}
	 */
	public static boolean isNumeric(final CharSequence cs) {
		return StringUtil.isNumeric(cs);
	}

	/**
	 * <p>Convert a <code>String</code> to an <code>int</code>, returning
	 * <code>zero</code> if the conversion fails.</p>
	 *
	 * <p>If the string is <code>null</code>, <code>zero</code> is returned.</p>
	 *
	 * <pre>
	 *   $.toInt(null) = 0
	 *   $.toInt("")   = 0
	 *   $.toInt("1")  = 1
	 * </pre>
	 *
	 * @param value the string to convert, may be null
	 * @return the int represented by the string, or <code>zero</code> if
	 * conversion fails
	 */
	public static int toInt(final Object value) {
		return NumberUtil.toInt(String.valueOf(value));
	}

	/**
	 * <p>Convert a <code>String</code> to an <code>int</code>, returning a
	 * default value if the conversion fails.</p>
	 *
	 * <p>If the string is <code>null</code>, the default value is returned.</p>
	 *
	 * <pre>
	 *   $.toInt(null, 1) = 1
	 *   $.toInt("", 1)   = 1
	 *   $.toInt("1", 0)  = 1
	 * </pre>
	 *
	 * @param value        the string to convert, may be null
	 * @param defaultValue the default value
	 * @return the int represented by the string, or the default if conversion fails
	 */
	public static int toInt(final Object value, final int defaultValue) {
		return NumberUtil.toInt(String.valueOf(value), defaultValue);
	}

	/**
	 * <p>Convert a <code>String</code> to a <code>long</code>, returning
	 * <code>zero</code> if the conversion fails.</p>
	 *
	 * <p>If the string is <code>null</code>, <code>zero</code> is returned.</p>
	 *
	 * <pre>
	 *   $.toLong(null) = 0L
	 *   $.toLong("")   = 0L
	 *   $.toLong("1")  = 1L
	 * </pre>
	 *
	 * @param value the string to convert, may be null
	 * @return the long represented by the string, or <code>0</code> if
	 * conversion fails
	 */
	public static long toLong(final Object value) {
		return NumberUtil.toLong(String.valueOf(value));
	}

	/**
	 * <p>Convert a <code>String</code> to a <code>long</code>, returning a
	 * default value if the conversion fails.</p>
	 *
	 * <p>If the string is <code>null</code>, the default value is returned.</p>
	 *
	 * <pre>
	 *   $.toLong(null, 1L) = 1L
	 *   $.toLong("", 1L)   = 1L
	 *   $.toLong("1", 0L)  = 1L
	 * </pre>
	 *
	 * @param value        the string to convert, may be null
	 * @param defaultValue the default value
	 * @return the long represented by the string, or the default if conversion fails
	 */
	public static long toLong(final Object value, final long defaultValue) {
		return NumberUtil.toLong(String.valueOf(value), defaultValue);
	}

	/**
	 * <p>Convert a <code>String</code> to an <code>Double</code>, returning a
	 * default value if the conversion fails.</p>
	 *
	 * <p>If the string is <code>null</code>, the default value is returned.</p>
	 *
	 * <pre>
	 *   $.toDouble(null, 1) = 1.0
	 *   $.toDouble("", 1)   = 1.0
	 *   $.toDouble("1", 0)  = 1.0
	 * </pre>
	 *
	 * @param value the string to convert, may be null
	 * @return the int represented by the string, or the default if conversion fails
	 */
	public static Double toDouble(Object value) {
		return toDouble(String.valueOf(value), -1.00);
	}

	/**
	 * <p>Convert a <code>String</code> to an <code>Double</code>, returning a
	 * default value if the conversion fails.</p>
	 *
	 * <p>If the string is <code>null</code>, the default value is returned.</p>
	 *
	 * <pre>
	 *   $.toDouble(null, 1) = 1.0
	 *   $.toDouble("", 1)   = 1.0
	 *   $.toDouble("1", 0)  = 1.0
	 * </pre>
	 *
	 * @param value        the string to convert, may be null
	 * @param defaultValue the default value
	 * @return the int represented by the string, or the default if conversion fails
	 */
	public static Double toDouble(Object value, Double defaultValue) {
		return NumberUtil.toDouble(String.valueOf(value), defaultValue);
	}

	/**
	 * <p>Convert a <code>String</code> to an <code>Float</code>, returning a
	 * default value if the conversion fails.</p>
	 *
	 * <p>If the string is <code>null</code>, the default value is returned.</p>
	 *
	 * <pre>
	 *   $.toFloat(null, 1) = 1.00f
	 *   $.toFloat("", 1)   = 1.00f
	 *   $.toFloat("1", 0)  = 1.00f
	 * </pre>
	 *
	 * @param value the string to convert, may be null
	 * @return the int represented by the string, or the default if conversion fails
	 */
	public static Float toFloat(Object value) {
		return toFloat(String.valueOf(value), -1.0f);
	}

	/**
	 * <p>Convert a <code>String</code> to an <code>Float</code>, returning a
	 * default value if the conversion fails.</p>
	 *
	 * <p>If the string is <code>null</code>, the default value is returned.</p>
	 *
	 * <pre>
	 *   $.toFloat(null, 1) = 1.00f
	 *   $.toFloat("", 1)   = 1.00f
	 *   $.toFloat("1", 0)  = 1.00f
	 * </pre>
	 *
	 * @param value        the string to convert, may be null
	 * @param defaultValue the default value
	 * @return the int represented by the string, or the default if conversion fails
	 */
	public static Float toFloat(Object value, Float defaultValue) {
		return NumberUtil.toFloat(String.valueOf(value), defaultValue);
	}

	/**
	 * <p>Convert a <code>String</code> to an <code>Boolean</code>, returning a
	 * default value if the conversion fails.</p>
	 *
	 * <p>If the string is <code>null</code>, the default value is returned.</p>
	 *
	 * <pre>
	 *   $.toBoolean("true", true)  = true
	 *   $.toBoolean("false")   	= false
	 *   $.toBoolean("", false)  	= false
	 * </pre>
	 *
	 * @param value the string to convert, may be null
	 * @return the int represented by the string, or the default if conversion fails
	 */
	public static Boolean toBoolean(Object value) {
		return toBoolean(value, null);
	}

	/**
	 * <p>Convert a <code>String</code> to an <code>Boolean</code>, returning a
	 * default value if the conversion fails.</p>
	 *
	 * <p>If the string is <code>null</code>, the default value is returned.</p>
	 *
	 * <pre>
	 *   $.toBoolean("true", true)  = true
	 *   $.toBoolean("false")   	= false
	 *   $.toBoolean("", false)  	= false
	 * </pre>
	 *
	 * @param value        the string to convert, may be null
	 * @param defaultValue the default value
	 * @return the int represented by the string, or the default if conversion fails
	 */
	public static Boolean toBoolean(Object value, Boolean defaultValue) {
		if (value != null) {
			String val = String.valueOf(value);
			val = val.toLowerCase().trim();
			return Boolean.parseBoolean(val);
		}
		return defaultValue;
	}

	/**
	 * Converts to an Integer array.<br>
	 *
	 * @param str input to convert
	 * @return converted result
	 */
	public static Integer[] toIntArray(String str) {
		return toIntArray(",", str);
	}

	/**
	 * Converts to an Integer array.<br>
	 *
	 * @param split delimiter
	 * @param str   input to convert
	 * @return converted result
	 */
	public static Integer[] toIntArray(String split, String str) {
		if (StringUtil.isEmpty(str)) {
			return new Integer[]{};
		}
		String[] arr = str.split(split);
		final Integer[] ints = new Integer[arr.length];
		for (int i = 0; i < arr.length; i++) {
			final Integer v = toInt(arr[i], 0);
			ints[i] = v;
		}
		return ints;
	}

	/**
	 * Converts to a list of integers.<br>
	 *
	 * @param str input to convert
	 * @return converted result
	 */
	public static List<Integer> toIntList(String str) {
		return Arrays.asList(toIntArray(str));
	}

	/**
	 * Converts to a list of integers.<br>
	 *
	 * @param split delimiter
	 * @param str   input to convert
	 * @return converted result
	 */
	public static List<Integer> toIntList(String split, String str) {
		return Arrays.asList(toIntArray(split, str));
	}

	/**
	 * Converts to a Long array.<br>
	 *
	 * @param str input to convert
	 * @return converted result
	 */
	public static Long[] toLongArray(String str) {
		return toLongArray(",", str);
	}

	/**
	 * Converts to a Long array.<br>
	 *
	 * @param split delimiter
	 * @param str   input to convert
	 * @return converted result
	 */
	public static Long[] toLongArray(String split, String str) {
		if (StringUtil.isEmpty(str)) {
			return new Long[]{};
		}
		String[] arr = str.split(split);
		final Long[] longs = new Long[arr.length];
		for (int i = 0; i < arr.length; i++) {
			final Long v = toLong(arr[i], 0);
			longs[i] = v;
		}
		return longs;
	}

	/**
	 * Converts to a list of long integers.<br>
	 *
	 * @param str input to convert
	 * @return converted result
	 */
	public static List<Long> toLongList(String str) {
		return Arrays.asList(toLongArray(str));
	}

	/**
	 * Converts to a list of long integers.<br>
	 *
	 * @param split delimiter
	 * @param str   input to convert
	 * @return converted result
	 */
	public static List<Long> toLongList(String split, String str) {
		return Arrays.asList(toLongArray(split, str));
	}

	/**
	 * Gets the first long integer.
	 *
	 * @param str input to convert
	 * @return converted result
	 */
	public static Long firstLong(String str) {
		return firstLong(",", str);
	}

	/**
	 * Gets the first long integer.
	 *
	 * @param split delimiter
	 * @param str   input to convert
	 * @return converted result
	 */
	public static Long firstLong(String split, String str) {
		List<Long> longs = toLongList(split, str);
		if (isEmpty(longs)) {
			return null;
		} else {
			return longs.get(0);
		}
	}

	/**
	 * Converts to a String array.<br>
	 *
	 * @param str input to convert
	 * @return converted result
	 */
	public static String[] toStrArray(String str) {
		return toStrArray(",", str);
	}

	/**
	 * Converts to a String array.<br>
	 *
	 * @param split delimiter
	 * @param str   input to convert
	 * @return converted result
	 */
	public static String[] toStrArray(String split, String str) {
		if (isBlank(str)) {
			return new String[]{};
		}
		return str.split(split);
	}

	/**
	 * Converts to a list of strings.<br>
	 *
	 * @param str input to convert
	 * @return converted result
	 */
	public static List<String> toStrList(String str) {
		return Arrays.asList(toStrArray(str));
	}

	/**
	 * Converts to a list of strings.<br>
	 *
	 * @param split delimiter
	 * @param str   input to convert
	 * @return converted result
	 */
	public static List<String> toStrList(String split, String str) {
		return Arrays.asList(toStrArray(split, str));
	}


	/**
	 * Convert a {@code Collection} into a delimited {@code String} (e.g., CSV).
	 * <p>Useful for {@code toString()} implementations.
	 *
	 * @param coll the {@code Collection} to convert
	 * @return the delimited {@code String}
	 */
	public static String join(Collection<?> coll) {
		return StringUtil.join(coll);
	}

	/**
	 * Convert a {@code Collection} into a delimited {@code String} (e.g. CSV).
	 * <p>Useful for {@code toString()} implementations.
	 *
	 * @param coll  the {@code Collection} to convert
	 * @param delim the delimiter to use (typically a ",")
	 * @return the delimited {@code String}
	 */
	public static String join(Collection<?> coll, String delim) {
		return StringUtil.join(coll, delim);
	}

	/**
	 * Convert a {@code String} array into a comma delimited {@code String}
	 * (i.e., CSV).
	 * <p>Useful for {@code toString()} implementations.
	 *
	 * @param arr the array to display
	 * @return the delimited {@code String}
	 */
	public static String join(Object[] arr) {
		return StringUtil.join(arr);
	}

	/**
	 * Convert a {@code String} array into a delimited {@code String} (e.g. CSV).
	 * <p>Useful for {@code toString()} implementations.
	 *
	 * @param arr   the array to display
	 * @param delim the delimiter to use (typically a ",")
	 * @return the delimited {@code String}
	 */
	public static String join(Object[] arr, String delim) {
		return StringUtil.join(arr, delim);
	}

	/**
	 * Generates a UUID.
	 *
	 * @return UUID
	 */
	public static String randomUUID() {
		return StringUtil.randomUUID();
	}

	/**
	 * Escapes HTML for safe rendering.
	 *
	 * @param html html
	 * @return {String}
	 */
	public static String escapeHtml(String html) {
		return StringUtil.escapeHtml(html);
	}

	/**
	 * Generates random characters.
	 *
	 * @param count number of characters
	 * @return random string
	 */
	public static String random(int count) {
		return StringUtil.random(count);
	}

	/**
	 * Generates random characters.
	 *
	 * @param count      number of characters
	 * @param randomType random character set
	 * @return random string
	 */
	public static String random(int count, RandomType randomType) {
		return StringUtil.random(count, randomType);
	}

	/**
	 * Encodes the value.
	 *
	 * @param value string to encode
	 * @return {String}
	 */
	public static String encodeBase64(String value) {
		return java.util.Base64.getEncoder().encodeToString(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
	}

	/**
	 * Decode the given encoded URI component.
	 * <p>See {@link StringUtils#uriDecode(String, Charset)} for the decoding rules.
	 *
	 * @param source the encoded String
	 * @return the decoded value
	 * @throws IllegalArgumentException when the given source contains invalid encoded sequences
	 * @see StringUtils#uriDecode(String, Charset)
	 * @see java.net.URLDecoder#decode(String, String)
	 */
	public static String decode(String source) {
		return StringUtils.uriDecode(source, Charsets.UTF_8);
	}

	/**
	 * Decode the given encoded URI component.
	 * <p>See {@link StringUtils#uriDecode(String, Charset)} for the decoding rules.
	 *
	 * @param source  the encoded String
	 * @param charset the character encoding to use
	 * @return the decoded value
	 * @throws IllegalArgumentException when the given source contains invalid encoded sequences
	 * @see StringUtils#uriDecode(String, Charset)
	 * @see java.net.URLDecoder#decode(String, String)
	 */
	public static String decode(String source, Charset charset) {
		return StringUtils.uriDecode(source, charset);
	}


	/**
	 * Compares times.
	 *
	 * @param startInclusive the start instant, inclusive, not null
	 * @param endExclusive   the end instant, exclusive, not null
	 * @return a {@code Duration}, not null
	 */
	public static Duration between(Temporal startInclusive, Temporal endExclusive) {
		return Duration.between(startInclusive, endExclusive);
	}


	/**
	 * Finds an annotation.
	 *
	 * @param annotatedElement AnnotatedElement
	 * @param annotationType   annotation class
	 * @param <A>              annotation type
	 * @return {Annotation}
	 */
	@Nullable
	public static <A extends Annotation> A getAnnotation(AnnotatedElement annotatedElement, Class<A> annotationType) {
		return AnnotatedElementUtils.findMergedAnnotation(annotatedElement, annotationType);
	}

	/**
	 * Gets a bean property.
	 *
	 * @param bean         bean
	 * @param propertyName property name
	 * @return property value
	 */
	public static Object getProperty(Object bean, String propertyName) {
		return BeanUtil.getProperty(bean, propertyName);
	}

	/**
	 * Sets a bean property.
	 *
	 * @param bean         bean
	 * @param propertyName property name
	 * @param value        property value
	 */
	public static void setProperty(Object bean, String propertyName, Object value) {
		BeanUtil.setProperty(bean, propertyName, value);
	}


	/**
	 * Copies properties to another object without type conversion by default.
	 * <p>
	 * For beans with fluent setters, use copyProperties.
	 *
	 * @param source source object
	 * @param clazz  target class
	 * @param <T>    target type
	 * @return T
	 */
	public static <T> T copy(Object source, Class<T> clazz) {
		return BeanUtil.copyProperties(source, clazz);
	}

	/**
	 * Copies an object.
	 * <p>
	 * For beans with fluent setters, use copyProperties.
	 *
	 * @param source     source object
	 * @param targetBean destination object
	 */
	public static void copy(Object source, Object targetBean) {
		BeanUtil.copyProperties(source, targetBean);
	}

	/**
	 * Copy the property values of the given source bean into the target class.
	 * <p>Note: The source and target classes do not have to match or even be derived
	 * from each other, as long as the properties match. Any bean properties that the
	 * source bean exposes but the target bean does not will silently be ignored.
	 * <p>This is just a convenience method. For more complex transfer needs,
	 *
	 * @param source the source bean
	 * @param clazz  the target bean class
	 * @param <T>    target type
	 * @return T
	 * @throws BeansException if the copying failed
	 */
	public static <T> T copyProperties(Object source, Class<T> clazz) throws BeansException {
		return BeanUtil.copyProperties(source, clazz);
	}

	/**
	 * Converts a map to a bean.
	 *
	 * @param beanMap   map
	 * @param valueType target type
	 * @param <T>       target type
	 * @return {T}
	 */
	public static <T> T toBean(Map<String, Object> beanMap, Class<T> valueType) {
		return BeanUtil.toBean(beanMap, valueType);
	}

}
