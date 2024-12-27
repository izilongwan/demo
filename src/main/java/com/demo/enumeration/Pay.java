package com.demo.enumeration;

import com.demo.service.PayService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Pay implements PayService {
    CASH_PAY() {
        @Override
        public void pay(String orderId) {
            // super.pay(orderId);
            log.debug("cash pay orderId: {}", orderId);
        }
    },
    ALI_PAY() {
        @Override
        public void pay(String orderId) {
            super.pay(orderId);
        }
    },
    WECHAT_PAY() {
        @Override
        public void pay(String orderId) {
            super.pay(orderId);
        }
    };

    @Override
    public void pay(String orderId) {
        log.debug("pay orderId: {}", orderId);
        System.out.println("adsf");
    }

}
