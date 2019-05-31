/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.utils;

import java.io.*;

public class FileUtils {

    public static InputStream filePathAsInputStream(String path) throws IOException {
        File file = new File(path);
        if (!file.exists() && !file.isDirectory()) {
            throw new FileNotFoundException(StringUtils.format("File {} not found", path));
        }
        return new FileInputStream(file);
    }
}
