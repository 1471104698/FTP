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


        String fileName = order.getMsg();
        String path = FTPServer.getPath() + File.separator + fileName;
        File file = new File(path);
        if(!file.exists()){
            ftpServer.sendLine(Result.error("文件不存在"));
            return;
        }
        ftpServer.sendLine(Result.ok("开始文件传输..."));
        if(file.isDirectory()){   //下载的是文件夹
            File[] files = file.listFiles();
            //告知客户端传输文件个数
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
                    Thread.sleep(500);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }else {   //下载的是文件，注意：如果文件路径是不存在的，那么 isDirectory() isFile() exists() 都是 false
            try {
                ftpServer.sendLine(Result.ok(String.valueOf(1)));
                //传输文件名
                ftpServer.sendLine(Result.ok(file.getName()));
                //传输文件大小
                ftpServer.sendLine(Result.ok(String.valueOf(file.length())));

                download(file, ftpServer.getDataSocket().getOutputStream());
                ftpServer.getDataSocket().shutdownOutput();
                ftpServer.sendLine(Result.ok("传输完成"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ToolUtils.IOUtils.close(ftpServer.getDataSocket());
    }
    private void download(File file, OutputStream os){
        try {
            FileInputStream fis = new FileInputStream(file);
            int len = 0;
            byte[] flush = new byte[1024];
            while((len = fis.read(flush)) != -1){
                os.write(flush, 0, len);
                os.flush();
            }
            ToolUtils.IOUtils.close(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
