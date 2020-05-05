package cn.oy.test.io;

import cn.oy.test.model.Order;
import cn.oy.test.model.Result;
import cn.oy.test.utils.ToolUtils;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * @author 蒜头王八
 * @project: ftp
 * @Description:
 * @Date 2020/5/1 22:01
 */
public class FTPClient {
    public static void main(String[] args) {
        FTPClient ftpClient = new FTPClient();
        //发送读取文件列表命令
        ftpClient.sendLine(Order.list());
        ftpClient.readLine();
        System.out.println(ftpClient.result.getMsg());
        //进入数据传输模式
        ftpClient.downland();
        //停止连接,关闭 socket
        ftpClient.closeFTP();
    }

    private static Logger logger = Logger.getLogger(FTPClient.class);

    //服务端 ip
    static String SERVER_IP = "localhost";
    //服务端端口
    static int SERCER_PORT = 20;

    static Scanner scanner = new Scanner(System.in);

    Socket msgSocket = null;

    //用于数据传输的 socket
    Socket dataSocket = null;

    //客户端和服务端之间文字通信
    ObjectInputStream reader = null;
    ObjectOutputStream writer = null;

    //服务端响应消息实体
    Result result;

    //下载服务器文件名
    String downFile = "";

    //上传的本地文件名
    String uploadFile = "";

    //本地路径
    static String path = "F:" + File.separator + "FTP临时文件夹";

    public FTPClient() {
        try {
            msgSocket = new Socket(SERVER_IP, SERCER_PORT);
            writer = new ObjectOutputStream(msgSocket.getOutputStream());
            reader = new ObjectInputStream(msgSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("连接服务器失败");
        }
        connect();
    }


    /**
     * 连接服务端,输入用户密码
     */
    private void connect() {
        //输入账户和密码
        System.out.println("输入账号：");
        String user = scanner.nextLine();
        //发送给服务端，空格隔开
        sendLine(Order.user(user));
        //接收服务端的响应，判断账号是否录入成功
        readLine();
        System.out.println(result);

        System.out.println("输入密码：");
        while(true){
            String pwd = scanner.nextLine();
            sendLine(Order.pwd(pwd));
            readLine();
            assert result != null;
            System.out.println(result);
            if(result.getCode() != 500){
                break;
            }
        }

        //输入指令：LIST、CMD
    }

    private void closeFTP(){
        sendLine(Order.quit());
        readLine();
        System.out.println(result);
        ToolUtils.IOUtils.close(msgSocket);
    }


    /**
     * 上传文件到服务器
     * @param filePath
     */
    private void upload(String filePath) {

        pasv();
        try(
                FileInputStream fis = new FileInputStream(filePath);
                OutputStream os = dataSocket.getOutputStream()
        ) {
            int len = 0;
            byte[] flush = new byte[1024];
            while((len = fis.read(flush)) != -1){
                os.write(flush, 0, len);
                os.flush();
            }
            ToolUtils.IOUtils.close(fis, os, dataSocket);
        } catch (IOException e) {
            logger.error("上传文件失败");
        }
    }

    private void pasv(){
        //进行 pasv 肯定需要进行文件传输，那么需要判断是否存在本地目录文件夹
        mkdir();

        //发送 PASV 请求给服务端
        sendLine(Order.pasv());
        //获取服务端端口号
        readLine();
        int port = Integer.parseInt(result.getMsg());
        try {
            //连接服务端
            dataSocket = new Socket(SERVER_IP, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载文件
     */
    private void downland() {
        //建立 pasv 连接
        pasv();

        System.out.println("输入要下载的文件名：");
        downFile = scanner.nextLine();
        //将文件名发送给服务端，表示要下载某个文件，让服务通知是否存在该文件，如果存在，那么返回下载，如果不存在，那么直接 return
        sendLine(Order.down(downFile));
        readLine();
        System.out.println(result);
        if(result.getCode() == 500){
            return;
        }
        try {
            InputStream is = dataSocket.getInputStream();
            //获取文件个数
            readLine();
            int number = Integer.parseInt(result.getMsg());
            while (number-- > 0){

                //获取文件名称
                readLine();
                String fileName = result.getMsg();
                //获取文件大小
                readLine();
                long sum = Long.parseLong(result.getMsg());

                //获取本地输出流，用来写文件
                FileOutputStream fos = new FileOutputStream(path + File.separator + fileName);

                //进行文件传输
                int len = 0;
                byte[] flush = new byte[1024];
                while ((len = is.read(flush)) != -1) {
                    fos.write(flush, 0, len);
                    sum -= len;
                    if(sum == 0){
                        break;
                    }
                }
                fos.close();
            }
            dataSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建文件夹
     */
    private void mkdir(){
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    /**
     * 发送消息给服务端
     *
     * @param order
     */
    private void sendLine(Order order) {
        try {
            writer.writeObject(order);
            writer.flush();
        } catch (IOException e) {
            logger.error("发送消息到服务器失败");
        }
    }

    /**
     * 读取服务器返回的信息
     * @return
     */
    private void readLine(){
        try {
            result = (Result) reader.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("读取服务器消息错误");
        }
    }
}
