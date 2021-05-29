package cn.net.yzl.base.okgo;

import java.io.Serializable;

import cn.net.yzl.base.okgo.ResponseData;

/**
 * 接口返回数据data部分为空
 */

public class SimpleResponse implements Serializable {

    private static final long serialVersionUID = -1477609349345966116L;

    public int code;
    public String message;
    private String status;
    private Object data;

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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ResponseData toLzyResponse() {
        ResponseData lzyResponse = new ResponseData();
        lzyResponse.code = code;
        lzyResponse.message = message;
        return lzyResponse;
    }
}