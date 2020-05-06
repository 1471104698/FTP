package cn.oy.test.utils;

import cn.oy.test.processor.*;
import cn.oy.test.processor.impl.*;
import cn.oy.test.model.Type;

import java.io.*;
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
        commondMap.put(Type.USER.name(), new UserProcessor());
        commondMap.put(Type.PWD.name(), new PwdProcessor());
        commondMap.put(Type.LIST.name(), new ListProcessor());
        commondMap.put(Type.QUIT.name(), new QuitProcessor());
        commondMap.put(Type.PASV.name(), new PASVProcessor());
        commondMap.put(Type.DOWN.name(), new DownloadProcessor());
        commondMap.put(Type.UPLOAD.name(), new UploadProcessor());
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
        /**
         * 创建文件夹
         * @param path
         */
        public static void mkdir(String path){
            File file = new File(path);
            if(!file.exists()){
                file.mkdirs();
            }
        }

        /**
         * 获取文件夹下所有的文件，不包括文件夹
         * @param file
         * @return
         */
        public static File[] getFiles(File file){
            return file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isFile();
                }
            });
        }

        /**
         * 传入 输入流 和 输出流， 进行文件的读写
         * @param is
         * @param os
         */
        public static void rwFile(InputStream is, OutputStream os){
            try {
                int len = 0;
                byte[] flush = new byte[1024];
                while((len = is.read(flush)) != -1){
                    os.write(flush, 0, len);
                    os.flush();
                }
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 传入 输入流 和 输出流，以及文件的大小，对文件进行限定读写
         * @param is
         * @param sum
         * @param os
         */
        public static void rwFileByLimit(InputStream is, long sum, OutputStream os) {
            try {
                //获取本地输出流，用来写文件

                //进行文件传输
                int len = 0;
                byte[] flush = new byte[1024];
                while ((len = is.read(flush)) != -1) {
                    os.write(flush, 0, len);
                    sum -= len;
                    if(sum == 0){
                        break;
                    }
                }
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
