package cn.oy.test.processor.impl;

import cn.oy.test.processor.Commond;
import cn.oy.test.io.FTPServer;
import cn.oy.test.model.Order;
import cn.oy.test.model.Result;

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
        File[] files = list(FTPServer.getPath());
        assert files != null;
        StringBuilder sb = new StringBuilder();
        sb.append("总共有 ").append(files.length).append(" 个文件/文件夹\r\n");
        for(File f : files){
            System.out.println(f.exists());
            sb.append(f.getName()).append(" ").append(f.length()).append("字节").append("\r\n");
        }
        ftpServer.sendLine(Result.ok(sb.toString()));
    }
    /**
     * 获取服务器文件列表
     * @return
     */
    public File[] list(String filePath){
        File file = new File(filePath);
        if(!file.exists()){
            return  null;
        }
        return file.listFiles();
    }
}
