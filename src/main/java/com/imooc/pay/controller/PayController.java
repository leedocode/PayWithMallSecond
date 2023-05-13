package com.imooc.pay.controller;

import com.imooc.pay.service.impl.PayService;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述: TODO
 */

@Controller
@RequestMapping("/pay")
@Slf4j
public class PayController {

    @Autowired
    PayService payService;

    @GetMapping("/create")
    public ModelAndView create(@RequestParam("orderId") String orderId, @RequestParam("amount") BigDecimal amount, @RequestParam("payType")BestPayTypeEnum bestPayTypeEnum) {
        PayResponse payResponse = payService.create(orderId, amount, bestPayTypeEnum);
        Map map = new HashMap();
        String viewName = "";
        if (bestPayTypeEnum == BestPayTypeEnum.WXPAY_NATIVE) {
            map.put("codeUrl", payResponse.getCodeUrl());
            viewName = "createForWxNative";
        }
        return new ModelAndView(viewName, map);

    }

    @PostMapping("/notify")
    @ResponseBody
    public String asyncNotify(@RequestBody String notifyData) {
        return payService.asyncNotify(notifyData);
    }

}
