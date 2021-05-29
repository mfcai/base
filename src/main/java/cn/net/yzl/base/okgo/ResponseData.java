package cn.net.yzl.base.okgo;

import java.io.Serializable;

/**
 * 全应用通用的接口返回数据包装层
 */

public class ResponseData<T> implements Serializable {

    private static final long serialVersionUID = 5213230387175987834L;

    public int code;
    public String message;
    public T data;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LzyResponse{\n" +//
                "\tdata=" + data + "\n" +//
                '}';
    }
}
