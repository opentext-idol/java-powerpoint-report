/*
 * Copyright 2017-2018 Micro Focus International plc.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.frontend.reports.powerpoint;

/**
 * Exception to represent any template-related errors.
 */
public class TemplateLoadException extends Exception {
    public TemplateLoadException(final String message) {
        super(message);
    }

    public TemplateLoadException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
