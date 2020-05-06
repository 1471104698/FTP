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
        try {
            InputStream is = ftpServer.getDataSocket().getInputStream();
            //获取文件名称
            String fileName = order.getMsg();
            String path = ftpServer.getPath() + File.separator + fileName;
            //获取文件类型
            String type = ftpServer.readLineUTF();
            if ("file".equals(type)) {
                ToolUtils.FileUntils.rwFile(is, new FileOutputStream(path));
            } else {
                ToolUtils.FileUntils.mkdir(path);
                //获取文件个数
                int numbers = Integer.parseInt(ftpServer.readLineUTF());
                while (numbers-- > 0) {
                    //获取文件名称
                    fileName = ftpServer.readLineUTF();
                    //获取文件大小
                    long sum = Long.parseLong(ftpServer.readLineUTF());

                    String pa = path + File.separator + fileName;

                    ToolUtils.FileUntils.rwFileByLimit(is, sum, new FileOutputStream(pa));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ftpServer.sendLine(Result.ok("传输完成"));
    }
}
