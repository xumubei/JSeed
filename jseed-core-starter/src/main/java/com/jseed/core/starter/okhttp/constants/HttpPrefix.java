package com.jseed.core.starter.okhttp.constants;

/**
 * HTTP URL scheme
 *
 * @author cyrus
 * @date: 2024-07-08
 */
public interface HttpPrefix {

    /**
     * http
     */
    String HTTP = "http://";

    /**
     * https
     */
    String HTTPS = "https://";

    /**
     * ws
     */
    String WS = "ws://";

    /**
     * wss
     */
    String WSS = "wss://";

    /**
     * Prefix
     */
    class Prefix {
        /**
         * Gets the prefix.
         *
         * @param url URL to inspect
         * @return
         */
        public static String getByUrl(String url) {
            if (url.startsWith(HTTP)) {
                return HTTP;
            }
            if (url.startsWith(HTTPS)) {
                return HTTPS;
            }
            if (url.startsWith(WS)) {
                return WS;
            }
            if (url.startsWith(WSS)) {
                return WSS;
            }
            return null;
        }
    }

}
