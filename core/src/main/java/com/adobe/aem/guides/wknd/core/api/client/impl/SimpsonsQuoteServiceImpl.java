package com.adobe.aem.guides.wknd.core.api.client.impl;

import com.adobe.aem.guides.wknd.core.api.JsonUtil;
import com.adobe.aem.guides.wknd.core.api.client.ApiException;
import com.adobe.aem.guides.wknd.core.api.client.SimpsonsQuoteService;
import com.adobe.aem.guides.wknd.core.api.client.model.Quote;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;

/**
 * SimpsonsQuoteServiceImpl
 * gets quotes from Simpsons quote API
 */
@Component(service = SimpsonsQuoteService.class)
@Designate(ocd = SimpsonsQuoteServiceImpl.Config.class)
public class SimpsonsQuoteServiceImpl implements SimpsonsQuoteService {
    private static final Logger LOG = LoggerFactory.getLogger(SimpsonsQuoteServiceImpl.class);
    private static final String DEFAULT_MAX_QUOTES = "1000";
    private static final String DEFAULT_CONNECTION_TIMEOUT = "15000";
    private static final String DEFAULT_SOCKET_TIMEOUT = "5000";
    public static final String DEFAULT_API_ENDPOINT = "https://thesimpsonsquoteapi.glitch.me/quotes";

    @ObjectClassDefinition(name = "Simplsons Code API Configuration")
    public @interface Config {

        @AttributeDefinition(
                name = "Base API Endpoint URL",
                description = "in the form of URL string - eg http://www.api.com/",
                defaultValue = DEFAULT_API_ENDPOINT
        )
        String EndpointUrl();

        @AttributeDefinition(
                name = "Connection Timeout",
                description = "The connection timeout in milliseconds.  Defaults to " + DEFAULT_CONNECTION_TIMEOUT + '.',
                type = AttributeType.INTEGER,
                defaultValue = DEFAULT_CONNECTION_TIMEOUT
        )
        int ConnectionsTimeout();

        @AttributeDefinition(
                name = "Socket Timeout",
                description = "The socket timeout in milliseconds.  Defaults to " + DEFAULT_SOCKET_TIMEOUT + '.',
                type = AttributeType.INTEGER,
                defaultValue = DEFAULT_SOCKET_TIMEOUT
        )
        int SocketTimeout();

        @AttributeDefinition(
                name = "Max Quotes",
                description = "The number of quotes to be allowed at a time.  Defaults to " + DEFAULT_MAX_QUOTES + '.',
                type = AttributeType.INTEGER,
                defaultValue = DEFAULT_MAX_QUOTES
        )
        int MaxQuotes();
    }

    private int connectionTimeout;
    private int socketTimeout;
    private int maxQuotes;
    private String endpointUrl;

    @Activate
    @Modified
    protected void activate(Config configuration) {
        endpointUrl = configuration.EndpointUrl();
        connectionTimeout = configuration.ConnectionsTimeout();
        socketTimeout = configuration.SocketTimeout();
        maxQuotes = configuration.MaxQuotes();
        LOG.debug("API configuration - endpoint url set to: " + endpointUrl);
        LOG.debug("API configuration - endpoint timeout set to : " + connectionTimeout);
        LOG.debug("API configuration - socket timeout set to : " + socketTimeout);
        LOG.debug("API configuration - max quotes set to : " + maxQuotes);
    }

    @Override
    public String getEndpointUrl() {
        return endpointUrl;
    }

    private String getEndpointWithCount(final int number) {
        return endpointUrl + "?count=" + number;
    }

    @Override
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    @Override
    public int getSocketTimeout() {
        return socketTimeout;
    }

    @Override
    public int getMaxQuotes() {
        return maxQuotes;
    }

    @Override
    public List<Quote> getSimpsonsQuotes(final int number) throws ApiException {
        Response respBody;
        HttpResponse httpResponse = null;
        // sanity checks on quote number
        if (number <= 0 || number > maxQuotes) {
            throw new ApiException("Invalid number of quotes requested - max allowed is " + maxQuotes, 500);
        }

        try {
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(connectionTimeout)
                    .setSocketTimeout(socketTimeout)
                    .build();
            SocketConfig socketConfig = SocketConfig.custom()
                    .setSoTimeout(socketTimeout)
                    .build();
            CloseableHttpClient client = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .setDefaultSocketConfig(socketConfig)
                    .build();

            Executor executor = Executor.newInstance(client);
            respBody = executor.execute(Request.Get(getEndpointWithCount(number)));
            httpResponse = respBody.returnResponse();
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            // read response body out as String
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            httpResponse.getEntity().writeTo(out);
            String responseString = out.toString(String.valueOf(StandardCharsets.UTF_8));

            /*
             * Switch on HTTP status code for conditional handling
             * of different response errors
             * http://hc.apache.org/httpcomponents-core-ga/httpcore/apidocs/org/apache/http/HttpStatus.html
             */
            switch (responseCode) {
                case SC_OK:
                    return JsonUtil.getJsonMapper()
                            .readValue(responseString, new TypeReference<List<Quote>>(){});
                case SC_NOT_FOUND:
                    LOG.debug("404 error for Simpsons quote endpoint");
                    throw new ApiException(httpResponse.getStatusLine().getReasonPhrase(),
                            responseCode, httpResponse.getAllHeaders(),
                            responseString);
                default:
                    LOG.error("Client error occurred for Simpsons quote endpoint");
                    throw new ApiException(httpResponse.getStatusLine().getReasonPhrase(),
                            responseCode, httpResponse.getAllHeaders(),
                            responseString);
            }
        } catch (IOException e) {
            // IOException occurred, throw exception and treat as a server error
            if (Objects.nonNull(httpResponse)) {
                throw new ApiException(e.getMessage(), httpResponse.getStatusLine().getStatusCode(),
                        httpResponse.getAllHeaders(), null);
            } else {
                throw new ApiException(
                        e.getMessage(), 500);
            }
        }
    }

    @Override
    public Quote getRandomQuote() throws ApiException {
        return getSimpsonsQuotes(1).get(0);
    }
}
