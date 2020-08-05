package com.adobe.aem.guides.wknd.core.api.client;


import com.adobe.aem.guides.wknd.core.api.client.model.Quote;

import java.util.List;

/**
 *
 */
public interface SimpsonsQuoteService {
    /**
     * Gets the url to the api endpoint.
     *
     * @return The endpoint url
     */
    String getEndpointUrl();

    /**
     * Gets the connection timeout to use for requests to the api.
     *
     * @return The connection timeout in milliseconds.
     */
    int getConnectionTimeout();

    /**
     * Gets the socket timeout to use for requests to the api.
     *
     * @return The socket connection timeout in milliseconds.
     */
    int getSocketTimeout();

    /**
     *  Get the max number of quotes to be allowed
     * @return max number of quotes allowed to be requested at one time
     */
    int getMaxQuotes();

    /**
     * Get a list of Simpsons quotes
     * @param number
     * @return List of quotes
     */
    List<Quote> getSimpsonsQuotes(int number) throws ApiException;

    /**
     * Get a random quote
     * @return single quote
     */
    Quote getRandomQuote() throws ApiException;
}
