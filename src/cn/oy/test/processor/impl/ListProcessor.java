package cn.oy.test.processor.impl;

import cn.oy.test.processor.Commond;
import cn.oy.test.io.FTPServer;
import cn.oy.test.model.Order;
import cn.oy.test.model.Result;
import cn.oy.test.utils.ToolUtils;

import java.io.File;

/**
 * @author 蒜头王八
 * @project: ftp
 * @Description:
 * @Date 2020/5/4 9:50
 */
public class ListProcessor implements Commond {

    @Override
    public void commond(Order order, FTPServer ftpServer) {
        String path = order.getMsg();
        if(path.startsWith("/") || path.startsWith("\\")){
            path = ftpServer.getPath() + path;
        }else{
            path = ftpServer.getCur_path() + path;
        }
        File file = new File(path);
        if(!file.isDirectory()){
            ftpServer.sendLine(Result.error("没有此文件或文件夹"));
            return;
        }
        ftpServer.sendLine(Result.ok("ok"));
        File[] files = file.listFiles();
        assert files != null;
        StringBuilder sb = new StringBuilder();

        sb.append("总共有 ").append(files.length).append(" 个 文件/文件夹\r\n");
        for(File f : files){
            sb.append(f.getName());
            if(f.isFile()){
                sb.append(" ").append(f.length()).append("字节");
            }
            sb.append("\r\n");
        }
        ftpServer.sendLineUTF(sb.toString());
    }
}
