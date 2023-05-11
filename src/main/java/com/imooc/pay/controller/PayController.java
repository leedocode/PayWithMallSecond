package com.imooc.pay.controller;

import com.imooc.pay.service.impl.PayService;
import com.lly835.bestpay.model.PayResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述: TODO
 */

@Controller
@RequestMapping("/pay")
public class PayController {

    @Autowired
    PayService payService;

    @GetMapping("/create")
    public ModelAndView create(@RequestParam("orderId") String orderId, @RequestParam("amount") BigDecimal amount) {
        PayResponse payResponse = payService.create(orderId, amount);
        Map map = new HashMap();
        map.put("codeUrl", payResponse.getCodeUrl());
        return new ModelAndView("create", map);
    }
}
