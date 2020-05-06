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
public class UploadProcessor implements Commond {
    @Override
    public void commond(Order order, FTPServer ftpServer) {
        //获取文件名称
        String fileName = order.getMsg();

        String path = ftpServer.getPath() + File.separator + fileName;

        ToolUtils.FileUntils.readFile(ftpServer, path);

        ftpServer.sendLineUTF("传输完成");
    }
}
