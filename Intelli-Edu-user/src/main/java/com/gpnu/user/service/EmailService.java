package com.gpnu.user.service;




public interface EmailService {

    /**
     * @description: 发送验证码
     */
    void sendVerifiedCode(String email,String code);

    /**
     * @param email:
     * @param subject:
     * @param text:
     * @return void
     * @description: 发送邮件
     */
    void sendEmail(String email, String subject, String text);


}