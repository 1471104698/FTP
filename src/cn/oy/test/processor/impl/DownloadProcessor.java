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
        String path = ftpServer.getPath() + File.separator + fileName;
        File file = new File(path);
        //判断文件是否存在
        if (!file.exists()) {
            ftpServer.sendLine(Result.error("文件不存在"));
            return;
        }
        ftpServer.sendLine(Result.ok("开始文件传输..."));
        download(ftpServer, file);
    }

    private void download(FTPServer ftpServer, File file) {
        try {
            OutputStream os = ftpServer.getDataSocket().getOutputStream();
            //如果是文件夹，那么获取文件夹内的所有文件，并传输给客户端
            if (file.isDirectory()) {
                ftpServer.sendLine(Result.ok("directory"));
                //只获取文件，不获取文件夹
                File[] files = ToolUtils.FileUntils.getFiles(file);
                //告知客户端传输文件个数
                assert files != null;
                ftpServer.sendLine(Result.ok(String.valueOf(files.length)));
                //再分别传输文件（这里过滤了文件夹，如果需要处理文件夹，那么需要递归处理，这里省略）
                for (File f : files) {
                    //传输文件名
                    ftpServer.sendLine(Result.ok(f.getName()));
                    //传输文件大小
                    ftpServer.sendLine(Result.ok(String.valueOf(f.length())));
                    ToolUtils.FileUntils.transFile(new FileInputStream(f), os);
                    Thread.sleep(500);
                }
            } else {   //下载的是文件，注意：如果文件路径是不存在的，那么 isDirectory() isFile() exists() 都是 false
                ftpServer.sendLine(Result.ok("file"));
                ToolUtils.FileUntils.transFile(new FileInputStream(file), os);
            }
            ToolUtils.IOUtils.close(ftpServer.getDataSocket());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        ftpServer.sendLine(Result.ok("传输完成"));
    }

}
