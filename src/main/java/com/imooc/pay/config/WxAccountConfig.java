package com.imooc.pay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 描述: TODO
 */
@Component
@ConfigurationProperties(prefix = "wx")
@Data
public class WxAccountConfig {
    public String appid;
    public String mchId;
    public String mchKey;
    public String notifyUrl;
    public String returnUrl;
}
