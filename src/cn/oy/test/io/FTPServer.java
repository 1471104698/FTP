package cn.oy.test.io;

import cn.oy.test.model.Order;
import cn.oy.test.model.Result;
import cn.oy.test.utils.ToolUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author 蒜头王八
 * @project: ftp
 * @Description:
 * @Date 2020/5/2 15:31
 */
public class FTPServer extends FTP<Result, Order> {
    private Logger logger = Logger.getLogger(FTPServer.class);

    //服务端，这里的服务端跟 StartServer 的不同，用于跟 客户端数据传输的 socket 通信
    private ServerSocket server = null;

    //FTP 服务器路径
    private String path = "F:" + File.separator + "FTP临时服务器" + File.separator;
    private String cur_path = path;

    // private static String path = "F:" + File.separator;

    public FTPServer(Socket msgSocket) throws IOException {
        super(msgSocket);
        super.writer = new ObjectOutputStream(getMsgSocket().getOutputStream());
        super.reader = new ObjectInputStream(getMsgSocket().getInputStream());
    }

    public void run() {
        //判断 FTP 服务器文件夹路径是否存在，如果不存在，那么进行创建
        ToolUtils.FileUntils.mkdir(path);

        //客户端连接到服务端后，那么获取客户端发来的消息
        while (true) {
            readLine();
            System.out.println("收到指令：" + msg);
            //直接使用 map 获取对应的处理类，方便扩展
            ToolUtils.commondMap.get(msg.getType()).commond(msg, this);
            if ("QUIT".equals(msg.getType())) {
                break;
            }
        }
    }


    public void setServer(ServerSocket server) {
        this.server = server;
    }

    public ServerSocket getServer() {
        return server;
    }

    public String getPath() {
        return path;
    }

    public String getCur_path() {
        return cur_path;
    }

    public void setCur_path(String cur_path) {
        this.cur_path = cur_path;
    }
}
