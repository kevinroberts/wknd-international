package com.adobe.aem.guides.wknd.core.api.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 */
public class Quote {
    private int nextQuote;
    private int prevQuote;
    private int likes;
    private String quoteFontSize;
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

    public int getNextQuote() {
        return nextQuote;
    }

    public int getPrevQuote() {
        return prevQuote;
    }

    public void setNextQuote(final int nextQuote) {
        this.nextQuote = nextQuote;
    }

    public void setPrevQuote(final int prevQuote) {
        this.prevQuote = prevQuote;
    }

    public String getQuoteFontSize() {
        return quoteFontSize;
    }

    public void setQuoteFontSize(final String quoteFontSize) {
        this.quoteFontSize = quoteFontSize;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(final int likes) {
        this.likes = likes;
    }
}
