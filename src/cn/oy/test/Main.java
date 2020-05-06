package cn.oy.test;

import cn.oy.test.utils.ToolUtils;
import org.junit.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static String path = "F:" + File.separator + "书籍" + File.separator + "Java核心技术++卷1++基础知识++原书第10版--中文版扫描--带书签已OCR.pdf";

    public static void main(String[] args) throws InterruptedException, IOException {
        // write your code here
        File file = new File("F:\\");
        File[] files = file.listFiles();
        StringBuilder sb = new StringBuilder();
        assert files != null;
        sb.append("总共有 ").append(files.length).append(" 个文件/文件夹\r\n");
        for(File f : files){
            sb.append(f.getName()).append(" ").append(f.length()).append("字节").append("\r\n");
        }
        System.out.println(sb.toString());
    }

    @Test
    public void t1() throws IOException, ClassNotFoundException, InterruptedException {
        Socket socket = new Socket("localhost", 20);
        down(socket);
    }

    private void down(Socket socket) throws IOException {

        InputStream is = socket.getInputStream();
        DataInputStream dis = new DataInputStream(is);
        //获取文件个数
        int number = Integer.parseInt(dis.readUTF());
        while (number-- > 0) {

            //获取文件名称
            String fileName = dis.readUTF();
            //获取文件大小
            long sum = Long.parseLong(dis.readUTF());

            //获取本地输出流，用来写文件
            FileOutputStream fos = new FileOutputStream("F:\\FTP临时文件夹\\" + fileName);

            ToolUtils.FileUntils.rwFileByLimit(is, sum, fos);
        }
        socket.close();
    }

    @Test
    public void t2() {
        try {
            ServerSocket server = new ServerSocket(20);
            // 获取过程中会阻塞
            while (true) {
                Socket socket = server.accept();
                dowmload(socket);
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dowmload(Socket socket) throws IOException {
        OutputStream os = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(os);

        //服务端文件下载的路径
        String pa = "F:" + File.separator + "书籍";
        File file = new File(pa);
        File[] files = file.listFiles();
        //发送文件个数
        assert files != null;
        dos.writeUTF(String.valueOf(files.length));
        for (File f : files) {
            //获取本地输入流，读取文件
            FileInputStream fis = new FileInputStream(f);
            //发送文件名称
            dos.writeUTF(f.getName());
            //发送文件大小
            dos.writeUTF(String.valueOf(f.length()));
            ToolUtils.FileUntils.rwFile(fis, os);
            fis.close();
        }
    }
}
