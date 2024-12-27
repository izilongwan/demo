package com.demo.enumeration;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class PayTest {
    @Test
    void testPay() {
        Pay cashPay = Pay.valueOf(Pay.CASH_PAY.name());
        cashPay.pay(LocalDateTime.now().toString());
    }
}
