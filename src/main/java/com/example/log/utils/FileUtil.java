package com.example.log.utils;

import cn.hutool.core.io.resource.ClassPathResource;

import java.io.File;
import java.io.InputStream;

/**
 * 文件工具类
 */
public class FileUtil {
    public static String getPath(String path){
        String absolutePath = cn.hutool.core.io.FileUtil.getAbsolutePath(path);
        return absolutePath;
    }

}
