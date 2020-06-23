package cn.oy.test.processor.impl;

import cn.oy.test.processor.Commond;
import cn.oy.test.io.FTPServer;
import cn.oy.test.model.Order;

import java.io.File;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

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
        String path = simplifyPath(order.getMsg());
        if(path.startsWith("\\") || path.startsWith("/")){
            path = ftpServer.getPath() + path + File.separator;
        }else{
            path = ftpServer.getCur_path() + path + File.separator;
        }
        ftpServer.sendLineUTF("目录切换成功");
        ftpServer.setCur_path(path);
    }

    /**
     * 简化路径
     * @param path
     * @return
     */
    public String simplifyPath(String path) {
        String[] paths = path.split("/");
        Deque<String> stack = new LinkedList<>();
        for(String pa : paths){
            if(".".equals(pa) || "".equals(pa)){
                continue;
            }
            if("..".equals(pa)){
                if(!stack.isEmpty()){
                    stack.pop();
                }
                continue;
            }
            stack.push(pa);
        }
        if(stack.isEmpty()){
            return "/";
        }
        List<String> res = new ArrayList<>();
        while(!stack.isEmpty()){
            res.add(stack.pop());
        }
        StringBuilder sb = new StringBuilder();
        for(int i = res.size() - 1; i >= 0; i--){
            sb.append("/").append(res.get(i));
        }
        return sb.toString();
    }
}
