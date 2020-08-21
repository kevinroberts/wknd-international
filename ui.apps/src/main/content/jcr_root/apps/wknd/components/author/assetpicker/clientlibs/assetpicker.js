/*
 * ADOBE CONFIDENTIAL
 *
 * Copyright 2014 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 */

(function(window, document, Granite, $) {
    "use strict";

    var rel = ".cq-AssetPickerField",
        assetPickerDialog,
        $assetPicker,
        modifiedPreviewTargets = [];

    /**
     * Runs the AssetPicker dialog.
     * https://helpx.adobe.com/experience-manager/6-3/assets/using/asset-selector.html
     */
    function showAssetPicker() {
        var href = Granite.HTTP.externalize("/aem/assetpicker?mode=single&mimetype=image/*&root=/content/dam/wknd"),
            $iframe = $('<iframe class="cq-AssetPicker cq-AssetPicker-iframe"></iframe>');

        $iframe.attr("src", href);

        assetPickerDialog = new Coral.Dialog();
        var $assetPicker = $(assetPickerDialog);
        $assetPicker.appendTo("body");
        $assetPicker.empty();
        $assetPicker.append($iframe);
        $assetPicker.attr("modal", "true");
        $assetPicker.addClass("cq-AssetPicker cq-AssetPicker-modal");

        assetPickerDialog.show();
        return $assetPicker;
    }

    /**
     * Receives two-part messages from the AssetPicker dialog.  The "data" part indicates the
     * template picker path should be updated; the "config" part indicates whether or not the
     * dialog should be closed.
     */
    function receiveMessage(event) {
        if (event.origin !== location.origin) {
            return;
        }
        if (!$assetPicker) {
            return;
        }

        var fromDam = JSON.parse(event.data);

        if (fromDam.data) {
            var $sink = $assetPicker.data("sink"),
                path = decodeURI(fromDam.data[0].path);

            $sink.val(path).change();
        }

        if (fromDam.config) {
            var configFromDam = fromDam.config;
            if (configFromDam.action === 'close' || configFromDam.action === 'done') {
                assetPickerDialog.hide();
            }
        }
    }

    window.addEventListener("message", receiveMessage, false);

    /*
     * Update the previewTarget in response to js-datafield changes.
     */
    $(document).on("change", rel + " .js-datafield", function (e) {
        var $pickerField = $(e.target).closest(rel),
            $productImageForm = $pickerField.closest(".product-image"),
            path = Granite.HTTP.externalize($(e.target).val()),
            previewSelector = $pickerField.attr("data-previewtarget"),
            $previewTarget = null;

        if ($productImageForm.length > 0) {
            // The product editor supports multiple asset types
            $previewTarget = $productImageForm.find(previewSelector);
            $previewTarget.attr("src", path);
        } else {
            $previewTarget = $(document).find(previewSelector);
            if ($previewTarget.length > 0) {
                if (!$previewTarget.data("reset-value")) {
                    $previewTarget.data("reset-value", $previewTarget.attr("src"));
                    modifiedPreviewTargets.push($previewTarget);
                }
                $previewTarget.attr("src", path);
            }
        }
    });
    /*
     * Set default preview to previous set value
     */
    $(document).on("dialog-ready", function() {
        var val = $(".cq-AssetPickerField .js-datafield").val();
        var $pickerField = $(rel);
        var previewSelector = $pickerField.attr("data-previewtarget");

        if (val && val !== '') {
            var $previewTarget = $(document).find(previewSelector);
            if ($previewTarget.length > 0) {
                console.log("setting preview target to ", val)
                $previewTarget.attr("src", val);
            }
        }
    });

    /*
     * Reset any modified previewTargets when form is reset.
     */
    $(document).on("reset", "form", function(e) {
        e.preventDefault();
        for (var i = 0; i < modifiedPreviewTargets.length; i++) {
            var $previewTarget = modifiedPreviewTargets[i];
            if ($previewTarget && $previewTarget.data("reset-value")) {
                $previewTarget.attr("src", $previewTarget.data("reset-value"));
            }
        }
    });

    /*
     * Run the AssetPicker dialog when the browse-activator is clicked.
     */
    $(document).on("click", rel + " .js-browse-activator", function (e) {
        var $pickerField = $(e.target).closest(rel),
            $sink = $pickerField.find(".js-datafield");
        $assetPicker = showAssetPicker();
        $assetPicker.data("sink", $sink);
    });

})(window, document, Granite, Granite.$);

