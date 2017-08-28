package com.baiduad.bean.request;

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
public class RequestHeaderAd<RequestBody> {
    /**
     * timeStamp : 请求方时间戳，消息发出时系统的当前时间。格式：yyyyMMddHHmmss
     * sign : 对消息包的摘要, 摘要算法为MD5，摘要的内容为（messageID+timeStamp +transactionType+系统密钥+消息体）
     * imei : 手机唯一识别号
     * companyId : 厂商id
     */
    public String timeStamp;
    public String sign;
    public String imei;
    public String companyId;

    /**
     * @param body 请求体
     */
    public RequestHeaderAd(RequestBody body) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        timeStamp = sdf.format(new Date(System.currentTimeMillis()));

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

        imei = App.DID;
        companyId = UrlsUtil.COMPANYID;

        StringBuilder sb = new StringBuilder();
        String sign_temp = sb.append(timeStamp).append(imei).append(companyId).append(bodyStr).toString();
        sign = SecurityUtil.md5(sign_temp);
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
}
