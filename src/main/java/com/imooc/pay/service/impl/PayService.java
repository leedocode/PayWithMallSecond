package com.imooc.pay.service.impl;

import com.imooc.pay.service.IPayService;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 描述: TODO
 */

@Slf4j
@Service
public class PayService implements IPayService {

    @Autowired
    BestPayService bestPayService;
    @Override
    public PayResponse create(String orderId, BigDecimal amount) {

        PayRequest payRequest = new PayRequest();
        payRequest.setPayTypeEnum(BestPayTypeEnum.WXPAY_NATIVE);
        payRequest.setOrderId(orderId);
        payRequest.setOrderName("微信公众账号支付订单");
        payRequest.setOrderAmount(amount.doubleValue());
        PayResponse response = bestPayService.pay(payRequest);
        log.info("response = {}", response);
        return response;
    }

    @Override
    public String asyncNotify(String notifyData) {
        //1 签名检验
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("payResponse={}", payResponse);



        //4 告诉微信无需一直通知了
        return "<xml>\n" +
                "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                "</xml>";
    }
}
