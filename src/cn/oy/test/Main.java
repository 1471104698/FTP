package cn.oy.test;

import cn.oy.test.io.FTPClient;
import cn.oy.test.io.FTPServer;
import cn.oy.test.model.Order;
import cn.oy.test.utils.ToolUtils;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.junit.Test;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    public static String path = "F:" + File.separator + "书籍" + File.separator + "《图解HTTP》完整彩色版.pdf";

    public static void main(String[] args) throws InterruptedException, IOException {
        // write your code here
        InetSocketAddress inetAddress = new InetSocketAddress(0);
        ServerSocket socket = new ServerSocket();
        socket.bind(inetAddress);
        System.out.println(socket.getLocalPort());
    }

    @Test
    public void t1() throws IOException, ClassNotFoundException, InterruptedException {
        Socket socket = new Socket("localhost", 20);
        String pa = "F:" + File.separator + "书籍" + File.separator + "《图解HTTP》完整彩色版.pdf";
        down(socket, pa);
        // pa = "F:" + File.separator + "书籍" + File.separator + "How Tomcat Works.pdf";
        // down(socket, pa);
    }

    private void down(Socket socket, String path) throws IOException {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        OutputStream os = socket.getOutputStream();
        int len = 0;
        byte[] flush = new byte[1024];
        while((len = fis.read(flush)) != -1){
            os.write(flush, 0, len);
        }
        os.close();
        fis.close();
    }

    @Test
    public void t2()  {
        ServerSocket server = null;
        try {
            server = new ServerSocket(20);
            // 获取过程中会阻塞
            Socket socket = server.accept();
            while (true){
                FileOutputStream fos = new FileOutputStream("F:\\源码.pdf");
                InputStream is = socket.getInputStream();
                int len = 0;
                byte[] flush = new byte[1024];
                while((len = is.read(flush)) != -1){
                    fos.write(flush, 0, len);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
