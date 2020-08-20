package com.adobe.aem.guides.wknd.core.models.impl;

import com.adobe.aem.guides.wknd.core.api.client.ApiException;
import com.adobe.aem.guides.wknd.core.api.client.SimpsonsQuoteService;
import com.adobe.aem.guides.wknd.core.api.client.model.Quote;
import com.adobe.aem.guides.wknd.core.models.SimpsonsQuote;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.List;
import java.util.Objects;

import static com.adobe.aem.guides.wknd.core.util.AppConstants.WKND_COMPONENT_PATH;

/**
 * SimpsonsQuoteImpl
 * Component for rendering random simpson quotes
 *
 * @Injected fields/methods are assumed to be required. To mark them as optional, use @Optional:
 * DefaultInjectionStrategy.OPTIONAL to the @Model annotation: will change the default behavior
 */
@Model(
        adaptables = {SlingHttpServletRequest.class},
        adapters = {SimpsonsQuote.class},
        resourceType = {SimpsonsQuoteImpl.RESOURCE_TYPE},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class SimpsonsQuoteImpl implements SimpsonsQuote {
    protected static final String RESOURCE_TYPE = WKND_COMPONENT_PATH + "/content/simpsonsquote";
    private static final Logger LOG = LoggerFactory.getLogger(SimpsonsQuoteImpl.class);

    @OSGiService
    private SimpsonsQuoteService simpsonsQuoteService;

    @Inject
    private Node currentNode;

    @ValueMapValue
    private Integer numberOfQuotes;

    private List<Quote> quoteList;
    private String identifier;

    @PostConstruct
    private void init() {
        try {
            identifier = StringUtils.substringAfterLast(currentNode.getIdentifier(), "/");
        } catch (RepositoryException e) {
            LOG.warn("Could not get model identifier" , e);
        }
        try {
            if (Objects.nonNull(numberOfQuotes)) {
                quoteList = simpsonsQuoteService.getSimpsonsQuotes(numberOfQuotes);
            } else {
                quoteList = simpsonsQuoteService.getSimpsonsQuotes(1);
            }

            if (Objects.nonNull(quoteList) && quoteList.size() > 0) {
                for (int i = 0; i < quoteList.size(); i++) {
                    Quote quote = quoteList.get(i);
                    quote.setLikes(RandomUtils.nextInt(10, 500));
                    // reduce font size for longer quotes
                    if (quote.getQuote().length() > 177) {
                        quote.setQuoteFontSize("22px");
                    } else if (quote.getQuote().length() > 94 && quote.getQuote().length() <= 177) {
                        quote.setQuoteFontSize("24px");
                    } else if (quote.getQuote().length() >= 74 && quote.getQuote().length() <= 94) {
                        quote.setQuoteFontSize("32px");
                    } else {
                        quote.setQuoteFontSize("48px");
                    }
                    // set prev. and next quote indices
                    if (i == 0) {
                        quoteList.get(0).setPrevQuote(quoteList.size());
                        quoteList.get(0).setNextQuote(i + 2);
                    } else if (i == (quoteList.size() - 1)) {
                        quoteList.get(i).setPrevQuote(i);
                        quoteList.get(i).setNextQuote(1);
                    } else {
                        quoteList.get(i).setPrevQuote(i);
                        quoteList.get(i).setNextQuote(i + 2);
                    }
                }
            }
        } catch (ApiException e) {
            LOG.error("API exception reached, ", e);
        }
    }

    @Override
    public List<Quote> getQuotes() {
        return quoteList;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean isEmpty() {
        return (Objects.isNull(numberOfQuotes));
    }
}
