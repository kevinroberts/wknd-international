package com.adobe.aem.guides.wknd.core.models;

import com.adobe.aem.guides.wknd.core.api.client.model.Quote;

import java.util.List;

/**
 *
 */
public interface SimpsonsQuote {

    List<Quote> getQuotes();

    String getIdentifier();

    /***
     * @return a boolean if the component has content to display.
     */
    boolean isEmpty();
}
