package com.houbank.cases;

import com.alibaba.fastjson.JSONObject;
import com.houbank.utils.AESUtils;
import com.houbank.utils.ConfigFile;
import com.houbank.utils.ExcelDataProvider;
import com.houbank.utils.SendRequestHeader;
import com.houbank.utils.redis.JedisTemple;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Log4j2
public class LoginTest {

<<<<<<< HEAD
    private static String apploginkey = "1234567";
=======
    private static String apploginkey = "123456";
>>>>>>> 726243be7b2634bef7d732846bb17b62d31df170

    private JedisTemple redis = new JedisTemple();

    @Test(dataProvider = "testMapData",groups = "loginTrue",description = "用户登陆接口测试")
    public void loginTest(Map<String, String> caseData) throws Exception {

        Reporter.log("开始执行POST请求"+caseData.get("url"));

        //第一步就是发送请求
        Reporter.log("请求参数："+extractParam(caseData));
        String result = "";
        String ignored = caseData.get("ignored");
        String mobile = caseData.get("mobile");

        //今日登录失败次数
        String loginFailedCount = redis.get(String.format(ConfigFile.bundle.getString("loginFailedKey"), mobile));
       //今日短信验证码登录失败次数
        String smsLoginFailedCount = redis.get(String.format(ConfigFile.bundle.getString("smsLoginFailedCount"), mobile));
       //短信验证码
        String smsLoginSendCode = redis.get(String.format(ConfigFile.bundle.getString("smsLoginSendCode"), mobile));

        if (StringUtils.isNotEmpty(ignored) && ignored.equals("0")) {
             result = getResult(caseData);
            JSONObject resultObj = JSONObject.parseObject(result);
            Reporter.log("响应结果："+resultObj);
            if (resultObj == null || resultObj.isEmpty())
                throw new Exception("调用接口获取返回结果失败");
            String actaulResult = resultObj.get("status").toString();

            Thread.sleep(3000);

            //验证结果
            Assert.assertEquals(caseData.get("excepted.status"),actaulResult);
        }
    }

    @DataProvider(name = "testMapData")
    public static Iterator<Object[]> testData() {
        ExcelDataProvider caseData = new ExcelDataProvider("D:/workspace/hb-autotest/src/main/resources/LoginTest.xlsx","Sheet1","");
        return caseData;
    }

    private String getResult(Map<String, String> caseData) throws Exception {

        JSONObject param = new JSONObject();
        param.put("mobile",caseData.get("params.mobile"));
        param.put("password",caseData.get("params.password"));
        param.put("loginType",caseData.get("params.loginType"));

        //xloanVersion=1.3.9 后需加密
        //xloanVersion=2.0.0 才能到加密步骤
        String data = AESUtils.strEncodBase64(apploginkey,JSONObject.toJSONString(param));
        param.put("data",data);

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("content-type","application/json");
        headers.put("filter-key", "filter-header");
        headers.put("xloanPlatform", "WEB");
        headers.put("xloanOsVersion", "8.0.0");
        headers.put("xloanChannel", "DAY7_H5");
        headers.put("xloanVersion", "2.0.0");

        Reporter.log("请求头信息:"+JSONObject.toJSONString(headers));

        return SendRequestHeader.httpJsonPost(caseData.get("url"), param, headers);

    }

    public String extractParam(Map<String,String> map){
        Map<String, Object> param = new HashMap<>();
        map.forEach((k,v)->{
            if (k.contains("params"))
                param.put(k.substring(7), v);
        });
        return JSONObject.toJSONString(param);
    }

}
