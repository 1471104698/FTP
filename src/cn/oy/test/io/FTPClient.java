package cn.oy.test.io;

import cn.oy.test.model.Order;
import cn.oy.test.model.Result;
import cn.oy.test.utils.ToolUtils;
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
public class FTPClient implements FTP{
    public static void main(String[] args) {
        String path = "F:" + File.separator;

        FTPClient ftpClient = new FTPClient();
        //发送读取文件列表命令
        ftpClient.sendLine(Order.list());
        ftpClient.readLine();
        System.out.println(ftpClient.result.getMsg());
        //进入数据传输模式
        FTPClient.path = path;
        // ftpClient.downland();
        ftpClient.upload();
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

    DataInputStream dreader = null;
    DataOutputStream dwriter = null;

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
            dreader = new DataInputStream(msgSocket.getInputStream());
            dwriter = new DataOutputStream((msgSocket.getOutputStream()));
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

    /**
     * 关闭连接
     */
    private void closeFTP(){
        sendLine(Order.quit());
        readLine();
        System.out.println(result);
        ToolUtils.IOUtils.close(msgSocket);
    }

    /**
     * 被动模式
     */
    private void pasv(){
        //进行 pasv 肯定需要进行文件传输，那么需要判断是否存在本地目录文件夹
        ToolUtils.FileUntils.mkdir(path);

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
     * 上传文件到服务器
     */
    private void upload() {

        pasv();
        System.out.println("输入要上传的文件/文件夹");
        uploadFile = scanner.nextLine();
        File file = new File(path + File.separator + uploadFile);
        if(!file.exists()){
            System.out.println("文件/文件夹不存在");
            return;
        }
        sendLine(Order.upload(uploadFile));
        if(file.isDirectory()){
            sendLineUTF("directory");
            uploadMultiFiles(file);
        }else{
            sendLineUTF("file");
            uploadSingleFile(file);
        }
        ToolUtils.IOUtils.close(dataSocket);
        readLine();
        System.out.println(result.getMsg());
    }

    /**
     * 上传多个文件
     */
    private void uploadMultiFiles(File file){
        //告知服务器是文件夹（即存在多文件）
        //获取所有的文件
        File[] files = ToolUtils.FileUntils.getFiles(file);

        //告知文件个数
        sendLineUTF(String.valueOf(files.length));
        for(File f : files){
            //告知文件名称
            sendLineUTF(f.getName());
            //告知文件大小
            sendLineUTF(String.valueOf(f.length()));
            //进行文件传输
            uploadSingleFile(f);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 上传单个文件
     */
    private void uploadSingleFile(File f){
        try{
            FileInputStream fis = new FileInputStream(f);
            OutputStream os = dataSocket.getOutputStream();
            ToolUtils.FileUntils.transFile(fis, os);
        } catch (IOException e) {
            // logger.error("上传文件失败");
            System.out.println("上传文件失败");
        }
    }

    /**
     * 下载文件
     */
    private void downland() {
        //建立 pasv 连接
        pasv();

        System.out.println("输入要下载的文件/文件夹：");
        downFile = scanner.nextLine();
        //将文件名发送给服务端，表示要下载某个文件，让服务通知是否存在该文件，如果存在，那么返回下载，如果不存在，那么直接 return
        sendLine(Order.down(downFile));
        readLine();
        System.out.println(result);
        if(result.getCode() == 500){
            return;
        }
        File file = new File(path + File.separator + downFile);
        if(file.exists()){
            System.out.println("文件已经存在");
            return;
        }
        //获取服务端返回的通知：文件 或 文件夹
        readLine();
        if("file".equals(result.getMsg())){
            downloadSingleFile();
        }else{
            downloadMultiFiles();
        }
        readLine();
        System.out.println(result.getMsg());
        ToolUtils.IOUtils.close(dataSocket);
    }

    /**
     * 多个文件下载
     * @throws IOException
     */
    private void downloadMultiFiles() {
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
                String pa = path + File.separator + fileName;
                ToolUtils.FileUntils.transMultiFiles(is, sum, pa);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 单个文件下载
     */
    private void downloadSingleFile(){
        try {
            String pa = path + File.separator + downFile;
            //获取本地输出流，用来写文件
            FileOutputStream fos = new FileOutputStream(pa);
            InputStream is = dataSocket.getInputStream();
            ToolUtils.FileUntils.transFile(is, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息实体 Order 给服务端
     *
     * @param order
     */
    private void sendLine(Order order) {
        try {
            writer.writeObject(order);
            writer.flush();
        } catch (IOException e) {
            // logger.error("发送消息到服务器失败");
            System.out.println("发送消息到服务器失败");
        }
    }

    /**
     * 发送消息给服务端
     *
     * @param str
     */
    private void sendLineUTF(String str) {
        try {
            dwriter.writeUTF(str);
            dwriter.flush();
        } catch (IOException e) {
            // logger.error("发送消息到服务器失败");
            System.out.println("发送消息到服务器失败");
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
            // logger.error("读取服务器消息错误");
            System.out.println("读取服务器消息错误");
        }
    }
    /**
     * 读取服务器返回的信息
     * @return
     */
    private String readLineUTF(){
        try {
            return dreader.readUTF();
        } catch (IOException e) {
            // logger.error("读取服务器消息错误");
            System.out.println("读取服务器消息错误");
        }
        return "";
    }
}
