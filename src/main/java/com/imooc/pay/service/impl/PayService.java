package com.imooc.pay.service.impl;

import com.imooc.pay.service.IPayService;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.impl.BestPayServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 描述: TODO
 */

@Slf4j
@Service
public class PayService implements IPayService {
    @Override
    public PayResponse create(String orderId, BigDecimal amount) {

        WxPayConfig wxPayConfig= new WxPayConfig();
        wxPayConfig.setAppId("wx3e6b9f1c5a7ff034");
        wxPayConfig.setMchId("1614433647");
        wxPayConfig.setMchKey("Aa111111111122222222223333333333");


        BestPayServiceImpl bestPayService = new BestPayServiceImpl();
        bestPayService.setWxPayConfig(wxPayConfig);

        PayRequest payRequest = new PayRequest();
        payRequest.setPayTypeEnum(BestPayTypeEnum.WXPAY_NATIVE);
        payRequest.setOrderId(orderId);
        payRequest.setOrderName("微信公众账号支付订单");
        payRequest.setOrderAmount(amount.doubleValue());
        wxPayConfig.setNotifyUrl("http://mall-second.natapp1.cc/pay/notify"); //微信支付通知地址，支付成功后将会通过这个地址返回相应通知信息
        PayResponse response = bestPayService.pay(payRequest);
        log.info("response = {}", response);
        return response;
    }
}
