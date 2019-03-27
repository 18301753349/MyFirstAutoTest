package com.houbank.cases;

import com.alibaba.fastjson.JSONObject;
import com.houbank.enums.ApplicationConfigEnum;
import com.houbank.interfaces.LoginApi;
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
public class LoginTest {

    private Jedis jedis;

    @BeforeTest
    public void setUp() {
        jedis = RedisUtil.getJedis();
    }

    @Test(dataProvider = "loginRedisData", groups = "login", description = "登录缓存测试")
    public void loginRedisTest(Map<String, Object> caseData) throws Exception {

        Reporter.log("开始执行POST请求" + caseData.get("url"));

        String result = "";
        String mobile = caseData.get("params.mobile").toString();
        Integer exceptedSta = Integer.valueOf(caseData.get("excepted.status").toString());

        //设置redis数据
        deleteRedisData(mobile);
        switch (exceptedSta) {
            //登录接口- 超过登录失败最大次数
            case 2001:
                RedisUtil.set(ConfigFile.getConfigValueByMobile(ApplicationConfigEnum.loginFailed_Key, mobile), ConfigFile.getConfigIntValue(ApplicationConfigEnum.loginFailed_MaxCount).toString(), jedis);
                break;
            //登录接口- 超过短信登录失败最大次数
            case 2003:
                RedisUtil.set(ConfigFile.getConfigValueByMobile(ApplicationConfigEnum.smsLogin_FailedCount, mobile), ConfigFile.getConfigIntValue(ApplicationConfigEnum.smsLogin_FailedMaxCount).toString(), jedis);
                break;
            //发送短信验证码失败
            case -1:
                RedisUtil.set(ConfigFile.getConfigValueByMobile(ApplicationConfigEnum.sendSms_Count, mobile), ConfigFile.getConfigIntValue(ApplicationConfigEnum.sendSms_MaxCount).toString(), jedis);
                break;
//                RedisUtil.set(ConfigFile.getConfigValueByMobile(ApplicationConfigEnum.SMS_LOGIN_SENDSMS_TOTALCOUNT, mobile), ConfigFile.getConfigIntValue(ApplicationConfigEnum.EVERY_DAY_SENDSMSCODE_MAX_COUNT).toString(),jedis);
        }

        //调用发送短信验证码接口,并断言结果
        if (caseData.get("params.loginType").toString().equalsIgnoreCase("sms")) {
            Integer status = Integer.parseInt(LoginApi.sendSmsCode(caseData));
            if (status != 0) {
                Assert.assertEquals(exceptedSta, status);
                return;
            }
            //调用短信验证码接口获取短信后，取到验证码
            setSmsCode(caseData, exceptedSta, mobile);
        }

        Reporter.log("请求参数：" + extractParam(caseData));

        result = LoginApi.login(caseData);

        //验证结果
        Assert.assertEquals(caseData.get("excepted.status"), result);
    }
    @DataProvider(name = "loginRedisData")
    public static Iterator<Object[]> smsCodeLoginData() {
        ExcelDataProvider caseData = new ExcelDataProvider("D:/workspace/MyFirstAutoTest/src/main/resources/LoginTest.xlsx", "Sheet2", "");
        return caseData;
    }


    @Test(dataProvider = "loginData", groups = "login", description = "登陆接口测试")
    public void loginTest(Map<String, Object> caseData) throws Exception {

        Reporter.log("开始执行POST请求" + caseData.get("url"));

        String result = "";
        String mobile = caseData.get("params.mobile").toString();
        Integer exceptedSta = Integer.valueOf(caseData.get("excepted.status").toString());

        //删除redis数据
        deleteRedisData(mobile);

        //调用发送短信验证码接口,并断言结果
        if (caseData.get("params.loginType").toString().equalsIgnoreCase("sms")) {
            Integer status = Integer.parseInt(LoginApi.sendSmsCode(caseData));
            if (status != 0) {
                Assert.assertEquals(exceptedSta, status);
                return;
            }
            //调用短信验证码接口获取短信后，取到验证码
            setSmsCode(caseData, exceptedSta, mobile);
        }

        Reporter.log("请求参数：" + extractParam(caseData));

        result = LoginApi.login(caseData);

        //验证结果
        Assert.assertEquals(caseData.get("excepted.status"), result);

    }
    @DataProvider(name = "loginData")
    public static Iterator<Object[]> loginData() {
        ExcelDataProvider caseData = new ExcelDataProvider("D:/workspace/MyFirstAutoTest/src/main/resources/LoginTest.xlsx", "Sheet1", "");
        return caseData;
    }


    private String extractParam(Map<String, Object> map) {
        Map<String, Object> param = new HashMap<>();
        map.forEach((k, v) -> {
            if (k.contains("params") && (map.get(k) != null || map.get(k).toString().equals(null) || map.get(k).toString().equals("null")))
                param.put(k.substring(7), v);
        });
        return JSONObject.toJSONString(param);
    }

    private void deleteRedisData(String mobile) {
        RedisUtil.del(ConfigFile.getConfigValueByMobile(ApplicationConfigEnum.loginFailed_Key, mobile), jedis);
        RedisUtil.del(ConfigFile.getConfigValueByMobile(ApplicationConfigEnum.smsLogin_FailedCount, mobile), jedis);
        RedisUtil.del(ConfigFile.getConfigValueByMobile(ApplicationConfigEnum.sendSms_Count, mobile), jedis);
        RedisUtil.del(ConfigFile.getConfigValueByMobile(ApplicationConfigEnum.sendSms_TotalCount, mobile), jedis);
    }

    private void setSmsCode(Map<String, Object> caseData, Integer exceptedSta, String mobile) throws Exception {
        RedisData redisData = RedisData.getRedisData(mobile, jedis);
        Object smsCode = caseData.get("params.smsCode");
        if(smsCode == null || smsCode.toString().equals(null) || smsCode.toString().equals("null"))
            caseData.replace("params.smsCode", redisData.getSmsLoginSendCode());
    }

}
