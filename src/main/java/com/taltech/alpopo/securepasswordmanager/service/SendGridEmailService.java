package com.taltech.alpopo.securepasswordmanager.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendGridEmailService implements EmailService {

    private final String sendGridApiKey;
    private final String fromEmail;
    private final String fromName;

    public SendGridEmailService(@Value("${spring.sendgrid.api-key}") String sendGridApiKey,
                                @Value("${SENDGRID_FROM_EMAIL}") String fromEmail,
                                @Value("${SENDGRID_FROM_NAME}") String fromName) {
        this.sendGridApiKey = sendGridApiKey;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
    }

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        Email from = new Email(fromEmail, fromName);
        Email to = new Email(toEmail);
        String subject = "Your OTP for SecurePasswordManager";
        String content = "Your OTP is: " + otp + "\nIt will expire in 10 minutes";

        Mail mail = new Mail(from, subject, to, new Content("text/plain", content));

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
        } catch (IOException e) {
            throw new RuntimeException("Error sending email via SendGrid", e);
        }
    }
}
