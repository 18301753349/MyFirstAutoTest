package com.houbank.interfaces;

import com.alibaba.fastjson.JSONObject;
import com.houbank.enums.ApplicationConfigEnum;
import com.houbank.model.RedisData;
import com.houbank.utils.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Log4j2
public class LoginApi {


    private static String apploginkey = "123456";

    //登录接口
    public static String login(Map<String, Object> caseData) throws Exception {

        JSONObject param = new JSONObject();
        param.put("mobile", caseData.get("params.mobile"));
        String loginType = caseData.get("params.loginType").toString();
        param.put("loginType", loginType);
        if (loginType.equalsIgnoreCase("pwd"))
            param.put("password", caseData.get("params.password"));
        else if (loginType.equalsIgnoreCase("sms"))
            param.put("smsCode", caseData.get("params.smsCode"));

        //xloanVersion=1.3.9 后需加密
        //xloanVersion=2.0.0 才能到加密步骤
        String data = null;
        if (caseData.get("xloanVersion") != null && StringUtils.compare(caseData.get("xloanVersion").toString(), "1.3.9") >= 0)
            data = AESUtils.strEncodBase64(apploginkey, JSONObject.toJSONString(param));
        param.put("data", data);

        HashMap<String, String> headers = setHeaders(caseData);

        Reporter.log("请求头信息:" + JSONObject.toJSONString(headers));

        String result = SendRequestHeader.httpJsonPost(caseData.get("url").toString(), param, headers);
        JSONObject resultObj = JSONObject.parseObject(result);
        Reporter.log("响应结果：" + resultObj);
        if (resultObj == null || resultObj.isEmpty())
            throw new Exception("调用接口获取返回结果失败");
        String actaulResult = resultObj.get("status").toString();

        return actaulResult;
    }


    //发送短信验证码接口
    public static String sendSmsCode(Map<String, Object> caseData) throws Exception {
        String url = ConfigFile.application.getString("sendSmsCodeUrl");
        JSONObject param = new JSONObject();
        param.put("mobile", caseData.get("params.mobile"));
        param.put("type", "SMSLOGIN");

        HashMap<String, String> headers = setHeaders(caseData);

        String sendSmsCodeResult =  SendRequestHeader.httpJsonPost(url, param, headers);
        JSONObject sendSmsCodeResultObj = JSONObject.parseObject(sendSmsCodeResult);
        if (sendSmsCodeResultObj == null || sendSmsCodeResultObj.isEmpty())
            throw new Exception("调用发送短信验证码接口获取返回结果失败");
        Object status = sendSmsCodeResultObj.get("status");

        return status.toString();
    }

    private static HashMap<String, String> setHeaders(Map<String, Object> caseData) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");
        headers.put("filter-key", "filter-header");
        headers.put("xloanPlatform", caseData.get("xloanPlatform") == null ? "WEB" : caseData.get("xloanPlatform").toString());
        headers.put("xloanOsVersion", caseData.get("xloanOsVersion") == null ? null : caseData.get("xloanOsVersion").toString());
        headers.put("xloanChannel", caseData.get("xloanChannel") == null ? "H5" : caseData.get("xloanChannel").toString());
        headers.put("xloanVersion", caseData.get("xloanVersion") == null ? null : caseData.get("xloanVersion").toString());
        return headers;
    }

}
