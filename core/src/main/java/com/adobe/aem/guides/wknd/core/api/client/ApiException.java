package com.adobe.aem.guides.wknd.core.api.client;

import com.google.common.base.Strings;
import org.apache.http.Header;

/**
 * ApiException
 * all possible API exceptions can be thrown
 * via this class
 */
public final class ApiException extends Exception {
    private final int code;
    private final Header[] responseHeaders;
    private final String responseBody;

    public ApiException(final String message, final int code, final String responseBody) {
        this(message, null, code, null, responseBody);
    }

    public ApiException(final String message, final int code, final Header[] responseHeaders,
                        final String responseBody) {
        this(message, null, code, responseHeaders, responseBody);
    }

    public ApiException(final String message, final int code) {
        this(message, null, code, null, "");
    }

    public ApiException(final String message, final Throwable throwable, final int code,
                        final Header[] responseHeaders, final String responseBody) {
        super(Strings.nullToEmpty(message), throwable);
        this.code = code;
        final Header[] NO_HEADERS = {};
        this.responseHeaders = responseHeaders == null
                ? NO_HEADERS
                : responseHeaders;
        this.responseBody = Strings.nullToEmpty(responseBody);
    }

    public int getCode() {
        return code;
    }

    /**
     * Get the HTTP response headers.
     */
    public Header[] getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * Get the HTTP response body.
     */
    public String getResponseBody() {
        return responseBody;
    }
}
