package com.gpnu.user.service.impl;


import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.user.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;





@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sendMailer;

    @Autowired
    private TemplateEngine templateEngine;




    @Override
    public void sendVerifiedCode(String email,String code) {
        Context context = new Context();
        context.setVariable("code", code);

        // 2.发送邮件
        String text = templateEngine.process("emailVerifyCode", context);


        // 发送邮件
        sendEmail(email, "智慧教学平台验证码为", text);

    }

    @Override
    public void sendEmail(String to, String subject, String content) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            //邮件发件人
            helper.setFrom(sendMailer);
            //邮件收件人
            helper.setTo(to);
            //邮件主题
            helper.setSubject(subject);
            //邮件内容
            helper.setText(content, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            // 发送邮件失败，抛出自定义异常
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "邮件发送失败，请稍后再试");

        }
    }




}
