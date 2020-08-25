/*
 * ***********************************************************************
 * WKND Sites Project CONFIDENTIAL
 * ___________________
 *
 * Copyright 2020 WKND Sites Project.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains the property
 * of WKND Sites Project and its suppliers, if any. The intellectual and
 * technical concepts contained herein are proprietary to WKND Sites Project
 * and its suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from WKND Sites Project.
 * ***********************************************************************
 */

package com.adobe.aem.guides.wknd.core.models.impl;

import com.adobe.acs.commons.models.injectors.annotation.SharedValueMapValue;
import com.adobe.acs.commons.wcm.PageRootProvider;
import com.adobe.acs.commons.wcm.properties.shared.SharedComponentProperties;
import com.adobe.aem.guides.wknd.core.models.Banner;
import com.adobe.cq.wcm.core.components.models.Image;
import com.day.cq.wcm.api.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.factory.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Objects;
import java.util.UUID;

@Model(adaptables = {
    SlingHttpServletRequest.class
}, adapters = Banner.class, resourceType = BannerImpl.BANNER_RESOURCE_TYPE)
public class BannerImpl implements Banner
{
    protected static final String BANNER_RESOURCE_TYPE = "wknd/components/content/banner";
    private static final Logger LOG = LoggerFactory.getLogger(BannerImpl.class);

    @Self
    private SlingHttpServletRequest request;

    @OSGiService
    private PageRootProvider pageRootProvider;

    @OSGiService
    private ModelFactory modelFactory;

    @SharedValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String textOverlayShared;

    @SharedValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String linkTextShared;

    @SharedValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String linkUrlShared;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String textOverlay;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String linkText;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL, name = "linkURL")
    private String linkUrl;

    @Inject
    private Node currentNode;

    private Image image;

    private Image sharedImage;

    private boolean empty = false;

    private String identifier;

    @PostConstruct
    private void init() {
        try {
            identifier = StringUtils.substringAfterLast(currentNode.getIdentifier(), "/");
        } catch (RepositoryException e) {
            LOG.warn("Could not get model identifier from component node" , e);
            identifier = UUID.randomUUID().toString().replace("-", "");
        }
        Resource child = request.getResource().getChild("image");
        if (Objects.nonNull(child) && child.getValueMap().containsKey("fileReference")) {
            image = modelFactory.getModelFromWrappedRequest(request, child, Image.class);
        }

        if (Objects.nonNull(pageRootProvider)) {
            Page pageRoot = pageRootProvider.getRootPage(request.getResource());
            String sharedPropsPath = pageRoot.getPath() + "/jcr:content/" + SharedComponentProperties.NN_SHARED_COMPONENT_PROPERTIES +  "/"
                    + BANNER_RESOURCE_TYPE;
            Resource sharedPropsResource = request.getResourceResolver().getResource(sharedPropsPath);
            Resource sharedImageResource = Objects.nonNull(sharedPropsResource) ? sharedPropsResource.getChild("sharedImage") : null;
            if (Objects.nonNull(sharedImageResource) && sharedImageResource.getValueMap().containsKey("fileReference")) {
                sharedImage = modelFactory.getModelFromWrappedRequest(request, sharedImageResource, Image.class);
            }
        }

        if (Objects.isNull(textOverlay)
                && Objects.isNull(linkUrl)
                && Objects.isNull(textOverlayShared)) {
            empty = true;
        }
    }

    @Override
    public String getTextOverlayShared() {
        return textOverlayShared;
    }

    @Override
    public String getTextOverlay() {
        return textOverlay;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getLinkUrl() {
        if (StringUtils.isNotEmpty(linkUrl)
        && !StringUtils.startsWithIgnoreCase(linkUrl, "http")
                && !linkUrl.endsWith(".html")) {
            linkUrl = linkUrl + ".html";
        }
        return linkUrl;
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public String getLinkText() {
        return linkText;
    }

    @Override
    public String getLinkTextShared() {
        return linkTextShared;
    }

    @Override
    public String getLinkUrlShared() {
        if (StringUtils.isNotEmpty(linkUrlShared)
                && !StringUtils.startsWithIgnoreCase(linkUrlShared, "http")
                && !linkUrlShared.endsWith(".html")) {
            linkUrlShared = linkUrlShared + ".html";
        }
        return linkUrlShared;
    }

    @Override
    public Image getImageShared() {
        return sharedImage;
    }

    @Override
    public boolean isEmpty() {
        return empty;
    }
}
