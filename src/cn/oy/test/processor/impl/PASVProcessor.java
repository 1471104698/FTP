package cn.oy.test.processor.impl;

import cn.oy.test.io.FTPServer;
import cn.oy.test.model.Order;
import cn.oy.test.model.Result;
import cn.oy.test.processor.Commond;

import java.io.IOException;
import java.net.*;

/**
 * @author 蒜头王八
 * @project: ftp
 * @Description:
 * @Date 2020/5/4 16:33
 */
public class PASVProcessor implements Commond {

    @Override
    public void commond(Order order, FTPServer ftpServer) {
        //创建新的 数据传输服务端并接收新的数据传输客户端
        /*
        关于端口号：
        一般用到的是1到65535,其中0不使用,1-1023为系统端口,也叫BSD保留端口;1024-65535为用户端口,又分为: BSD临时端口(1024-5000)和BSD服务器(非特权)端口(5001-65535).
          0-1023: BSD保留端口,也叫系统端口,这些端口只有系统特许的进程才能使用;
          1024-5000: BSD临时端口,一般的应用程序使用1024到4999来进行通讯;
          5001-65535: BSD服务器(非特权)端口,用来给用户自定义端口.
         随机获取端口号的一般方法：
            取[1024, 65536)之间的一个随机数
            循环取[1024, 65536)进行bind，直到bind成功为止

            第一种方法，无法保证随机获取的端口是未占用的
            第二种方法，不停的尝试bind，效率很低

         最好的方法：
         使用 InetSocketAddress inetAddress = new InetSocketAddress(0); ，传入 0 端口，默认会返回一个空闲的端口
         socket.bind(inetAddress) 进行绑定即可
     */
        System.out.println("进入 PASV 模式");
        //随机获取一个空闲的端口号
        InetSocketAddress inetAddress = new InetSocketAddress(0);

        try {
            ServerSocket server = new ServerSocket();
            ftpServer.setServer(server);
            server.bind(inetAddress);
            //将端口号发送给客户端
            ftpServer.sendLine(Result.ok(String.valueOf(server.getLocalPort())));
            //获取客户端数据传输的 socket 连接
            Socket dataSocket = server.accept();
            ftpServer.setDataSocket(dataSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
