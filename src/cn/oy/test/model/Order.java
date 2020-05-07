package cn.oy.test.model;

/**
 * @author 蒜头王八
 * @project: ftp
 * @Description:
 * @Date 2020/5/3 16:53
 */
//客户端给服务端的命令消息实体
public class Order extends Message {
    //消息类型，比如 user, pwd, list 等
    private String type;

    public Order() {
    }

    public Order(String type) {
        this(type, "");
    }

    public Order(String type, String msg) {
        super(msg);
        this.type = type;
    }

    public static Order user(String msg) {
        return new Order(Type.USER.name(), msg);
    }

    public static Order pwd(String msg) {
        return new Order(Type.PWD.name(), msg);
    }

    public static Order list(String msg) {
        return new Order(Type.LIST.name(), msg);
    }

    public static Order cd(String msg) {
        return new Order(Type.CD.name(), msg);
    }

    public static Order quit() {
        return new Order(Type.QUIT.name());
    }

    public static Order pasv() {
        return new Order(Type.PASV.name());
    }

    public static Order down(String msg) {
        return new Order(Type.DOWN.name(), msg);
    }

    public static Order upload(String msg) {
        return new Order(Type.UPLOAD.name(), msg);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Order{" +
                "type='" + type + '\'' +
                "} " + super.toString();
    }
}
