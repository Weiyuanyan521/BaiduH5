package com.haokan.baiduh5.bean;

/**
 * Created by wangzixu on 2017/2/7.
 */
public class ResponseEntity<ResponseBody> {
    private ResponseBody body;
    private int err_code;
    private String err_msg;

    public int getErr_code() {
        return err_code;
    }

    public void setErr_code(int err_code) {
        this.err_code = err_code;
    }

    public String getErr_msg() {
        return err_msg;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }

    public ResponseBody getBody() {
        return body;
    }

    public void setBody(ResponseBody body) {
        this.body = body;
    }
}
