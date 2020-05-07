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
        String path = ftpServer.getPath() + fileName;
        File file = new File(path);
        //判断文件是否存在
        if (!file.exists()) {
            ftpServer.sendLine(Result.error("文件不存在"));
            return;
        }
        ftpServer.sendLine(Result.ok("开始文件传输..."));
        ToolUtils.FileUntils.writeFile(ftpServer, file);
        ftpServer.sendLineUTF("传输完成");
    }
}
