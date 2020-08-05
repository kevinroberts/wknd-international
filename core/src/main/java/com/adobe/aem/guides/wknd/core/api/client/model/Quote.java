package com.adobe.aem.guides.wknd.core.api.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
public class Quote {
    private String quote;
    private String character;
    private String image;
    private String characterDirection;

    @JsonProperty("quote")
    public String getQuote() {
        return quote;
    }

    @JsonProperty("character")
    public String getCharacter() {
        return character;
    }

    @JsonProperty("image")
    public String getImage() {
        return image;
    }

    @JsonProperty("characterDirection")
    public String getCharacterDirection() {
        return characterDirection;
    }
}
