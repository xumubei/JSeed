package com.jseed.oss.starter.utils;

import cn.hutool.core.util.ObjectUtil;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * String helpers built on Spring utilities to reduce dependencies.
 *
 * @author cyrus
 */
public class StringUtil extends StringUtils {

	public static final int INDEX_NOT_FOUND = -1;
	/**
	 * <p>The maximum size to which the padding constant(s) can expand.</p>
	 */
	private static final int PAD_LIMIT = 8192;

	/**
	 * Check whether the given {@code CharSequence} contains actual <em>text</em>.
	 * <p>More specifically, this method returns {@code true} if the
	 * {@code CharSequence} is not {@code null}, its length is greater than
	 * 0, and it contains at least one non-whitespace character.
	 * <pre class="code">
	 * StringUtil.isBlank(null) = true
	 * StringUtil.isBlank("") = true
	 * StringUtil.isBlank(" ") = true
	 * StringUtil.isBlank("12345") = false
	 * StringUtil.isBlank(" 12345 ") = false
	 * </pre>
	 *
	 * @param cs the {@code CharSequence} to check (may be {@code null})
	 * @return {@code true} if the {@code CharSequence} is not {@code null},
	 * its length is greater than 0, and it does not contain whitespace only
	 * @see Character#isWhitespace
	 */
	public static boolean isBlank(final CharSequence cs) {
		return !StringUtil.hasText(cs);
	}

	/**
	 * <p>Checks if a CharSequence is not empty (""), not null and not whitespace only.</p>
	 * <pre>
	 * StringUtil.isNotBlank(null)	  = false
	 * StringUtil.isNotBlank("")		= false
	 * StringUtil.isNotBlank(" ")	   = false
	 * StringUtil.isNotBlank("bob")	 = true
	 * StringUtil.isNotBlank("  bob  ") = true
	 * </pre>
	 *
	 * @param cs the CharSequence to check, may be null
	 * @return {@code true} if the CharSequence is
	 * not empty and not null and not whitespace
	 * @see Character#isWhitespace
	 */
	public static boolean isNotBlank(final CharSequence cs) {
		return StringUtil.hasText(cs);
	}

	/**
	 * Checks whether any value is blank.
	 *
	 * @param css CharSequence
	 * @return boolean
	 */
	public static boolean isAnyBlank(final CharSequence... css) {
		if (ObjectUtil.isEmpty(css)) {
			return true;
		}
		return Stream.of(css).anyMatch(StringUtil::isBlank);
	}

	/**
	 * Checks whether all values are non-blank.
	 *
	 * @param css CharSequence
	 * @return boolean
	 */
	public static boolean isNoneBlank(final CharSequence... css) {
		if (ObjectUtil.isEmpty(css)) {
			return false;
		}
		return Stream.of(css).allMatch(StringUtil::isNotBlank);
	}

