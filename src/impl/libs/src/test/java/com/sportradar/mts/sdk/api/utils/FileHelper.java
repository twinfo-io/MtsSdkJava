/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.utils;

import com.google.common.io.Closeables;
import sun.misc.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileHelper {

    public static List<String> OpenFile(String dirPath, String fileName)
    {
        if(dirPath.endsWith("/"))
        {
            dirPath = dirPath.substring(0, dirPath.length()-2);
        }
        if(fileName.startsWith("/"))
        {
            fileName = fileName.substring(1);
        }
        String filePath = dirPath + "/" + fileName;
        return OpenFile(filePath);
    }

    public static List<String> OpenFile(String filePath)
    {
        filePath = FindFileInDir(filePath, null);

        List<String> list = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            list = stream.collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String ReadFile(String dirPath, String fileName)
    {
        List<String> stream = OpenFile(dirPath, fileName);
        String content = String.join("\n", stream);
        return content;
    }

    public static String FindFileInDir(String fileName, String startDir)
    {
        if (StringUtils.isNullOrEmpty(fileName))
        {
            return "";
        }
        if (StringUtils.isNullOrEmpty(startDir))
        {
            startDir = Paths.get("").toString();
        }
        File f = new File(fileName);
        if (f.exists())
        {
            return fileName;
        }
        for (File dir : f.listFiles())
        {
            if(dir.isDirectory())
            {
                for(File file : dir.listFiles())
                {
                    if(file.getName().equals(fileName))
                    {
                        return file.getAbsolutePath();
                    }
                }
            }
        }

        return fileName;
    }

    public byte[] ReadFileToByTe(String filePath)
    {
        if (StringUtils.isNullOrEmpty(filePath))
        {
            return new byte[]{};
        }
        filePath = filePath.replace("\\", "/");
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(filePath);
        byte[] msg = null;
        try {
            msg = IOUtils.readFully(stream, -1, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Closeables.closeQuietly(stream);
        return msg;
    }

    public String ReadFileToJson(String filePath)
    {
        if (StringUtils.isNullOrEmpty(filePath))
        {
            return null;
        }
        String json = new String(ReadFileToByTe(filePath));
        json = json.replace("\n\r", "");
        json = json.replace("\t", "");

        return json;
    }
}
