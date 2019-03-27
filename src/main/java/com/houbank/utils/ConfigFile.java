package com.houbank.utils;

import com.houbank.enums.ApplicationConfigEnum;

import java.util.Locale;
import java.util.ResourceBundle;

public class ConfigFile {

    public static ResourceBundle application = ResourceBundle.getBundle("application", Locale.CHINA);

    public static String getConfigValueByMobile(ApplicationConfigEnum configEnum, String mobile) {
        String result = "";
        switch (configEnum) {
            case loginFailed_Key:
                result = String.format(application.getString("loginFailed.Key"), mobile);
                break;
            case smsLogin_FailedCount:
                result = String.format(application.getString("smsLogin.FailedCount"), mobile);
                break;
            case smsLogin_SendCode:
                result = String.format(application.getString("smsLogin.SendCode"), mobile);
                break;
            case sendSms_Count:
                result = String.format(application.getString("sendSms.Count"), mobile);
                break;
            case sendSms_TotalCount:
                result = String.format(application.getString("sendSms.TotalCount"), mobile);
                break;
        }
        return result;
    }

    public static Integer getConfigIntValue(ApplicationConfigEnum configEnum) {
        Integer result = null;
        switch (configEnum) {
            case loginFailed_MaxCount:
                result = Integer.valueOf(application.getString("loginFailed.MaxCount"));
                break;
            case smsLogin_FailedMaxCount:
                result = Integer.valueOf(application.getString("smsLogin.FailedMaxCount"));
                break;
            case sendSms_MaxCount:
                result = Integer.valueOf(application.getString("sendSms.MaxCount"));
                break;
            case sendSms_MaxTotalCount:
                result = Integer.valueOf(application.getString("sendSms.MaxTotalCount"));
                break;
        }
        return result;
    }

}
