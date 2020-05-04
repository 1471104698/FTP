package cn.oy.test.processor.impl;

import cn.oy.test.processor.Commond;
import cn.oy.test.io.FTPServer;
import cn.oy.test.model.Order;
import cn.oy.test.model.Result;

/**
 * @author 蒜头王八
 * @project: ftp
 * @Description:
 * @Date 2020/5/4 9:58
 */
public class PwdProcessor implements Commond {
    @Override
    public void commond(Order order, FTPServer ftpServer) {
        if(!"root".equals(order.getMsg())){
            ftpServer.sendLine(Result.error("密码错误，请重新输入"));
        }else{
            ftpServer.sendLine(Result.ok("登录成功"));
        }
    }
}
