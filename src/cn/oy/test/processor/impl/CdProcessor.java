package cn.oy.test.processor.impl;

import cn.oy.test.processor.Commond;
import cn.oy.test.io.FTPServer;
import cn.oy.test.model.Order;

import java.io.File;

/**
 * @author 蒜头王八
 * @project: ftp
 * @Description:
 * @Date 2020/5/4 10:59
 */
public class CdProcessor implements Commond {
    @Override
    public void commond(Order order, FTPServer ftpServer) {
        //获取 cd 的目录
        String path = order.getMsg();
        if(path.startsWith("\\") || path.startsWith("/")){
            path = ftpServer.getPath() + path + File.separator;
        }else{
            path = ftpServer.getCur_path() + path + File.separator;
        }
        ftpServer.sendLineUTF("目录切换成功");
        ftpServer.setCur_path(path);
    }
}
