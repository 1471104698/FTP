package cn.oy.test.io;


import java.io.*;
import java.net.Socket;

/**
 * @author 蒜头王八
 * @project: ftp
 * @Description:
 * @Date 2020/5/5 16:50
 */
public abstract class FTP<T, V> {

    private Socket msgSocket;

    //用于数据传输的 socket
    Socket dataSocket = null;

    //客户端和服务端之间文字通信
    ObjectInputStream reader = null;
    ObjectOutputStream writer = null;
    private DataInputStream dreader = null;
    private DataOutputStream dwriter = null;

    V msg;

    public FTP(Socket msgSocket) {
        this.msgSocket = msgSocket;
        try {
            dreader = new DataInputStream(msgSocket.getInputStream());
            dwriter = new DataOutputStream((msgSocket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送消息给服务端
     *
     * @param msg
     */
    public void sendLine(T msg) {
        try {
            writer.writeObject(msg);
            writer.flush();
        } catch (IOException e) {
            // logger.error("发送消息到客户端失败");
            System.out.println("发送消息到客户端失败");
        }
    }

    /**
     * 发送消息给服务端
     *
     * @param str
     */
    public void sendLineUTF(String str) {
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
     *
     * @return
     */
    public void readLine() {
        try {
            msg = (V) reader.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            // logger.error("读取客户端消息错误");
            System.out.println("读取客户端消息错误");
        }
    }

    /**
     * 读取服务器返回的信息
     *
     * @return
     */
    public String readLineUTF() {
        try {
            return dreader.readUTF();
        } catch (IOException e) {
            // logger.error("读取服务器消息错误");
            System.out.println("读取服务器消息错误");
        }
        return "";
    }


    public Socket getMsgSocket() {
        return msgSocket;
    }

    public Socket getDataSocket() {
        return dataSocket;
    }

    public void setDataSocket(Socket dataSocket) {
        this.dataSocket = dataSocket;
    }

    public V getMsg() {
        return msg;
    }
}
