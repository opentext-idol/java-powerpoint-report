/*
 * Copyright 2017-2018 Micro Focus International plc.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */
package com.hp.autonomy.frontend.reports.powerpoint;

import lombok.Data;
import org.apache.poi.sl.usermodel.PictureData;

/**
 * Data class to store image type and data.
 */
@Data
public class ImageData {

    /** The image type. */
    private final PictureData.PictureType type;

    /** The image data. */
    private final byte[] data;
}
