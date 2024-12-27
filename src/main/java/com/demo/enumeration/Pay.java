package com.demo.enumeration;

import com.demo.service.PayService;

public enum Pay implements PayService {
    CASH_PAY() {
        @Override
        public void pay(String orderId) {
            super.pay(orderId);
        }
    },
    AliPay() {
        @Override
        public void pay(String orderId) {
            // TODO Auto-generated method stub
            super.pay(orderId);
        }
    },
    WechatPay() {
        @Override
        public void pay(String orderId) {
            // TODO Auto-generated method stub
            super.pay(orderId);
        }
    };

    @Override
    public void pay(String orderId) {
        throw new UnsupportedOperationException("Unimplemented method 'pay'");
    }

}
