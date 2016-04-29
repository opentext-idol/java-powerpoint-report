/*
 * Copyright 2014-2016 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

define([
    'jquery'
], function($) {

    var log = function(event) {
        $.ajax('../api/public/stats', {
            contentType: 'application/json',
            data: JSON.stringify(event),
            method: 'POST'
        });
    };

    var types = {
        ABANDONMENT: 'abandonment',
        CLICK_THROUGH: 'clickthrough',
        PAGE: 'page'
    };

    var clickTypes = {
        FULL_PREVIEW: 'full_preview',
        ORIGINAL: 'original',
        PREVIEW: 'preview'
    };

    return {

    };

});
