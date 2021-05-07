/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.mts.sdk.api.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
            startDir = Paths.get("").toAbsolutePath().toString();
        }
        File f = new File(fileName);
        if (f.exists())
        {
            return f.getAbsolutePath();
        }
        File dir = new File(startDir);
        for (File tmpFile : dir.listFiles())
        {
            if(tmpFile.isFile() && tmpFile.getName().equals(fileName)){
               return tmpFile.getAbsolutePath();
            }

            if(tmpFile.isDirectory())
            {
                String subFilePath = FindFileInDir(fileName, tmpFile.getAbsolutePath());
                if(!subFilePath.equals(fileName)){
                    return subFilePath;
                }
            }
        }

        return fileName;
    }

    public byte[] ReadFileToByTe(String filePath)
    {
        byte[] msg = new byte[]{};
        if (StringUtils.isNullOrEmpty(filePath))
        {
            return msg;
        }

        filePath = filePath.replace("\\", File.separator);
        filePath = filePath.replace("/", File.separator);
        if(filePath.contains(File.separator)){
            String fileName = filePath.substring(filePath.lastIndexOf(File.separator)+1);
            filePath = FindFileInDir(fileName, "");
        }
        Path path = Paths.get(filePath);
        if(Files.exists(path)){
            try {
                msg = Files.readAllBytes(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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
