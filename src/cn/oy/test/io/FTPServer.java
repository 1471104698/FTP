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
public class FTPServer extends Thread {
    private Logger logger = Logger.getLogger(FTPServer.class);

    //
    private Socket msgSocket;
    //用于文件传输
    private Socket dataSocket = null;
    //服务端，这里的服务端跟 StartServer 的不同，用于跟 客户端数据传输的 socket 通信
    private ServerSocket server = null;
    //客户端的消息实体
    private Order order = null;

    //客户端和服务端之间文字通信
    private ObjectInputStream reader = null;
    private ObjectOutputStream writer = null;

    //FTP 服务器路径
    // private static String path = "F:" + File.separator + "书籍";
    private static String path = "F:\\";

    public FTPServer(Socket msgSocket) {
        this.msgSocket = msgSocket;
        try {
            reader = new ObjectInputStream(msgSocket.getInputStream());
            writer = new ObjectOutputStream(msgSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //判断 FTP 服务器文件夹路径是否存在，如果不存在，那么进行创建
        ToolUtils.FileUntils.mkdir(path);

        //客户端连接到服务端后，那么获取客户端发来的消息
        while (true) {
            readLine();
            System.out.println("收到指令：" + order);
            //直接使用 map 获取对应的处理类，方便扩展
            ToolUtils.commondMap.get(order.getType()).commond(order, this);
            if ("QUIT".equals(order.getType())) {
                break;
            }
        }
    }

    /**
     * 发送消息给服务端
     *
     * @param result
     */
    public void sendLine(Result result) {
        try {
            writer.writeObject(result);
            writer.flush();
        } catch (IOException e) {
            // logger.error("发送消息到客户端失败");
            System.out.println("发送消息到客户端失败");
        }
    }

    /**
     * 读取服务器返回的信息
     *
     * @return
     */
    private void readLine() {
        try {
            order = (Order) reader.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            // logger.error("读取客户端消息错误");
            System.out.println("读取客户端消息错误");
        }
    }

    public void setDataSocket(Socket dataSocket) {
        this.dataSocket = dataSocket;
    }

    public void setServer(ServerSocket server) {
        this.server = server;
    }

    public ServerSocket getServer() {
        return server;
    }

    public Socket getMsgSocket() {
        return msgSocket;
    }

    public Socket getDataSocket() {
        return dataSocket;
    }

    public Order getOrder() {
        return order;
    }

    public static String getPath() {
        return path;
    }
}
