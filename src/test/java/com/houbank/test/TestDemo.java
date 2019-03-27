package com.houbank.test;

import com.houbank.utils.ExcelDataProvider;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestDemo {

    //@Test
    public void excelTest() {
        ExcelDataProvider caseData = new ExcelDataProvider("D:/workspace/MyFirstAutoTest/src/main/resources/LoginTest.xls", "Sheet2", "");
        Map<String, Object> map = new LinkedHashMap<>();
        try {
            map = Obj2Map(caseData);
            for(Map.Entry<String,Object> m : map.entrySet()) {
                System.out.println(m.getKey() + ":"+ m.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void headersTest() {
        String xloanPlatform = "WEB";
        String xloanVersion = "2.0.0";
        String xloanChannel = "H5";
        if ((!"IOS".equalsIgnoreCase(xloanPlatform) && !"ANDROID".equals(xloanPlatform) && !"WEB".equals(xloanPlatform))
                || StringUtils.isBlank(xloanVersion)
                || StringUtils.isBlank(xloanChannel)) {
            System.out.println(1);
        }
        System.out.println(0);
    }

    public  Map<String,Object> Obj2Map(Object obj) throws Exception{
        Map<String,Object> map=new HashMap<String, Object>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for(Field field:fields){
            field.setAccessible(true);
            map.put(field.getName(), field.get(obj));
        }
        return map;
    }


}
