package com.imooc.pay.service.impl;

import com.imooc.pay.PayApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class PayServiceTest extends PayApplicationTests {

    @Autowired
    PayService payService;

    @Test
    public void create() {
        payService.create("1234561111", BigDecimal.valueOf(0.01));
    }
}