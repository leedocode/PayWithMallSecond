package com.imooc.pay.service.impl;

import com.imooc.pay.PayApplication;
import com.imooc.pay.dao.PayInfoMapper;
import com.imooc.pay.enums.PayPlatformEnum;
import com.imooc.pay.pojo.PayInfo;
import com.imooc.pay.service.IPayService;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
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

    @Autowired
    PayInfoMapper payInfoMapper;
    @Override
    public PayResponse create(String orderId, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum) {

        if (bestPayTypeEnum != BestPayTypeEnum.WXPAY_NATIVE) {
            throw new RuntimeException("暂不支持此支付方式");
        }

        //写入数据库
        PayInfo payInfo = new PayInfo(Long.parseLong(orderId), PayPlatformEnum.getByBestPayTypeEnum(bestPayTypeEnum).getCode(),
                OrderStatusEnum.NOTPAY.name(), amount);
        payInfoMapper.insertSelective(payInfo);

        PayRequest payRequest = new PayRequest();
        payRequest.setPayTypeEnum(bestPayTypeEnum);
        payRequest.setOrderId(orderId);
        payRequest.setOrderName("微信公众账号支付订单");
        payRequest.setOrderAmount(amount.doubleValue());
        PayResponse response = bestPayService.pay(payRequest);
        log.info("发起支付response = {}", response);
        return response;
    }

    @Override
    public String asyncNotify(String notifyData) {
        //1 签名检验
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("异步通知 payResponse={}", payResponse);

        //2 金额校验，从数据库里查询订单
        PayInfo payInfo = payInfoMapper.selectByOrderNo(Long.parseLong(payResponse.getOrderId()));
        //查询不到，则需要发出告警，属于严重错误
        if (payInfo == null) {
            throw new RuntimeException("通过orderNo查询到的结果是null");
        }
        //如果订单状态不是"已支付"
        if (!payInfo.getPlatformStatus().equals(OrderStatusEnum.SUCCESS.name())) {
            if (payInfo.getPayAmount().compareTo(BigDecimal.valueOf(payResponse.getOrderAmount())) != 0) {
                //告警
                throw new RuntimeException("异步通知里的金额和数据库里的金额不一致，orderNo=" + payInfo.getOrderNo());
            }
            // 3 修改支付状态
            payInfo.setPlatformStatus(OrderStatusEnum.SUCCESS.name());
            payInfo.setPlatformNumber(payResponse.getOutTradeNo());
            payInfo.setUpdateTime(null);
            payInfoMapper.updateByPrimaryKeySelective(payInfo);
        }

        //TODO: pay发送MQ消息 MALL接收MQ消息


        //4 告诉微信无需通知
        if (payResponse.getPayPlatformEnum() == BestPayPlatformEnum.WX) {
            return "<xml>\n" +
                    "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                    "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                    "</xml>";
        }

        throw new RuntimeException("异步通知中错误的支付平台");
    }

    @Override
    public PayInfo queryByOrderId(String orderId) {
        return payInfoMapper.selectByOrderNo(Long.parseLong(orderId));
    }
}
