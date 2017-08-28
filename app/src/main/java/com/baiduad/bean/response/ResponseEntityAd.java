package com.baiduad.bean.response;

/**
 * Created by wangzixu on 2017/2/7.
 */
public class ResponseEntityAd<ResponseBody> {
    private ResponseHeaderAd header;
    private ResponseBody body;

    public ResponseHeaderAd getHeader() {
        return header;
    }

    public void setHeader(ResponseHeaderAd header) {
        this.header = header;
    }

    public ResponseBody getBody() {
        return body;
    }

    public void setBody(ResponseBody body) {
        this.body = body;
    }
}
