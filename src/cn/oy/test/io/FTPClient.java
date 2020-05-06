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
    public static void main(String[] args) throws IOException {
        new FTPClient();
    }

    private static Logger logger = Logger.getLogger(FTPClient.class);

    static Scanner scanner = new Scanner(System.in);

    //下载服务器文件名
    String downFile = "";

    //上传的本地文件名
    String uploadFile = "";

    //本地路径
    String path = "F:" + File.separator + "FTP临时文件夹";

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
                    sendLine(Order.list());
                    readLine();
                    System.out.println(msg.getMsg());
                    break;
                case "UPLOAD":
                    upload();
                    break;
                case "DOWN":
                    downland();
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
        uploadFile = scanner.nextLine();
        File file = new File(path + File.separator + uploadFile);
        if (!file.exists()) {
            System.out.println("文件/文件夹不存在");
            return;
        }
        //发送上传文件指令
        sendLine(Order.upload(uploadFile));

        ToolUtils.FileUntils.writeFile(this, file);
        System.out.println(readLineUTF());
    }

    /**
     * 上传多个文件
     */
    private void uploadMultiFiles(File file) {
        //告知服务器是文件夹（即存在多文件）
        //获取所有的文件
        File[] files = ToolUtils.FileUntils.getFiles(file);

        //告知文件个数
        sendLineUTF(String.valueOf(files.length));
        for (File f : files) {
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
    private void uploadSingleFile(File f) {
        try {
            FileInputStream fis = new FileInputStream(f);
            OutputStream os = dataSocket.getOutputStream();
            ToolUtils.FileUntils.rwFile(fis, os);
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
        System.out.println(msg);
        if (msg.getCode() == 500) {
            return;
        }
        /*
        这里组合成 F:\\(下载的文件名/文件夹)
        如果是文件名，比如 downFile = B站.txt，那么 pa = F:\\B站
        如果是文件夹，比如 downFile = 书籍，那么 pa = F:\\书籍
         */
        String pa = path + File.separator + downFile;

        File file = new File(pa);
        if (file.exists()) {
            System.out.println("文件已经存在");
            return;
        }
        //下载文件
        ToolUtils.FileUntils.readFile(this, pa);
        System.out.println(readLineUTF());
    }
}
