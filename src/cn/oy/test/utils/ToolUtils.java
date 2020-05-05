package cn.oy.test.utils;

import cn.oy.test.processor.*;
import cn.oy.test.processor.impl.*;
import cn.oy.test.model.TYPE;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 蒜头王八
 * @project: ftp
 * @Description:
 * @Date 2020/5/3 16:19
 */
public class ToolUtils {
    public static Map<String, Commond> commondMap = new HashMap<>();
    static {
        commondMap.put(TYPE.USER.name(), new UserProcessor());
        commondMap.put(TYPE.PWD.name(), new PwdProcessor());
        commondMap.put(TYPE.LIST.name(), new ListProcessor());
        commondMap.put(TYPE.QUIT.name(), new QuitProcessor());
        commondMap.put(TYPE.PASV.name(), new PASVProcessor());
        commondMap.put(TYPE.DOWN.name(), new DownloadProcessor());
        commondMap.put(TYPE.UPLOAD.name(), new UploadProcessor());
    }

    public static class StringUtils{
        public static boolean isBlank(String str){
            return "".equals(str) || " ".equals(str) || str.split("\\s+").length == 0;
        }
        public static boolean isEmpty(String str){
            return isBlank(str) || str == null || str.length() == 0;
        }
    }
    public static class IOUtils {
        public static void close(Closeable... closeables){
            for(Closeable closeable : closeables){
                if(closeable != null){
                    try {
                        closeable.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static class FileUntils{
        public static void mkdir(String path){
            File file = new File(path);
            if(!file.exists()){
                file.mkdirs();
            }
        }
    }
}
