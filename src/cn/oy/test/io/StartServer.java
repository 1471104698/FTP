package cn.oy.test.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 蒜头王八
 * @project: ftp
 * @Description:
 * @Date 2020/5/3 11:37
 */
public class StartServer extends Thread {

    public static void main(String[] args) {
        //连接池
        ExecutorService threadPool = Executors.newFixedThreadPool(100);
        try {
            //此处的 ss 仅仅用于接收客户端连接的 socket，不与客户端进行通信
            ServerSocket server = new ServerSocket(20);
            while (true){
                // 获取过程中会阻塞
                Socket client = server.accept();
                FTPServer ftpServer = new FTPServer(client);
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        ftpServer.run();
                    }
                };
                threadPool.execute(runnable);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
