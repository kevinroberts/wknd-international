<%--
  ADOBE CONFIDENTIAL

  Copyright 2014 Adobe Systems Incorporated
  All Rights Reserved.

  NOTICE:  All information contained herein is, and remains
  the property of Adobe Systems Incorporated and its suppliers,
  if any.  The intellectual and technical concepts contained
  herein are proprietary to Adobe Systems Incorporated and its
  suppliers and may be covered by U.S. and Foreign Patents,
  patents in process, and are protected by trade secret or copyright law.
  Dissemination of this information or reproduction of this material
  is strictly forbidden unless prior written permission is obtained
  from Adobe Systems Incorporated.
--%><%
%><%@include file="/libs/granite/ui/global.jsp" %><%
%><%@ page session="false" import="
				   java.util.Map,
				   java.util.Iterator,
				   org.apache.commons.lang.StringUtils,
				   org.apache.sling.api.resource.Resource,
				   org.apache.sling.api.resource.ValueMap,
                   com.adobe.granite.ui.components.Config,
                   com.adobe.granite.ui.components.AttrBuilder,
                   com.adobe.granite.ui.components.Tag" %><%

    Config cfg = new Config(resource);
    Tag tag = cmp.consumeTag();

    Resource itemRes = resourceResolver.getResource(slingRequest.getRequestPathInfo().getSuffix());
    if (itemRes == null) {
        itemRes = resourceResolver.getResource(request.getParameter("item"));
    }

    String name = cfg.get("name", String.class);
    if (name == null) {
        name = "";
    }
    // Resolve any variable references in the name:
    Map<String,String> templateProperties = (Map<String,String>) request.getAttribute("cq-template-properties");
    if (templateProperties != null) {
        Iterator it = templateProperties.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String,String> pair = (Map.Entry)it.next();
            if (name.contains(pair.getKey())) {
                name = name.replace(pair.getKey(), pair.getValue());
            }
        }
    }
    // if name contains unmapped variables clean entire string
    if (StringUtils.contains(name, "${")) {
        name = "";
    }

    // Initialize the value to either the request-attribute-provided value,
    // or the value from the repository:
    String value = (String) request.getAttribute("cq-product-file-reference");
    if (StringUtils.isEmpty(value)) {
        // if the item exists, get the value from the repository, otherwise empty string
        // (e.g. when creating a new product)
        value = "";
        if (itemRes != null) {
            Resource imageRes = itemRes;
            String imageProp = name;
            // Check if property is from a child (e.g. ./image/fileReference)
            int ix = name.lastIndexOf("/");
            if (ix > 0) {
                imageProp = name.substring(ix + 1);
                // Get the child
                String imagePath = name.substring(0, ix);
                imageRes = itemRes.getChild(imagePath);
                // See if the child is actually under jcr:content node
                if (imageRes == null) {
                    Resource contentRes = itemRes.getChild("jcr:content");
                    if (contentRes != null) {
                        imageRes = contentRes.getChild(imagePath);
                    }
                }
            }
            if (imageRes != null) {
                value = imageRes.adaptTo(ValueMap.class).get(imageProp, "");
            }
        }
    }

    AttrBuilder attrs = tag.getAttrs();
    attrs.add("id", cfg.get("id", String.class));
    attrs.addClass(cfg.get("class", String.class));
    attrs.addRel(cfg.get("rel", String.class));
    attrs.add("title", i18n.getVar(cfg.get("title", String.class)));

    attrs.addClass("cq-AssetPickerField");
    if (StringUtils.isNotBlank(name)) {
        attrs.addOther("name", name);
    }
    attrs.addOthers(cfg.getProperties(), "id", "class", "rel", "title", "type", "name", "value", "emptyText", "disabled", "required", "fieldLabel", "fieldDescription", "renderReadOnly", "ignoreData", "icon");

    boolean buttonOnly = cfg.get("buttonOnly", false);

    AttrBuilder inputAttrs = new AttrBuilder(request, xssAPI);
    inputAttrs.add("type", buttonOnly ? "hidden" : "text");
    if (StringUtils.isNotBlank(name)) {
        inputAttrs.add("name", name);
    }
    inputAttrs.add("placeholder", i18n.getVar(cfg.get("emptyText", String.class)));
    inputAttrs.addDisabled(cfg.get("disabled", false));
    if (StringUtils.isNotBlank(value)) {
        inputAttrs.add("value", value);
    }

    inputAttrs.addClass("coral-InputGroup-input js-datafield");
    inputAttrs.add("is", "coral-textfield");

    if (cfg.get("required", false)) {
        inputAttrs.add("aria-required", true);
    }

%><div <%= attrs.build() %> >
    <div class="coral-InputGroup coral-InputGroup--block">
        <input <%= inputAttrs.build() %> />
        <% if (buttonOnly) { %>
            <button class="js-browse-activator" is="coral-button" type="button"><%= i18n.getVar(xssAPI.encodeForHTML(cfg.get("text", String.class))) %></button>
        <% } else { %>
            <span class="coral-InputGroup-button">
                <button class="js-browse-activator" icon="browse" is="coral-button" type="button" title="<%= i18n.get("Browse") %>">
                </button>
            </span>
        <% } %>
    </div>
</div>