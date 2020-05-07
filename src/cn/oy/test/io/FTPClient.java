package cn.oy.test.io;

import cn.oy.test.constant.ConfigContanst;
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
public class FTPClient extends FTP<Order, Result> {


    private static Logger logger = Logger.getLogger(FTPClient.class);

    static Scanner scanner = new Scanner(System.in);

    //下载服务器文件名
    String downFilePath = "";

    //上传的本地文件名
    String uploadFilePath = "";

    //本地路径
    // String path = "F:" + File.separator + "FTP临时文件夹" + File.separator;
    String path = "F:" + File.separator;

    //操作指令
    String op = "";

    public FTPClient() throws IOException {
        super(new Socket(ConfigContanst.SERVER_IP, ConfigContanst.SERCER_PORT));
        reader = new ObjectInputStream(getMsgSocket().getInputStream());
        writer = new ObjectOutputStream(getMsgSocket().getOutputStream());
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
        System.out.println(msg);

        System.out.println("输入密码：");
        while (true) {
            String pwd = scanner.nextLine();
            sendLine(Order.pwd(pwd));
            readLine();
            assert msg != null;
            System.out.println(msg);
            if (msg.getCode() != 500) {
                break;
            }
        }
        op();
    }

    private void op() {
        //输入指令：LIST、UPLOAD
        while (true) {
            System.out.println("输入操作指令：");
            op = scanner.nextLine();
            switch (op) {
                case "LIST":
                    list();
                    break;
                case "UPLOAD":
                    upload();
                    break;
                case "DOWN":
                    downland();
                    break;
                case "CD":
                    cd();
                    break;
                case "q":
                    closeFTP();
                    return;
                default:
                    System.out.println("指令错误，请重新输入");
            }
        }
    }

    /**
     * 关闭连接
     */
    private void closeFTP() {
        sendLine(Order.quit());
        readLine();
        System.out.println(msg);
        ToolUtils.IOUtils.close(getMsgSocket());
    }

    private void list(){
        System.out.println("输入查看的路径");
        //发送查看列表指令
        sendLine(Order.list(scanner.nextLine()));
        readLine();
        if(msg.getCode() == 500){
            System.out.println(msg.getMsg());
            return;
        }
        System.out.println(readLineUTF());
    }

    /**
     * cd FTP 服务器文件路径
     */
    private void cd(){
        System.out.println("输入 cd 的路径（ps: 开头 / 为绝对路径）");
        sendLine(Order.cd(scanner.nextLine()));
        //接收服务端返回的消息
        System.out.println(readLineUTF());
    }

    /**
     * 被动模式
     */
    private void pasv() {
        //进行 pasv 肯定需要进行文件传输，那么需要判断是否存在本地目录文件夹
        ToolUtils.FileUntils.mkdir(path);

        //发送 PASV 请求给服务端
        sendLine(Order.pasv());
        //获取服务端端口号
        readLine();
        int port = Integer.parseInt(msg.getMsg());
        try {
            //连接服务端
            dataSocket = new Socket(ConfigContanst.SERVER_IP, port);
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
        uploadFilePath = scanner.nextLine();
        File file = new File(path + uploadFilePath);
        if (!file.exists()) {
            System.out.println("文件/文件夹不存在");
            return;
        }
        //发送上传文件指令
        sendLine(Order.upload(uploadFilePath));

        ToolUtils.FileUntils.writeFile(this, file);
        System.out.println(readLineUTF());
    }

    /**
     * 下载文件
     */
    private void downland() {
        //建立 pasv 连接
        pasv();

        System.out.println("输入要下载的文件/文件夹：");
        downFilePath = scanner.nextLine();
        //将文件名发送给服务端，表示要下载某个文件，让服务通知是否存在该文件，如果存在，那么返回下载，如果不存在，那么直接 return
        sendLine(Order.down(downFilePath));
        readLine();
        System.out.println(msg);
        if (msg.getCode() == 500) {
            System.out.println("下载的文件不存在");
            return;
        }
        /*
        这里组合成 F:\\(下载的文件名/文件夹)
        如果是文件名，比如 downFile = B站.txt，那么 pa = F:\\B站.txt
        如果是文件夹，比如 downFile = 书籍，那么 pa = F:\\书籍
         */
        File file = new File(path + downFilePath);
        if (file.exists()) {
            System.out.println("文件已经存在");
            return;
        }
        //下载文件
        ToolUtils.FileUntils.readFile(this, path, downFilePath);
        System.out.println(readLineUTF());
    }
}
