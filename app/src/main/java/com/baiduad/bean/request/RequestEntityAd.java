package com.baiduad.bean.request;

/**
 * Created by wangzixu on 2017/2/7.
 */
public class RequestEntityAd<RequestBody> {
    private RequestHeaderAd header;
    private RequestBody body;

    public RequestHeaderAd getHeader() {
        return header;
    }

    public void setHeader(RequestHeaderAd header) {
        this.header = header;
    }

    public RequestBody getBody() {
        return body;
    }

    public void setBody(RequestBody body) {
        this.body = body;
    }
}
