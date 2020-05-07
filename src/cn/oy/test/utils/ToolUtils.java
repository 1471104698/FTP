package cn.oy.test.utils;

import cn.oy.test.constant.ConfigContanst;
import cn.oy.test.io.FTP;
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
        commondMap.put(Type.CD.name(), new CdProcessor());
    }

    public static class StringUtils {
        public static boolean isBlank(String str) {
            return "".equals(str) || " ".equals(str) || str.split("\\s+").length == 0;
        }

        public static boolean isEmpty(String str) {
            return isBlank(str) || str == null || str.length() == 0;
        }
    }

    public static class IOUtils {
        public static void close(Closeable... closeables) {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    try {
                        closeable.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static class FileUntils {
        /**
         * 创建文件夹
         *
         * @param path
         */
        public static void mkdir(String path) {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        }

        /**
         * 获取文件夹下所有的文件，不包括文件夹
         *
         * @param file
         * @return
         */
        public static File[] getFiles(File file) {
            return file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isFile();
                }
            });
        }

        /**
         * 传入 输入流 和 输出流， 进行文件的读写，用于单个文件的传输
         *
         * @param is
         * @param os
         */
        private static void rwFile(InputStream is, OutputStream os) {
            try {
                int len;
                byte[] flush = new byte[1024];
                while ((len = is.read(flush)) != -1) {
                    os.write(flush, 0, len);
                    os.flush();
                }
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 传入 输入流 和 输出流，以及文件的大小，对文件进行限定读写，用于同时多个文件传输
         * 解决 接收端接收完一个文件，如果发送端不关闭 流 或 连接 就无法接收结束符，导致进入阻塞状态的问题
         *
         * @param is
         * @param sum
         * @param os
         */
        private static void rwFileByLimit(InputStream is, long sum, OutputStream os) {
            try {
                //获取本地输出流，用来写文件

                //进行文件传输
                int len;
                byte[] flush = new byte[1024];
                while ((len = is.read(flush)) != -1) {
                    os.write(flush, 0, len);
                    sum -= len;
                    if (sum == 0) {
                        break;
                    }
                }
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 从本地读取文件，然后写到网络字节流
         *
         * @param ftp
         * @param file
         */
        public static void writeFile(FTP ftp, File file) {
            try {
                OutputStream os = ftp.getDataSocket().getOutputStream();
                //如果是文件夹，那么获取文件夹内的所有文件，并传输给客户端
                if (file.isDirectory()) {
                    ftp.sendLineUTF(ConfigContanst.DIRECTORY_TYPE);
                    //只获取文件，不获取文件夹
                    File[] files = getFiles(file);
                    //告知客户端传输文件个数
                    ftp.sendLineUTF(String.valueOf(files.length));
                    //再分别传输文件（这里过滤了文件夹，如果需要处理文件夹，那么需要递归处理，这里省略）
                    for (File f : files) {
                        //传输文件名
                        ftp.sendLineUTF(f.getName());
                        //传输文件大小
                        ftp.sendLineUTF(String.valueOf(f.length()));
                        rwFile(new FileInputStream(f), os);
                        Thread.sleep(500);
                    }
                } else {
                    //下载的是文件，注意：如果文件路径是不存在的，那么 isDirectory() isFile() exists() 都是 false

                    //发送文件类型
                    ftp.sendLineUTF(ConfigContanst.FILE_TYPE);
                    rwFile(new FileInputStream(file), os);
                }
                ftp.getDataSocket().close();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * 从网络字节流读取文件，然后写到本地
         *
         * @param ftp
         * @param path
         */
        public static void readFile(FTP ftp, String path, String filePath) {
            //读取要接收的文件类型
            String type = ftp.readLineUTF();
            FileOutputStream os = null;
            try {
                InputStream is = ftp.getDataSocket().getInputStream();
                //如果是文件夹，那么需要处理多个文件
                if (ConfigContanst.DIRECTORY_TYPE.equals(type)) {
                    path += filePath + File.separator;
                    mkdir(path);
                    //获取文件个数
                    int number = Integer.parseInt(ftp.readLineUTF());

                    while (number-- > 0) {

                        //获取文件名称
                        String fileName = ftp.readLineUTF();
                        //获取文件大小
                        long sum = Long.parseLong(ftp.readLineUTF());
                        rwFileByLimit(is, sum, new FileOutputStream(path + fileName));
                    }
                } else {    //处理单文件
                    //获取输入的文件的路径
                    int index = filePath.lastIndexOf("\\");
                    if(index != -1){
                        String pa = filePath.substring(0, index);
                        mkdir(path + File.separator + pa);
                    }

                    os = new FileOutputStream(path + filePath);

                    rwFile(is, os);
                }
                ftp.getDataSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
