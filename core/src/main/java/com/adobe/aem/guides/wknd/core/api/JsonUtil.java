package com.adobe.aem.guides.wknd.core.api;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *  JsonUtil
 *  Json mapping utility; provides json ObjectMapper with
 *  global configurations applied.
 */
public class JsonUtil {

    public static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        // global feature that can be used to suppress all failures caused by unknown (unmapped) json properties:
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static ObjectMapper getJsonMapper() {
        return mapper;
    }

}
