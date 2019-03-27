package com.houbank.model;

import com.houbank.utils.ConfigFile;
import com.houbank.utils.RedisUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

@Data
public class RedisData {
    private Integer loginFailedKey;
    private Integer smsLoginFailedCount;
    private Integer smsLoginSendCode;
    private Integer sendSmsCount;
    private Integer sendSmsTotalCount;

    public static RedisData getRedisData(String mobile, Jedis jedis) {
        RedisData redisData = new RedisData();
        String loginFailedKey = RedisUtil.get(String.format(ConfigFile.application.getString("loginFailed.Key"), mobile), jedis);
        if (StringUtils.isNotEmpty(loginFailedKey))
            redisData.setLoginFailedKey(Integer.valueOf(loginFailedKey));
        String smsLoginFailedCount = RedisUtil.get(String.format(ConfigFile.application.getString("smsLogin.FailedCount"), mobile), jedis);
        if (StringUtils.isNotEmpty(smsLoginFailedCount))
            redisData.setSmsLoginFailedCount(Integer.valueOf(smsLoginFailedCount));
        String smsLoginSendCode = RedisUtil.get(String.format(ConfigFile.application.getString("smsLogin.SendCode"), mobile), jedis);
        if (StringUtils.isNotEmpty(smsLoginSendCode))
            redisData.setSmsLoginSendCode(Integer.valueOf(smsLoginSendCode));
        String sendCodeCount = RedisUtil.get(String.format(ConfigFile.application.getString("sendSms.Count"), mobile), jedis);
        if (StringUtils.isNotEmpty(sendCodeCount))
            redisData.setSendSmsCount(Integer.valueOf(sendCodeCount));
        String sendSmsTotalCount = RedisUtil.get(String.format(ConfigFile.application.getString("sendSms.TotalCount"), mobile), jedis);
        if (StringUtils.isNotEmpty(sendSmsTotalCount))
            redisData.setSendSmsTotalCount(Integer.valueOf(sendSmsTotalCount));

        return redisData;
    }

}
