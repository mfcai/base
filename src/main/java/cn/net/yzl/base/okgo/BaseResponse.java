package cn.net.yzl.base.okgo;

import com.google.gson.Gson;

import java.io.Serializable;

public class BaseResponse<T> implements Serializable {
    /**
     * {"status":1,"code":200,"message":null,"data":"6666"}
     */
    private String message;
    private T data;
    private int code;
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static <T> T fromJson(Class<T> c, String jsonStr) {
        return new Gson().fromJson(jsonStr, c);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


}
