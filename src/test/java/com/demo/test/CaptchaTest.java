package com.demo.test;

import java.io.FileOutputStream;

import org.junit.jupiter.api.Test;

import com.wf.captcha.ChineseCaptcha;
import com.wf.captcha.ChineseGifCaptcha;

public class CaptchaTest {
    @Test
    public void test() throws Exception {
        ChineseCaptcha chineseCaptcha = new ChineseCaptcha();
        String text = chineseCaptcha.text();
        chineseCaptcha.out(new FileOutputStream("src/main/resources/1.png"));
        System.out.println(text);

        ChineseGifCaptcha chineseGifCaptcha = new ChineseGifCaptcha();
        chineseGifCaptcha.out(new FileOutputStream("src/main/resources/1.gif"));
    }
}
