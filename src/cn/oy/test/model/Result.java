package cn.oy.test.model;

import java.io.Serializable;

/**
 * @author 蒜头王八
 * @project: ftp
 * @Description:
 * @Date 2020/5/3 16:44
 */
//服务端给客户端的响应消息实体
public class Result extends Message {
    private int code;

    public Result() {
    }

    public Result(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public static Result ok(String msg){
        return new Result(200, msg);
    }

    public static Result error(String msg){
        return new Result(500, msg);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                "} " + super.toString();
    }
}
