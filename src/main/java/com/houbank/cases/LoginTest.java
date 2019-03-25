package com.houbank.cases;

import com.alibaba.fastjson.JSONObject;
import com.houbank.utils.*;
import lombok.extern.log4j.Log4j2;
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
public class LoginTest {


    private static String apploginkey = "123456";

    private Jedis jedis;
    //今日登录失败次数
    String loginFailedCount;

    //今日短信验证码登录失败次数
    String smsLoginFailedCount;

    //短信验证码
    String smsLoginSendCode;

    @BeforeTest
    public void setUp() {;
        jedis= RedisUtil.getJedis();
    }

    @Test(dataProvider = "loginData", groups = "login", description = "密码登陆测试")
    public void loginTest(Map<String, String> caseData) throws Exception {

        Reporter.log("开始执行POST请求" + caseData.get("url"));

        //第一步就是发送请求
        Reporter.log("请求参数：" + extractParam(caseData));
        String result = "";
        String mobile = caseData.get("params.mobile");

        //今日登录失败次数
        loginFailedCount = RedisUtil.get(String.format(ConfigFile.application.getString("loginFailedKey"), mobile),jedis);

        result = login(caseData);
        JSONObject resultObj = JSONObject.parseObject(result);
        Reporter.log("响应结果：" + resultObj);
        if (resultObj == null || resultObj.isEmpty())
            throw new Exception("调用接口获取返回结果失败");
        String actaulResult = resultObj.get("status").toString();

        Thread.sleep(3000);

        //验证结果
        Assert.assertEquals(caseData.get("excepted.status"), actaulResult);

    }

    @DataProvider(name = "loginData")
    public static Iterator<Object[]> loginData() {
        ExcelDataProvider caseData = new ExcelDataProvider("D:/workspace/hb-autotest/src/main/resources/LoginTest.xlsx", "Sheet1", "");
        return caseData;
    }

    @Test(dataProvider = "smsCodeLoginData", groups = "smsCodeLogin", description = "短信验证码登陆测试")
    public void smsCodeLoginTest(Map<String, String> caseData) throws Exception {

        Reporter.log("开始执行POST请求" + caseData.get("url"));

        //第一步就是发送请求
        Reporter.log("请求参数：" + extractParam(caseData));
        String sendSmsCodeResult = "";
        String result = "";
        String mobile = caseData.get("mobile");

        loginFailedCount = RedisUtil.get(String.format(ConfigFile.application.getString("loginFailedKey"), mobile),jedis);
        smsLoginFailedCount = RedisUtil.get(String.format(ConfigFile.application.getString("smsLoginFailedCount"), mobile),jedis);
        //smsLoginSendCode = RedisUtil.get(String.format(ConfigFile.application.getString("smsLoginSendCode"), mobile),jedis);

        //调用发送短信验证码接口
        sendSmsCodeResult = sendSmsCode(caseData);
        JSONObject sendSmsCodeResultObj = JSONObject.parseObject(sendSmsCodeResult);
        if (sendSmsCodeResultObj == null || sendSmsCodeResultObj.isEmpty())
            throw new Exception("调用发送短信验证码接口获取返回结果失败");
        Integer code = (Integer) sendSmsCodeResultObj.get("code");
        caseData.replace("params.smsCode", code.toString());

        Reporter.log("请求参数：" + extractParam(caseData));

        result = login(caseData);
        JSONObject resultObj = JSONObject.parseObject(result);
        Reporter.log("响应结果：" + resultObj);
        if (resultObj == null || resultObj.isEmpty())
            throw new Exception("调用接口获取返回结果失败");
        String actaulResult = resultObj.get("status").toString();

        Thread.sleep(3000);

        //验证结果
        Assert.assertEquals(caseData.get("excepted.status"), actaulResult);
    }

    @DataProvider(name = "smsCodeLoginData")
    public static Iterator<Object[]> smsCodeLoginData() {
        ExcelDataProvider caseData = new ExcelDataProvider("D:/workspace/hb-autotest/src/main/resources/LoginTest.xlsx", "Sheet2", "");
        return caseData;
    }

    private String login(Map<String, String> caseData) throws Exception {

        JSONObject param = new JSONObject();
        param.put("mobile", caseData.get("params.mobile"));
        String loginType = caseData.get("params.loginType");
        if(loginType.equalsIgnoreCase("pwd"))
            param.put("password", caseData.get("params.password"));
        else if(loginType.equalsIgnoreCase("sms"))
            param.put("smsCode", caseData.get("params.smsCode"));

        //xloanVersion=1.3.9 后需加密
        //xloanVersion=2.0.0 才能到加密步骤
        String data = AESUtils.strEncodBase64(apploginkey, JSONObject.toJSONString(param));
        param.put("data", data);

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("content-type", "application/json");
        headers.put("filter-key", "filter-header");
        headers.put("xloanPlatform", "WEB");
        headers.put("xloanOsVersion", "8.0.0");
        headers.put("xloanChannel", "DAY7_H5");
        headers.put("xloanVersion", "2.0.0");

        Reporter.log("请求头信息:" + JSONObject.toJSONString(headers));

        return SendRequestHeader.httpJsonPost(caseData.get("url"), param, headers);

    }

    private String sendSmsCode(Map<String, String> caseData) throws Exception {
        String url = ConfigFile.application.getString("sendSmsCodeUrl");
        JSONObject param = new JSONObject();
        param.put("mobile", caseData.get("params.mobile"));
        param.put("type", "SMSLOGIN");

        //xloanVersion=1.3.9 后需加密
        //xloanVersion=2.0.0 才能到加密步骤
        String data = AESUtils.strEncodBase64(apploginkey, JSONObject.toJSONString(param));
        param.put("data", data);

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("content-type", "application/json");
        headers.put("filter-key", "filter-header");
        headers.put("xloanPlatform", "WEB");
        headers.put("xloanChannel", "DAY7_H5");
        headers.put("xloanVersion", "2.0.0");

        return SendRequestHeader.httpJsonPost(url, param, headers);
    }


    private String extractParam(Map<String, String> map) {
        Map<String, Object> param = new HashMap<>();
        map.forEach((k, v) -> {
            if (k.contains("params"))
                param.put(k.substring(7), v);
        });
        return JSONObject.toJSONString(param);
    }

}
