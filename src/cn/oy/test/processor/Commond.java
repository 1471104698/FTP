package cn.oy.test.processor;

import cn.oy.test.io.FTPServer;
import cn.oy.test.model.Order;

/**
 * @author 蒜头王八
 * @project: ftp
 * @Description:
 * @Date 2020/5/4 9:46
 */
public interface Commond {
    public void commond(Order order, FTPServer ftpServer);
}
