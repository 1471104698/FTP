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
 * @Date 2020/5/4 9:51
 */
public class UserProcessor implements Commond {

    @Override
    public void commond(Order order, FTPServer ftpServer) {
        if(ToolUtils.StringUtils.isEmpty(order.getMsg())){
            ftpServer.sendLine(Result.error("认证错误"));
        }else{
            System.out.println("收到客户端账号为 : " + order.getMsg());
            ftpServer.sendLine(Result.ok("账号记录成功"));
        }
    }
}