	/**
	 * Checks whether the string represents a number.
	 *
	 * @param cs the CharSequence to check, may be null
	 * @return {boolean}
	 */
	public static boolean isNumeric(final CharSequence cs) {
		if (isBlank(cs)) {
			return false;
		}
		for (int i = cs.length(); --i >= 0; ) {
			int chr = cs.charAt(i);
			if (chr < 48 || chr > 57) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Convert a {@code Collection} into a delimited {@code String} (e.g., CSV).
	 * <p>Useful for {@code toString()} implementations.
	 *
	 * @param coll the {@code Collection} to convert
	 * @return the delimited {@code String}
	 */
	public static String join(Collection<?> coll) {
		return StringUtil.collectionToCommaDelimitedString(coll);
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
		return StringUtil.collectionToDelimitedString(coll, delim);
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
		return StringUtil.arrayToCommaDelimitedString(arr);
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
		return StringUtil.arrayToDelimitedString(arr, delim);
	}

	/**
	 * Generates a UUID.
	 *
	 * @return UUID
	 */
	public static String randomUUID() {
		return getId(ThreadLocalRandom.current(), 32, 16);
	}

	/**
	 * Generates a random alphanumeric ID with a lower collision probability than an equally long truncated UUID.
	 *
	 * @param len ID length. Use more than seven characters, preferably ten to sixteen or more.
	 * @return generated ID
	 */
	public static String getFastId(int len) {
		return getId(ThreadLocalRandom.current(), len, 62);
	}


	/**
	 * Generates a random ID with a lower collision probability than an equally long truncated UUID.
	 *
	 * @param random Random
	 * @param len    ID length. Use more than seven characters, preferably ten to sixteen or more.
	 * @return generated ID
	 */
	public static String getId(Random random, int len) {
		return getId(random, len, 62);
	}

	/**
	 * Generates a random ID with a lower collision probability than an equally long truncated UUID.
	 *
	 * @param random Random
	 * @param len    ID length. Use more than seven characters, preferably ten to sixteen or more.
	 * @param radix  alphabet size. Base 36 uses lowercase letters and digits. Base 62 also uses uppercase letters.
	 * @return generated ID
	 */
	public static String getId(Random random, int len, int radix) {
		if (len < 8) {
			throw new IllegalArgumentException("为了减少冲突，len 需要大于7，实际尽量设置在10~16或以上。");
		}
		byte[] randomBytes = new byte[len];
		random.nextBytes(randomBytes);
		int mask = radix - 1;
		for (int i = 0; i < len; i++) {
			randomBytes[i] = NumberUtil.DIGITS[(randomBytes[i] & 0xff) & mask];
		}
		return new String(randomBytes, StandardCharsets.ISO_8859_1);
	}

	/**
	 * Escapes HTML for safe rendering.
	 *
	 * @param html html
	 * @return {String}
	 */
	public static String escapeHtml(String html) {
		return StringUtil.isBlank(html) ? StringPool.EMPTY : HtmlUtils.htmlEscape(html);
	}

	/**
	 * Removes certain non-printing characters from the string.
	 *
	 * @param txt input string
	 * @return {String}
	 */
	public static String cleanChars(String txt) {
		return txt.replaceAll("[ 　`·•�\\f\\t\\v\\s]", "");
	}


	private static final String S_INT = "0123456789";
	private static final String S_STR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static final String S_ALL = S_INT + S_STR;

	/**
	 * Generates random characters.
	 *
	 * @param count number of characters
	 * @return random string
	 */
	public static String random(int count) {
		return StringUtil.random(count, RandomType.ALL);
	}

	/**
	 * Generates random characters.
	 *
	 * @param count      number of characters
	 * @param randomType random character set
	 * @return random string
	 */
	public static String random(int count, RandomType randomType) {
		if (count == 0) {
			return "";
		}
		Assert.isTrue(count > 0, "Requested random string length " + count + " is less than 0.");
		final ThreadLocalRandom random = ThreadLocalRandom.current();
		char[] buffer = new char[count];
		for (int i = 0; i < count; i++) {
			if (RandomType.INT == randomType) {
				buffer[i] = S_INT.charAt(random.nextInt(S_INT.length()));
			} else if (RandomType.STRING == randomType) {
				buffer[i] = S_STR.charAt(random.nextInt(S_STR.length()));
			} else {
				buffer[i] = S_ALL.charAt(random.nextInt(S_ALL.length()));
			}
		}
		return new String(buffer);
	}

	/**
	 * Formats the message using logging-style placeholders.
	 * <p>
	 * use: format("my name is {}, and i like {}!", "L.cm", "Java")
	 *
	 * @param message   message template
	 * @param arguments placeholder values
	 * @return formatted string
	 */
	public static String format(@Nullable String message, @Nullable Object... arguments) {
		// Return an empty string when the message is null.
		if (message == null) {
			return StringPool.EMPTY;
		}
		// No arguments were supplied.
		if (arguments == null || arguments.length == 0) {
			return message;
		}
		StringBuilder sb = new StringBuilder((int) (message.length() * 1.5));
		int cursor = 0;
		int index = 0;
		int argsLength = arguments.length;
		for (int start, end; (start = message.indexOf(StringPool.LEFT_BRACE, cursor)) != -1 && (end = message.indexOf(StringPool.RIGHT_BRACE, start)) != -1 && index < argsLength; ) {
			sb.append(message, cursor, start);
			sb.append(arguments[index]);
			cursor = end + 1;
			index++;
		}
		sb.append(message.substring(cursor));
		return sb.toString();
	}

	/**
	 * Formats elapsed time in milliseconds or seconds to three decimal places.
	 *
	 * @param nanos duration in nanoseconds
	 * @return formatted duration
	 */
	public static String format(long nanos) {
		if (nanos < 1) {
			return "0ms";
		}
		double millis = (double) nanos / (1000 * 1000);
		// Use milliseconds even for durations below one millisecond.
		if (millis > 1000) {
			return String.format("%.3fs", millis / 1000);
		} else {
			return String.format("%.3fms", millis);
		}
	}

	/**
	 * Sanitizes an identifier for use as a parameter.
	 *
	 * @param param identifier to sanitize
	 * @return sanitized identifier
	 */
	@Nullable
	public static String cleanIdentifier(@Nullable String param) {
		if (param == null) {
			return null;
		}
		StringBuilder paramBuilder = new StringBuilder();
		for (int i = 0; i < param.length(); i++) {
			char c = param.charAt(i);
			if (Character.isJavaIdentifierPart(c)) {
				paramBuilder.append(c);
			}
		}
		return paramBuilder.toString();
	}

	/**
	 * Checks whether the character occurs in the string.
	 *
	 * @param str        string to search
	 * @param searchChar character to find
	 * @return whether the character was found
	 * @since 3.1.2
	 */
	public static boolean contains(CharSequence str, char searchChar) {
		return indexOf(str, searchChar) > -1;
	}

	/**
	 * Checks whether the string contains any of the supplied search strings.
	 *
	 * @param str      string to search
	 * @param testStrs strings to look for
	 * @return whether any search string occurs
	 * @since 3.2.0
	 */
	public static boolean containsAny(CharSequence str, CharSequence... testStrs) {
		return null != getContainsStr(str, testStrs);
	}

	/**
	 * Returns the first supplied search string found in the input.
	 *
	 * @param str      string to search
	 * @param testStrs strings to look for
	 * @return first matching search string
	 * @since 3.2.0
	 */
	public static String getContainsStr(CharSequence str, CharSequence... testStrs) {
		if (isEmpty(str) || Func.isEmpty(testStrs)) {
			return null;
		}
		for (CharSequence checkStr : testStrs) {
			if (str.toString().contains(checkStr)) {
				return checkStr.toString();
			}
		}
		return null;
	}

	/**
	 * Checks for a substring without regard to case. Returns true when both inputs are <code>null</code>.
	 *
	 * @param str     string to search
	 * @param testStr substring to find
	 * @return whether the character was found
	 */
	public static boolean containsIgnoreCase(CharSequence str, CharSequence testStr) {
		if (null == str) {
			// A null input only matches a null search string.
			return null == testStr;
		}
		return str.toString().toLowerCase().contains(testStr.toString().toLowerCase());
	}

	/**
	 * Checks whether the string contains any of the supplied search strings.<br>
	 * Ignore case.
	 *
	 * @param str      string to search
	 * @param testStrs strings to look for
	 * @return whether any search string occurs
	 * @since 3.2.0
	 */
	public static boolean containsAnyIgnoreCase(CharSequence str, CharSequence... testStrs) {
		return null != getContainsStrIgnoreCase(str, testStrs);
	}

	/**
	 * Returns the first supplied search string found in the input.<br>
	 * Ignore case.
	 *
	 * @param str      string to search
	 * @param testStrs strings to look for
	 * @return first matching search string
	 * @since 3.2.0
	 */
	public static String getContainsStrIgnoreCase(CharSequence str, CharSequence... testStrs) {
		if (isEmpty(str) || Func.isEmpty(testStrs)) {
			return null;
		}
		for (CharSequence testStr : testStrs) {
			if (containsIgnoreCase(str, testStr)) {
				return testStr.toString();
			}
		}
		return null;
	}

	/**
	 * Extracts a substring with support for negative indexes.<br>
	 * Indexes start at 0. Index -1 refers to the last character.<br>
	 * If from and to are equal, return "".<br>
	 * Negative indexes count backward from the end. Out-of-range negative values are clamped to 0 for from and length for to.<br>
	 * Swap the normalized indexes if from is greater than to. Example:<br>
	 * abcdefgh 2 3 =》 c <br>
	 * abcdefgh 2 -3 =》 cde <br>
	 *
	 * @param str       String
	 * @param fromIndex inclusive start index
	 * @param toIndex   exclusive end index
	 * @return substring
	 */
	public static String sub(CharSequence str, int fromIndex, int toIndex) {
		if (isEmpty(str)) {
			return StringPool.EMPTY;
		}
		int len = str.length();

		if (fromIndex < 0) {
			fromIndex = len + fromIndex;
			if (fromIndex < 0) {
				fromIndex = 0;
			}
		} else if (fromIndex > len) {
			fromIndex = len;
		}

		if (toIndex < 0) {
			toIndex = len + toIndex;
			if (toIndex < 0) {
				toIndex = len;
			}
		} else if (toIndex > len) {
			toIndex = len;
		}

		if (toIndex < fromIndex) {
			int tmp = fromIndex;
			fromIndex = toIndex;
			toIndex = tmp;
		}

		if (fromIndex == toIndex) {
			return StringPool.EMPTY;
		}

		return str.toString().substring(fromIndex, toIndex);
	}


	/**
	 * Returns the part before the separator, excluding the separator.<br>
	 * If the input is null or empty, or the separator is null, return the input unchanged.<br>
	 * If the separator is empty, return an empty string. If the separator is absent, return the input unchanged.
	 * <p>
	 * Examples:
	 *
	 * <pre>
	 * StringUtil.subBefore(null, *)      = null
	 * StringUtil.subBefore("", *)        = ""
	 * StringUtil.subBefore("abc", "a")   = ""
	 * StringUtil.subBefore("abcba", "b") = "a"
	 * StringUtil.subBefore("abc", "c")   = "ab"
	 * StringUtil.subBefore("abc", "d")   = "abc"
	 * StringUtil.subBefore("abc", "")    = ""
	 * StringUtil.subBefore("abc", null)  = "abc"
	 * </pre>
	 *
	 * @param string          input string
	 * @param separator       delimiter to exclude
	 * @param isLastSeparator true to use the last occurrence of the separator
	 * @return text before the separator
	 * @since 3.1.1
	 */
	public static String subBefore(CharSequence string, CharSequence separator, boolean isLastSeparator) {
		if (isEmpty(string) || separator == null) {
			return null == string ? null : string.toString();
		}

		final String str = string.toString();
		final String sep = separator.toString();
		if (sep.isEmpty()) {
			return StringPool.EMPTY;
		}
		final int pos = isLastSeparator ? str.lastIndexOf(sep) : str.indexOf(sep);
		if (pos == INDEX_NOT_FOUND) {
			return str;
		}
		return str.substring(0, pos);
	}

	/**
	 * Returns the part after the separator, excluding the separator.<br>
	 * If the input is null or empty, return it unchanged.<br>
	 * If the separator is null, empty, or absent, return an empty string.
	 * <p>
	 * Examples:
	 *
	 * <pre>
	 * StringUtil.subAfter(null, *)      = null
	 * StringUtil.subAfter("", *)        = ""
	 * StringUtil.subAfter(*, null)      = ""
	 * StringUtil.subAfter("abc", "a")   = "bc"
	 * StringUtil.subAfter("abcba", "b") = "cba"
	 * StringUtil.subAfter("abc", "c")   = ""
	 * StringUtil.subAfter("abc", "d")   = ""
	 * StringUtil.subAfter("abc", "")    = "abc"
	 * </pre>
	 *
	 * @param string          input string
	 * @param separator       delimiter to exclude
	 * @param isLastSeparator true to use the last occurrence of the separator
	 * @return text before the separator
	 * @since 3.1.1
	 */
	public static String subAfter(CharSequence string, CharSequence separator, boolean isLastSeparator) {
		if (isEmpty(string)) {
			return null == string ? null : string.toString();
		}
		if (separator == null) {
			return StringPool.EMPTY;
		}
		final String str = string.toString();
		final String sep = separator.toString();
		final int pos = isLastSeparator ? str.lastIndexOf(sep) : str.indexOf(sep);
		if (pos == INDEX_NOT_FOUND) {
			return StringPool.EMPTY;
		}
		return str.substring(pos + separator.length());
	}

	/**
	 * Extracts the text between two markers, excluding the markers.<br>
	 * <p>
	 * Examples:
	 *
	 * <pre>
	 * StringUtil.subBetween("wx[b]yz", "[", "]") = "b"
	 * StringUtil.subBetween(null, *, *)          = null
	 * StringUtil.subBetween(*, null, *)          = null
	 * StringUtil.subBetween(*, *, null)          = null
	 * StringUtil.subBetween("", "", "")          = ""
	 * StringUtil.subBetween("", "", "]")         = null
	 * StringUtil.subBetween("", "[", "]")        = null
	 * StringUtil.subBetween("yabcz", "", "")     = ""
	 * StringUtil.subBetween("yabcz", "y", "z")   = "abc"
	 * StringUtil.subBetween("yabczyabcz", "y", "z")   = "abc"
	 * </pre>
	 *
	 * @param str    input string
	 * @param before opening marker
	 * @param after  closing marker
	 * @return extracted substring
	 * @since 3.1.1
	 */
	public static String subBetween(CharSequence str, CharSequence before, CharSequence after) {
		if (str == null || before == null || after == null) {
			return null;
		}

		final String str2 = str.toString();
		final String before2 = before.toString();
		final String after2 = after.toString();

		final int start = str2.indexOf(before2);
		if (start != INDEX_NOT_FOUND) {
			final int end = str2.indexOf(after2, start + before2.length());
			if (end != INDEX_NOT_FOUND) {
				return str2.substring(start + before2.length(), end);
			}
		}
		return null;
	}

	/**
	 * Extracts the text between two markers, excluding the markers.<br>
	 * <p>
	 * Examples:
	 *
	 * <pre>
	 * StringUtil.subBetween(null, *)            = null
	 * StringUtil.subBetween("", "")             = ""
	 * StringUtil.subBetween("", "tag")          = null
	 * StringUtil.subBetween("tagabctag", null)  = null
	 * StringUtil.subBetween("tagabctag", "")    = ""
	 * StringUtil.subBetween("tagabctag", "tag") = "abc"
	 * </pre>
	 *
	 * @param str            input string
	 * @param beforeAndAfter marker used at both ends
	 * @return extracted substring
	 * @since 3.1.1
	 */
	public static String subBetween(CharSequence str, CharSequence beforeAndAfter) {
		return subBetween(str, beforeAndAfter, beforeAndAfter);
	}

	/**
	 * Removes the specified prefix.
	 *
	 * @param str    input string
	 * @param prefix prefix to remove
	 * @return string without the prefix, or the original string if the prefix is absent
	 */
	public static String removePrefix(CharSequence str, CharSequence prefix) {
		if (isEmpty(str) || isEmpty(prefix)) {
			return StringPool.EMPTY;
		}

		final String str2 = str.toString();
		if (str2.startsWith(prefix.toString())) {
			return subSuf(str2, prefix.length());
		}
		return str2;
	}

	/**
	 * Removes the specified prefix without regard to case.
	 *
	 * @param str    input string
	 * @param prefix prefix to remove
	 * @return string without the prefix, or the original string if the prefix is absent
	 */
	public static String removePrefixIgnoreCase(CharSequence str, CharSequence prefix) {
		if (isEmpty(str) || isEmpty(prefix)) {
			return StringPool.EMPTY;
		}

		final String str2 = str.toString();
		if (str2.toLowerCase().startsWith(prefix.toString().toLowerCase())) {
			return subSuf(str2, prefix.length());
		}
		return str2;
	}

	/**
	 * Removes the specified suffix.
	 *
	 * @param str    input string
	 * @param suffix suffix to remove
	 * @return string without the suffix, or the original string if the suffix is absent
	 */
	public static String removeSuffix(CharSequence str, CharSequence suffix) {
		if (isEmpty(str) || isEmpty(suffix)) {
			return StringPool.EMPTY;
		}

		final String str2 = str.toString();
		if (str2.endsWith(suffix.toString())) {
			return subPre(str2, str2.length() - suffix.length());
		}
		return str2;
	}

	/**
	 * Removes the suffix and lowercases the first letter.
	 *
	 * @param str    input string
	 * @param suffix suffix to remove
	 * @return string without the suffix, or the original string if the suffix is absent
	 */
	public static String removeSufAndLowerFirst(CharSequence str, CharSequence suffix) {
		return lowerFirst(removeSuffix(str, suffix));
	}

	/**
	 * Removes the specified suffix without regard to case.
	 *
	 * @param str    input string
	 * @param suffix suffix to remove
	 * @return string without the suffix, or the original string if the suffix is absent
	 */
	public static String removeSuffixIgnoreCase(CharSequence str, CharSequence suffix) {
		if (isEmpty(str) || isEmpty(suffix)) {
			return StringPool.EMPTY;
		}

		final String str2 = str.toString();
		if (str2.toLowerCase().endsWith(suffix.toString().toLowerCase())) {
			return subPre(str2, str2.length() - suffix.length());
		}
		return str2;
	}

	/**
	 * Lowercases the first letter.
	 *
	 * @param str string
	 * @return {String}
	 */
	public static String lowerFirst(String str) {
		char firstChar = str.charAt(0);
		if (firstChar >= StringPool.U_A && firstChar <= StringPool.U_Z) {
			char[] arr = str.toCharArray();
			arr[0] += (StringPool.L_A - StringPool.U_A);
			return new String(arr);
		}
		return str;
	}

	/**
	 * Uppercases the first letter.
	 *
	 * @param str string
	 * @return {String}
	 */
	public static String upperFirst(String str) {
		char firstChar = str.charAt(0);
		if (firstChar >= StringPool.L_A && firstChar <= StringPool.L_Z) {
			char[] arr = str.toCharArray();
			arr[0] -= (StringPool.L_A - StringPool.U_A);
			return new String(arr);
		}
		return str;
	}

	/**
	 * Gets the part of the string before the specified index.
	 *
	 * @param string  input string
	 * @param toIndex exclusive end index
	 * @return part before the index
	 */
	public static String subPre(CharSequence string, int toIndex) {
		return sub(string, 0, toIndex);
	}

	/**
	 * Gets the part of the string starting at the specified index.
	 *
	 * @param string    input string
	 * @param fromIndex inclusive start index
	 * @return part starting at the index
	 */
	public static String subSuf(CharSequence string, int fromIndex) {
		if (isEmpty(string)) {
			return null;
		}
		return sub(string, fromIndex, string.length());
	}

	/**
	 * Finds a character within the given range.
	 *
	 * @param str        string to search
	 * @param searchChar character to find
	 * @return position of the match
	 */
	public static int indexOf(final CharSequence str, char searchChar) {
		return indexOf(str, searchChar, 0);
	}

	/**
	 * Finds a character within the given range.
	 *
	 * @param str        string to search
	 * @param searchChar character to find
	 * @param start      start index. Negative values are treated as zero.
	 * @return position of the match
	 */
	public static int indexOf(final CharSequence str, char searchChar, int start) {
		if (str instanceof String) {
			return ((String) str).indexOf(searchChar, start);
		} else {
			return indexOf(str, searchChar, start, -1);
		}
	}

	/**
	 * Finds a character within the given range.
	 *
	 * @param str        string to search
	 * @param searchChar character to find
	 * @param start      start index. Negative values are treated as zero.
	 * @param end        end index. Values beyond the length are capped at the end of the string.
	 * @return position of the match
	 */
	public static int indexOf(final CharSequence str, char searchChar, int start, int end) {
		final int len = str.length();
		if (start < 0 || start > len) {
			start = 0;
		}
		if (end > len || end < 0) {
			end = len;
		}
		for (int i = start; i < end; i++) {
			if (str.charAt(i) == searchChar) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds a substring within the given range without regard to case.<br>
	 *
	 * <pre>
	 * StringUtil.indexOfIgnoreCase(null, *, *)          = -1
	 * StringUtil.indexOfIgnoreCase(*, null, *)          = -1
	 * StringUtil.indexOfIgnoreCase("", "", 0)           = 0
	 * StringUtil.indexOfIgnoreCase("aabaabaa", "A", 0)  = 0
	 * StringUtil.indexOfIgnoreCase("aabaabaa", "B", 0)  = 2
	 * StringUtil.indexOfIgnoreCase("aabaabaa", "AB", 0) = 1
	 * StringUtil.indexOfIgnoreCase("aabaabaa", "B", 3)  = 5
	 * StringUtil.indexOfIgnoreCase("aabaabaa", "B", 9)  = -1
	 * StringUtil.indexOfIgnoreCase("aabaabaa", "B", -1) = 2
	 * StringUtil.indexOfIgnoreCase("aabaabaa", "", 2)   = 2
	 * StringUtil.indexOfIgnoreCase("abc", "", 9)        = -1
	 * </pre>
	 *
	 * @param str       input string
	 * @param searchStr substring to find
	 * @return position of the match
	 * @since 3.2.1
	 */
	public static int indexOfIgnoreCase(final CharSequence str, final CharSequence searchStr) {
		return indexOfIgnoreCase(str, searchStr, 0);
	}

	/**
	 * Finds a substring within the given range.
	 *
	 * <pre>
	 * StringUtil.indexOfIgnoreCase(null, *, *)          = -1
	 * StringUtil.indexOfIgnoreCase(*, null, *)          = -1
	 * StringUtil.indexOfIgnoreCase("", "", 0)           = 0
	 * StringUtil.indexOfIgnoreCase("aabaabaa", "A", 0)  = 0
	 * StringUtil.indexOfIgnoreCase("aabaabaa", "B", 0)  = 2
	 * StringUtil.indexOfIgnoreCase("aabaabaa", "AB", 0) = 1
	 * StringUtil.indexOfIgnoreCase("aabaabaa", "B", 3)  = 5
	 * StringUtil.indexOfIgnoreCase("aabaabaa", "B", 9)  = -1
	 * StringUtil.indexOfIgnoreCase("aabaabaa", "B", -1) = 2
	 * StringUtil.indexOfIgnoreCase("aabaabaa", "", 2)   = 2
	 * StringUtil.indexOfIgnoreCase("abc", "", 9)        = -1
	 * </pre>
	 *
	 * @param str       input string
	 * @param searchStr substring to find
	 * @param fromIndex start index
	 * @return position of the match
	 * @since 3.2.1
	 */
	public static int indexOfIgnoreCase(final CharSequence str, final CharSequence searchStr, int fromIndex) {
		return indexOf(str, searchStr, fromIndex, true);
	}

	/**
	 * Searches backward for a substring within the given range.
	 *
	 * @param str        string to search
	 * @param searchStr  substring to find
	 * @param fromIndex  start index
	 * @param ignoreCase whether to ignore case
	 * @return position of the match
	 * @since 3.2.1
	 */
	public static int indexOf(final CharSequence str, CharSequence searchStr, int fromIndex, boolean ignoreCase) {
		if (str == null || searchStr == null) {
			return INDEX_NOT_FOUND;
		}
		if (fromIndex < 0) {
			fromIndex = 0;
		}

		final int endLimit = str.length() - searchStr.length() + 1;
		if (fromIndex > endLimit) {
			return INDEX_NOT_FOUND;
		}
		if (searchStr.length() == 0) {
			return fromIndex;
		}

		if (false == ignoreCase) {
			// Use the JDK method for case-sensitive searches.
			return str.toString().indexOf(searchStr.toString(), fromIndex);
		}

		for (int i = fromIndex; i < endLimit; i++) {
			if (isSubEquals(str, i, searchStr, 0, searchStr.length(), true)) {
				return i;
			}
		}
		return INDEX_NOT_FOUND;
	}

	/**
	 * Finds a substring within the given range without regard to case.<br>
	 *
	 * @param str       input string
	 * @param searchStr substring to find
	 * @return position of the match
	 * @since 3.2.1
	 */
	public static int lastIndexOfIgnoreCase(final CharSequence str, final CharSequence searchStr) {
		return lastIndexOfIgnoreCase(str, searchStr, str.length());
	}

	/**
	 * Finds a substring within the given range without regard to case.<br>
	 *
	 * @param str       input string
	 * @param searchStr substring to find
	 * @param fromIndex starting index for the backward search
	 * @return position of the match
	 * @since 3.2.1
	 */
	public static int lastIndexOfIgnoreCase(final CharSequence str, final CharSequence searchStr, int fromIndex) {
		return lastIndexOf(str, searchStr, fromIndex, true);
	}

	/**
	 * Finds a substring within the given range.<br>
	 *
	 * @param str        string to search
	 * @param searchStr  substring to find
	 * @param fromIndex  starting index for the backward search
	 * @param ignoreCase whether to ignore case
	 * @return position of the match
	 * @since 3.2.1
	 */
	public static int lastIndexOf(final CharSequence str, final CharSequence searchStr, int fromIndex, boolean ignoreCase) {
		if (str == null || searchStr == null) {
			return INDEX_NOT_FOUND;
		}
		if (fromIndex < 0) {
			fromIndex = 0;
		}
		fromIndex = Math.min(fromIndex, str.length());

		if (searchStr.length() == 0) {
			return fromIndex;
		}

		if (false == ignoreCase) {
			// Use the JDK method for case-sensitive searches.
			return str.toString().lastIndexOf(searchStr.toString(), fromIndex);
		}

		for (int i = fromIndex; i > 0; i--) {
			if (isSubEquals(str, i, searchStr, 0, searchStr.length(), true)) {
				return i;
			}
		}
		return INDEX_NOT_FOUND;
	}

	/**
	 * Returns the index of the ordinal occurrence of searchStr in str.<br>
	 * Adapted from Apache Commons Lang.
	 * <p>
	 * Examples, with an asterisk representing any character:
	 *
	 * <pre>
	 * StringUtil.ordinalIndexOf(null, *, *)          = -1
	 * StringUtil.ordinalIndexOf(*, null, *)          = -1
	 * StringUtil.ordinalIndexOf("", "", *)           = 0
	 * StringUtil.ordinalIndexOf("aabaabaa", "a", 1)  = 0
	 * StringUtil.ordinalIndexOf("aabaabaa", "a", 2)  = 1
	 * StringUtil.ordinalIndexOf("aabaabaa", "b", 1)  = 2
	 * StringUtil.ordinalIndexOf("aabaabaa", "b", 2)  = 5
	 * StringUtil.ordinalIndexOf("aabaabaa", "ab", 1) = 1
	 * StringUtil.ordinalIndexOf("aabaabaa", "ab", 2) = 4
	 * StringUtil.ordinalIndexOf("aabaabaa", "", 1)   = 0
	 * StringUtil.ordinalIndexOf("aabaabaa", "", 2)   = 0
	 * </pre>
	 *
	 * @param str       input string, which may be null
	 * @param searchStr substring to find, which may be null
	 * @param ordinal   occurrence to find
	 * @return index of the match
	 * @since 3.2.3
	 */
	public static int ordinalIndexOf(String str, String searchStr, int ordinal) {
		if (str == null || searchStr == null || ordinal <= 0) {
			return INDEX_NOT_FOUND;
		}
		if (searchStr.length() == 0) {
			return 0;
		}
		int found = 0;
		int index = INDEX_NOT_FOUND;
		do {
			index = str.indexOf(searchStr, index + 1);
			if (index < 0) {
				return index;
			}
			found++;
		} while (found < ordinal);
		return index;
	}

	/**
	 * Compares equal-length regions of two strings.<br>
	 * Return false if either string is null.
	 *
	 * @param str1       first string
	 * @param start1     start index in the first string
	 * @param str2       second string
	 * @param start2     start index in the second string
	 * @param length     number of characters to compare
	 * @param ignoreCase whether to ignore case
	 * @return whether the regions match
	 * @since 3.2.1
	 */
	public static boolean isSubEquals(CharSequence str1, int start1, CharSequence str2, int start2, int length, boolean ignoreCase) {
		if (null == str1 || null == str2) {
			return false;
		}

		return str1.toString().regionMatches(ignoreCase, start1, str2.toString(), start2, length);
	}

	/**
	 * Compares two strings with case sensitivity.
	 *
	 * <pre>
	 * equalsIgnoreCase(null, null)   = true
	 * equalsIgnoreCase(null, &quot;abc&quot;)  = false
	 * equalsIgnoreCase(&quot;abc&quot;, null)  = false
	 * equalsIgnoreCase(&quot;abc&quot;, &quot;abc&quot;) = true
	 * equalsIgnoreCase(&quot;abc&quot;, &quot;ABC&quot;) = true
	 * </pre>
	 *
	 * @param str1 first string to compare
	 * @param str2 second string to compare
	 * @return <code>true</code> if the strings are equal or both <code>null</code>
	 */
	public static boolean equals(CharSequence str1, CharSequence str2) {
		return equals(str1, str2, false);
	}

	/**
	 * Compares two strings without regard to case.
	 *
	 * <pre>
	 * equalsIgnoreCase(null, null)   = true
	 * equalsIgnoreCase(null, &quot;abc&quot;)  = false
	 * equalsIgnoreCase(&quot;abc&quot;, null)  = false
	 * equalsIgnoreCase(&quot;abc&quot;, &quot;abc&quot;) = true
	 * equalsIgnoreCase(&quot;abc&quot;, &quot;ABC&quot;) = true
	 * </pre>
	 *
	 * @param str1 first string to compare
	 * @param str2 second string to compare
	 * @return <code>true</code> if the strings are equal or both <code>null</code>
	 */
	public static boolean equalsIgnoreCase(CharSequence str1, CharSequence str2) {
		return equals(str1, str2, true);
	}

	/**
	 * Checks whether two strings are equal.
	 *
	 * @param str1       first string to compare
	 * @param str2       second string to compare
	 * @param ignoreCase whether to ignore case
	 * @return <code>true</code> if the strings are equal or both <code>null</code>
	 * @since 3.2.0
	 */
	public static boolean equals(CharSequence str1, CharSequence str2, boolean ignoreCase) {
		if (null == str1) {
			// Null values match only when both strings are null.
			return str2 == null;
		}
		if (null == str2) {
			// Return false if the second string is null and the first is non-null.
			return false;
		}

		if (ignoreCase) {
			return str1.toString().equalsIgnoreCase(str2.toString());
		} else {
			return str1.equals(str2);
		}
	}

	/**
	 * Creates a StringBuilder.
	 *
	 * @return new StringBuilder
	 */
	public static StringBuilder builder() {
		return new StringBuilder();
	}

	/**
	 * Creates a StringBuilder.
	 *
	 * @param capacity initial capacity
	 * @return new StringBuilder
	 */
	public static StringBuilder builder(int capacity) {
		return new StringBuilder(capacity);
	}

	/**
	 * Creates a StringBuilder.
	 *
	 * @param strs initial strings
	 * @return new StringBuilder
	 */
	public static StringBuilder builder(CharSequence... strs) {
		final StringBuilder sb = new StringBuilder();
		for (CharSequence str : strs) {
			sb.append(str);
		}
		return sb;
	}

	/**
	 * Creates a StringBuilder.
	 *
	 * @param sb   initial StringBuilder
	 * @param strs initial strings
	 * @return new StringBuilder
	 */
	public static StringBuilder appendBuilder(StringBuilder sb, CharSequence... strs) {
		for (CharSequence str : strs) {
			sb.append(str);
		}
		return sb;
	}

	/**
	 * Creates a StringReader.
	 *
	 * @param str string
	 * @return StringReader
	 */
	public static StringReader getReader(CharSequence str) {
		if (null == str) {
			return null;
		}
		return new StringReader(str.toString());
	}

	/**
	 * Creates a StringWriter.
	 *
	 * @return StringWriter
	 */
	public static StringWriter getWriter() {
		return new StringWriter();
	}

	/**
	 * Counts occurrences of the specified substring.<br>
	 * Returns {@code 0} for {@code null} or empty input.
	 *
	 * <pre>
	 * StringUtil.count(null, *)       = 0
	 * StringUtil.count("", *)         = 0
	 * StringUtil.count("abba", null)  = 0
	 * StringUtil.count("abba", "")    = 0
	 * StringUtil.count("abba", "a")   = 2
	 * StringUtil.count("abba", "ab")  = 1
	 * StringUtil.count("abba", "xxx") = 0
	 * </pre>
	 *
	 * @param content      string to search
	 * @param strForSearch substring to count
	 * @return number of matches
	 */
	public static int count(CharSequence content, CharSequence strForSearch) {
		if (Func.hasEmpty(content, strForSearch) || strForSearch.length() > content.length()) {
			return 0;
		}

		int count = 0;
		int idx = 0;
		final String content2 = content.toString();
		final String strForSearch2 = strForSearch.toString();
		while ((idx = content2.indexOf(strForSearch2, idx)) > -1) {
			count++;
			idx += strForSearch.length();
		}
		return count;
	}

	/**
	 * Counts occurrences of the specified character.
	 *
	 * @param content       string to search
	 * @param charForSearch character to count
	 * @return number of matches
	 */
	public static int count(CharSequence content, char charForSearch) {
		int count = 0;
		if (isEmpty(content)) {
			return 0;
		}
		int contentLength = content.length();
		for (int i = 0; i < contentLength; i++) {
			if (charForSearch == content.charAt(i)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Converts snake_case to camelCase.
	 *
	 * @param para input string
	 * @return String
	 */
	public static String underlineToHump(String para) {
		StringBuilder result = new StringBuilder();
		String[] a = para.split("_");
		for (String s : a) {
			if (result.length() == 0) {
				result.append(s.toLowerCase());
			} else {
				result.append(s.substring(0, 1).toUpperCase());
				result.append(s.substring(1).toLowerCase());
			}
		}
		return result.toString();
	}

	/**
	 * Converts camelCase to snake_case.
	 *
	 * @param para input string
	 * @return String
	 */
	public static String humpToUnderline(String para) {
		para = lowerFirst(para);
		StringBuilder sb = new StringBuilder(para);
		int temp = 0;
		for (int i = 0; i < para.length(); i++) {
			if (Character.isUpperCase(para.charAt(i))) {
				sb.insert(i + temp, "_");
				temp += 1;
			}
		}
		return sb.toString().toLowerCase();
	}

	/**
	 * Converts kebab-case to camelCase.
	 *
	 * @param para input string
	 * @return String
	 */
	public static String lineToHump(String para) {
		StringBuilder result = new StringBuilder();
		String[] a = para.split("-");
		for (String s : a) {
			if (result.length() == 0) {
				result.append(s.toLowerCase());
			} else {
				result.append(s.substring(0, 1).toUpperCase());
				result.append(s.substring(1).toLowerCase());
			}
		}
		return result.toString();
	}

	/**
	 * Converts camelCase to kebab-case.
	 *
	 * @param para input string
	 * @return String
	 */
	public static String humpToLine(String para) {
		para = lowerFirst(para);
		StringBuilder sb = new StringBuilder(para);
		int temp = 0;
		for (int i = 0; i < para.length(); i++) {
			if (Character.isUpperCase(para.charAt(i))) {
				sb.insert(i + temp, "-");
				temp += 1;
			}
		}
		return sb.toString().toLowerCase();
	}

	/**
	 * Adapted from Apache Commons Lang.
	 *
	 * <p>Returns padding using the specified delimiter repeated
	 * to a given length.</p>
	 *
	 * <pre>
	 * StringUtils.repeat('e', 0)  = ""
	 * StringUtils.repeat('e', 3)  = "eee"
	 * StringUtils.repeat('e', -2) = ""
	 * </pre>
	 *
	 * <p>Note: this method does not support padding with
	 * <a href="http://www.unicode.org/glossary/#supplementary_character">Unicode Supplementary Characters</a>
	 * as they require a pair of {@code char}s to be represented.
	 * </p>
	 *
	 * @param ch     character to repeat
	 * @param repeat number of times to repeat char, negative treated as zero
	 * @return String with repeated character
	 */
	public static String repeat(final char ch, final int repeat) {
		if (repeat <= 0) {
			return StringPool.EMPTY;
		}
		final char[] buf = new char[repeat];
		Arrays.fill(buf, ch);
		return new String(buf);
	}

	/**
	 * Adapted from Apache Commons Lang.
	 *
	 * <p>Gets the leftmost {@code len} characters of a String.</p>
	 *
	 * <p>If {@code len} characters are not available, or the
	 * String is {@code null}, the String will be returned without
	 * an exception. An empty String is returned if len is negative.</p>
	 *
	 * <pre>
	 * StringUtils.left(null, *)    = null
	 * StringUtils.left(*, -ve)     = ""
	 * StringUtils.left("", *)      = ""
	 * StringUtils.left("abc", 0)   = ""
	 * StringUtils.left("abc", 2)   = "ab"
	 * StringUtils.left("abc", 4)   = "abc"
	 * </pre>
	 *
	 * @param str the CharSequence to get the leftmost characters from, may be null
	 * @param len the length of the required String
	 * @return the leftmost characters, {@code null} if null String input
	 */
	@Nullable
	public static String left(@Nullable final String str, final int len) {
		if (str == null) {
			return null;
		}
		if (len < 0) {
			return StringPool.EMPTY;
		}
		if (str.length() <= len) {
			return str;
		}
		return str.substring(0, len);
	}

	/**
	 * Adapted from Apache Commons Lang.
	 *
	 * <p>Gets the rightmost {@code len} characters of a String.</p>
	 *
	 * <p>If {@code len} characters are not available, or the String
	 * is {@code null}, the String will be returned without an
	 * an exception. An empty String is returned if len is negative.</p>
	 *
	 * <pre>
	 * StringUtils.right(null, *)    = null
	 * StringUtils.right(*, -ve)     = ""
	 * StringUtils.right("", *)      = ""
	 * StringUtils.right("abc", 0)   = ""
	 * StringUtils.right("abc", 2)   = "bc"
	 * StringUtils.right("abc", 4)   = "abc"
	 * </pre>
	 *
	 * @param str the String to get the rightmost characters from, may be null
	 * @param len the length of the required String
	 * @return the rightmost characters, {@code null} if null String input
	 */
	@Nullable
	public static String right(@Nullable final String str, final int len) {
		if (str == null) {
			return null;
		}
		if (len < 0) {
			return StringPool.EMPTY;
		}
		int length = str.length();
		if (length <= len) {
			return str;
		}
		return str.substring(length - len);
	}


	/**
	 * Adapted from Apache Commons Lang.
	 *
	 * <p>Right pad a String with a specified character.</p>
	 *
	 * <p>The String is padded to the size of {@code size}.</p>
	 *
	 * <pre>
	 * StringUtils.rightPad(null, *, *)     = null
	 * StringUtils.rightPad("", 3, 'z')     = "zzz"
	 * StringUtils.rightPad("bat", 3, 'z')  = "bat"
	 * StringUtils.rightPad("bat", 5, 'z')  = "batzz"
	 * StringUtils.rightPad("bat", 1, 'z')  = "bat"
	 * StringUtils.rightPad("bat", -1, 'z') = "bat"
	 * </pre>
	 *
	 * @param str     the String to pad out, may be null
	 * @param size    the size to pad to
	 * @param padChar the character to pad with
	 * @return right padded String or original String if no padding is necessary,
	 * {@code null} if null String input
	 */
	@Nullable
	public static String rightPad(@Nullable final String str, final int size, final char padChar) {
		if (str == null) {
			return null;
		}
		final int pads = size - str.length();
		if (pads <= 0) {
			// returns original String when possible
			return str;
		}
		if (pads > PAD_LIMIT) {
			return rightPad(str, size, String.valueOf(padChar));
		}
		return str.concat(repeat(padChar, pads));
	}

	/**
	 * Adapted from Apache Commons Lang.
	 *
	 * <p>Right pad a String with a specified String.</p>
	 *
	 * <p>The String is padded to the size of {@code size}.</p>
	 *
	 * <pre>
	 * StringUtils.rightPad(null, *, *)      = null
	 * StringUtils.rightPad("", 3, "z")      = "zzz"
	 * StringUtils.rightPad("bat", 3, "yz")  = "bat"
	 * StringUtils.rightPad("bat", 5, "yz")  = "batyz"
	 * StringUtils.rightPad("bat", 8, "yz")  = "batyzyzy"
	 * StringUtils.rightPad("bat", 1, "yz")  = "bat"
	 * StringUtils.rightPad("bat", -1, "yz") = "bat"
	 * StringUtils.rightPad("bat", 5, null)  = "bat  "
	 * StringUtils.rightPad("bat", 5, "")    = "bat  "
	 * </pre>
	 *
	 * @param str    the String to pad out, may be null
	 * @param size   the size to pad to
	 * @param padStr the String to pad with, null or empty treated as single space
	 * @return right padded String or original String if no padding is necessary,
	 * {@code null} if null String input
	 */
	@Nullable
	public static String rightPad(@Nullable final String str, final int size, String padStr) {
		if (str == null) {
			return null;
		}
		if (!StringUtils.hasLength(padStr)) {
			padStr = StringPool.SPACE;
		}
		final int padLen = padStr.length();
		final int strLen = str.length();
		final int pads = size - strLen;
		if (pads <= 0) {
			// returns original String when possible
			return str;
		}
		if (padLen == 1 && pads <= PAD_LIMIT) {
			return rightPad(str, size, padStr.charAt(0));
		}
		if (pads == padLen) {
			return str.concat(padStr);
		} else if (pads < padLen) {
			return str.concat(padStr.substring(0, pads));
		} else {
			final char[] padding = new char[pads];
			final char[] padChars = padStr.toCharArray();
			for (int i = 0; i < pads; i++) {
				padding[i] = padChars[i % padLen];
			}
			return str.concat(new String(padding));
		}
	}
	/**
	 * Adapted from Apache Commons Lang.
	 *
	 * <p>Left pad a String with a specified character.</p>
	 *
	 * <p>Pad to a size of {@code size}.</p>
	 *
	 * <pre>
	 * StringUtils.leftPad(null, *, *)     = null
	 * StringUtils.leftPad("", 3, 'z')     = "zzz"
	 * StringUtils.leftPad("bat", 3, 'z')  = "bat"
	 * StringUtils.leftPad("bat", 5, 'z')  = "zzbat"
	 * StringUtils.leftPad("bat", 1, 'z')  = "bat"
	 * StringUtils.leftPad("bat", -1, 'z') = "bat"
	 * </pre>
	 *
	 * @param str     the String to pad out, may be null
	 * @param size    the size to pad to
	 * @param padChar the character to pad with
	 * @return left padded String or original String if no padding is necessary,
	 * {@code null} if null String input
	 * @since 2.0
	 */
	@Nullable
	public static String leftPad(@Nullable final String str, final int size, final char padChar) {
		if (str == null) {
			return null;
		}
		final int pads = size - str.length();
		if (pads <= 0) {
			// returns original String when possible
			return str;
		}
		if (pads > PAD_LIMIT) {
			return leftPad(str, size, String.valueOf(padChar));
		}
		return repeat(padChar, pads).concat(str);
	}

	/**
	 * Adapted from Apache Commons Lang.
	 *
	 * <p>Left pad a String with a specified String.</p>
	 *
	 * <p>Pad to a size of {@code size}.</p>
	 *
	 * <pre>
	 * StringUtils.leftPad(null, *, *)      = null
	 * StringUtils.leftPad("", 3, "z")      = "zzz"
	 * StringUtils.leftPad("bat", 3, "yz")  = "bat"
	 * StringUtils.leftPad("bat", 5, "yz")  = "yzbat"
	 * StringUtils.leftPad("bat", 8, "yz")  = "yzyzybat"
	 * StringUtils.leftPad("bat", 1, "yz")  = "bat"
	 * StringUtils.leftPad("bat", -1, "yz") = "bat"
	 * StringUtils.leftPad("bat", 5, null)  = "  bat"
	 * StringUtils.leftPad("bat", 5, "")    = "  bat"
	 * </pre>
	 *
	 * @param str    the String to pad out, may be null
	 * @param size   the size to pad to
	 * @param padStr the String to pad with, null or empty treated as single space
	 * @return left padded String or original String if no padding is necessary,
	 * {@code null} if null String input
	 */
	@Nullable
	public static String leftPad(@Nullable final String str, final int size, String padStr) {
		if (str == null) {
			return null;
		}
		if (!StringUtils.hasLength(padStr)) {
			padStr = StringPool.SPACE;
		}
		final int padLen = padStr.length();
		final int strLen = str.length();
		final int pads = size - strLen;
		if (pads <= 0) {
			// returns original String when possible
			return str;
		}
		if (padLen == 1 && pads <= PAD_LIMIT) {
			return leftPad(str, size, padStr.charAt(0));
		}
		if (pads == padLen) {
			return padStr.concat(str);
		} else if (pads < padLen) {
			return padStr.substring(0, pads).concat(str);
		} else {
			final char[] padding = new char[pads];
			final char[] padChars = padStr.toCharArray();
			for (int i = 0; i < pads; i++) {
				padding[i] = padChars[i % padLen];
			}
			return new String(padding).concat(str);
		}
	}

	/**
	 * Adapted from Apache Commons Lang.
	 *
	 * <p>Gets {@code len} characters from the middle of a String.</p>
	 *
	 * <p>If {@code len} characters are not available, the remainder
	 * of the String will be returned without an exception. If the
	 * String is {@code null}, {@code null} will be returned.
	 * An empty String is returned if len is negative or exceeds the
	 * length of {@code str}.</p>
	 *
	 * <pre>
	 * StringUtils.mid(null, *, *)    = null
	 * StringUtils.mid(*, *, -ve)     = ""
	 * StringUtils.mid("", 0, *)      = ""
	 * StringUtils.mid("abc", 0, 2)   = "ab"
	 * StringUtils.mid("abc", 0, 4)   = "abc"
	 * StringUtils.mid("abc", 2, 4)   = "c"
	 * StringUtils.mid("abc", 4, 2)   = ""
	 * StringUtils.mid("abc", -2, 2)  = "ab"
	 * </pre>
	 *
	 * @param str the String to get the characters from, may be null
	 * @param pos the position to start from, negative treated as zero
	 * @param len the length of the required String
	 * @return the middle characters, {@code null} if null String input
	 */
	@Nullable
	public static String mid(@Nullable final String str, int pos, final int len) {
		if (str == null) {
			return null;
		}
		int length = str.length();
		if (len < 0 || pos > length) {
			return StringPool.EMPTY;
		}
		if (pos < 0) {
			pos = 0;
		}
		if (length <= pos + len) {
			return str.substring(pos);
		}
		return str.substring(pos, pos + len);
	}

}

