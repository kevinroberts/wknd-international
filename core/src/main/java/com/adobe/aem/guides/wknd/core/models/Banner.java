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

package com.adobe.aem.guides.wknd.core.models;

import com.adobe.cq.wcm.core.components.models.Image;
import org.osgi.annotation.versioning.ConsumerType;


/**
 * Defines the {@code Banner} Sling Model used for the {@code wknd/components/content/banner} component.
 *
 */
@ConsumerType
public interface Banner {

    /**
     * Returns a shared text value that is overlaid on top of the banner
     *
     * @return String
     */
    String getTextOverlayShared();

    /**
     * Returns the shared text value for the banner link
     * @return String
     */
    String getLinkTextShared();

    /**
     * Returns the shared link value for the banner link
     * @return String
     */
    String getLinkUrlShared();

    /**
     * Returns a text value that is overlaid on top of the banner
     *
     * @return String
     *
     */
    String getTextOverlay();

    /**
     * Gets a unique identifier for this component
     * for DOM targeting purposes
     * @return String
     */
    String getIdentifier();

    /**
     * Get the banner image
     * @return Image
     */
    Image getImage();

    /**
     * Get the shared banner image
     * @return Image
     */
    Image getImageShared();

    /**
     * Gets link url for banner
     * @return
     */
    String getLinkUrl();

    /**
     * Gets the banner link text
     * @return String
     */
    String getLinkText();

    /***
     * @return a boolean if the component has content to display.
     */
    boolean isEmpty();

}
