package com.haokan.baiduh5.bean.response;

/**
 * Created by wangzixu on 2017/2/7.
 */
public class ResponseHeader {
    /*
    messageID	STRING	28	否	消息编号,请求与应答的消息序列号必须相同。
    timeStamp	STRING	14	否	响应方时间戳，响应方消息发出时系统当前时间。格式为：yyyyMMddHHmmss
    resCode	INTEGER	8	否
    message	STRING	256	否	对resCode的描述
     */

    private String messageID;
    private int resCode;
    private String resMsg;
    private String timeStamp;

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public String getResMsg() {
        return resMsg;
    }

    public void setResMsg(String resMsg) {
        this.resMsg = resMsg;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
