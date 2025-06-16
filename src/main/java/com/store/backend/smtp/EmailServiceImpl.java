package com.store.backend.smtp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Value("${spring.mail.username}")
  private String emailFrom;

  @Override
  public void sendVerifySignUpEmail(String to, String subject, String otp) {
    Context context = new Context();
    context.setVariable("subject", subject);
    context.setVariable("otp", otp);

    String htmlContent = templateEngine.process("auth-email", context);
    MimeMessage message = mailSender.createMimeMessage();
    try {
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlContent, true);
      helper.setFrom(emailFrom);

      mailSender.send(message);
      log.info("Email xác thực đã được gửi đến: {}", to);
    } catch (MessagingException e) {
      log.error("Gửi email xác nhận thất bại", e);
      throw new RuntimeException("Gửi email thất bại", e);
    }
  }
}
