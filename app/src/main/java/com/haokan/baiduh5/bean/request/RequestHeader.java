package com.haokan.baiduh5.bean.request;

import android.text.TextUtils;

import com.haokan.baiduh5.App;
import com.haokan.baiduh5.http.UrlsUtil;
import com.haokan.baiduh5.util.SecurityUtil;

import org.json.JSONException;
import org.json.JSONStringer;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by wangzixu on 2017/2/7.
 */
public class RequestHeader<RequestBody> {
    /**
     * messageID : 交易流水号。由yyyyMMddHHmmss+流水号10位）组成。流水号从0000000001开始计数，步长为1，最大取值为9999999999，循环使用
     * timeStamp : 请求方时间戳，消息发出时系统的当前时间。格式：yyyyMMddHHmmss
     * transactionType : 交易类型
     * sign : 对消息包的摘要, 摘要算法为MD5，摘要的内容为（messageID+timeStamp +transactionType+系统密钥+消息体）
     * terminal : 1客户端，2网站，3HTML5, 4ios, 5android
     * version : 终端版本
     * imei : 手机唯一识别号
     * ua : 手机型号
     * companyId : 厂商id
     *
     * languageCode	STRING	10	是	语言码
     countryCode	STRING	10	是	国家码
     did	STRING	32	是	用户唯一标识
     */
    private String messageID;
    private String timeStamp;
    private String sign;
    private String terminal;
    private String version;
    private String imei;
    private String ua;
    private String companyId;
    private String languageCode;
    private String countryCode;
    private String did;

    /**
     * @param body 请求体
     */
    public RequestHeader(RequestBody body) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        timeStamp = sdf.format(new Date(System.currentTimeMillis()));
        messageID = timeStamp + new UrlsUtil().getSerialCode();

        //把body转换成json串, 并且字段有序
        String bodyStr;
        TreeMap<String, Object> treeMap = beanToMap(body);
        JSONStringer stringer = new JSONStringer();
        try {
            stringer.object();
            if (treeMap != null) {
                for (Map.Entry<String, Object> entry : treeMap.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (TextUtils.isEmpty(key) || value == null || TextUtils.isEmpty(value.toString())) {
                        continue;
                    }
                    stringer.key(entry.getKey()).value(entry.getValue());
                }
            }
            stringer.endObject();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        bodyStr = stringer.toString();

        StringBuilder sb = new StringBuilder();
        String sign_temp = sb.append(messageID).append(timeStamp).append(UrlsUtil.SECRET_KEY).append(bodyStr).toString();
//        LogHelper.d("wangzixu", "sign = " + sign_temp);
        sign = SecurityUtil.md5(sign_temp);

        terminal = "5";
        version = App.APP_VERSION_NAME;
        imei = App.DID;
        companyId = UrlsUtil.COMPANYID;
        languageCode = App.sLanguage_code;
        countryCode = App.sCountry_code;
        ua = App.sPhoneModel;
        did = App.DID;
    }

    public static TreeMap<String, Object> beanToMap(Object object){
        if (object == null) {
            return null;
        }

        TreeMap<String, Object> map = new TreeMap<String, Object>();
        try {
            Class cls = object.getClass();
            Field[] fields = cls.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
//                LogHelper.d("requestHeader", "field = " + field);
                map.put(field.getName(), field.get(object));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }


    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }
}
