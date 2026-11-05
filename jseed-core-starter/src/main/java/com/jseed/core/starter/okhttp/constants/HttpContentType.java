package com.jseed.core.starter.okhttp.constants;

public interface HttpContentType {

    /**
     * No request body type
     */
    String NONE = "";

    /**
     * Single-file upload
     */
    String FILE = "application/octet-stream";

    /**
     * Form or multipart upload
     */
    String MULTIPART_FILE = "multipart/form-data";

    /**
     * JSON request
     */
    String JSON = "application/json";

    /**
     * Form request
     */
    String FORM = "application/x-www-form-urlencoded";
}
