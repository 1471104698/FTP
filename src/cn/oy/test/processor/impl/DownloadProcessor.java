package cn.oy.test.processor.impl;

import cn.oy.test.model.Result;
import cn.oy.test.processor.Commond;
import cn.oy.test.io.FTPServer;
import cn.oy.test.model.Order;
import cn.oy.test.utils.ToolUtils;

import java.io.*;

/**
 * @author 蒜头王八
 * @project: ftp
 * @Description:
 * @Date 2020/5/4 14:54
 */
public class DownloadProcessor implements Commond {
    @Override
    public void commond(Order order, FTPServer ftpServer) {


        String path = order.getMsg();
        File file = new File(path);
        //下载的是文件夹
        if(file.isDirectory()){
            File[] files = file.listFiles();
            //先给客户端传输文件个数
            assert files != null;
            ftpServer.sendLine(Result.ok(String.valueOf(files.length)));
            //再分别传输文件
            for(File f : files){
                //传输文件名
                ftpServer.sendLine(Result.ok(f.getName()));
                //传输文件大小
                ftpServer.sendLine(Result.ok(String.valueOf(f.length())));
                try {
                    download(f, ftpServer.getDataSocket().getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else if(file.exists()){   //下载的是文件，注意：如果文件路径是不存在的，那么 isDirectory() isFile() exists() 都是 false
            try {
                download(file, ftpServer.getDataSocket().getOutputStream());
                ftpServer.sendLine(Result.ok("传输完成"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void download(File file, OutputStream os){
        try {
            FileInputStream fis = new FileInputStream(file);
            int len = 0;
            byte[] flush = new byte[1024];
            while((len = fis.read(flush)) != -1){
                os.write(flush, 0, len);
            }
            ToolUtils.IOUtils.close(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
