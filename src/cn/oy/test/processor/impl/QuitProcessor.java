package cn.oy.test.processor.impl;

import cn.oy.test.processor.Commond;
import cn.oy.test.io.FTPServer;
import cn.oy.test.model.Order;
import cn.oy.test.model.Result;
import cn.oy.test.utils.ToolUtils;

/**
 * @author 蒜头王八
 * @project: ftp
 * @Description:
 * @Date 2020/5/4 11:49
 */
public class QuitProcessor implements Commond {
    @Override
    public void commond(Order order, FTPServer ftpServer) {
        ftpServer.sendLine(Result.ok("goodbye"));
        ToolUtils.IOUtils.close(ftpServer.getMsgSocket());
    }
}
