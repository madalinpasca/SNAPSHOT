package com.madalin.wisetraveller.service;

import com.madalin.wisetraveller.model.Email;
import com.madalin.wisetraveller.model.EmailTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;

@Service
public class EmailService {
    @Getter
    @NoArgsConstructor
    public static class EmailConfiguration {
        private boolean smtpAuth = true;
        private boolean smtpStartTlsEnable = true;
        private String smtpHost = "smtp.gmail.com";
        private int smtpPort = 587;
        private String smtpSocketFactoryClass = "javax.net.ssl.SSLSocketFactory";
        private long timeout = 30;
        private static Map<String, EmailTemplate> templates = loadTemplates();
        private String mimeType = "text/html";
        private Properties properties = loadProperties();

        private static final String SMTP_PORT = "mail.smtp.port";
        private static final String SMTP_HOST = "mail.smtp.host";
        private static final String SMTP_AUTH = "mail.smtp.auth";
        private static final String SMTP_STARTLE = "mail.smtp.starttls.enable";
        private static final String SSL_SOCKET_FACTORY_CLASS = "mail.smtp.socketFactory.class";

        private static Map<String, EmailTemplate> loadTemplates() {
            Map<String, EmailTemplate> templates = new HashMap<>();

            templates.put("Activation", new EmailTemplate(
                    Collections.singletonList("http://localhost:8080/unauthenticated/activate"),
                    Collections.emptyList(),
                    "<h2>Confirm Registration</h2>\n" +
                            "<p><a href=\"%s?id=%d&uuid=%s\">Click this to confirm the registration. </a></p>\n" +
                            "<p>You should confirm your email in the next 24h.</p>",
                    "Confirm Registration",
                    new EmailTemplate.EmailAuthenticator("wise.traveller.licenta@gmail.com",
                            "wisewise")));

            templates.put("ResetPassword", new EmailTemplate(
                    Collections.singletonList("http://localhost:8080/unauthenticated/resetPassword"),
                    Collections.emptyList(),
                    "<h2>Reset Password</h2>\n" +
                            "<p><a href=\"%s?id=%d&uuid=%s\">Click this to reset the password to the new value. </a></p>\n" +
                            "<p>If you did not request a password reset ignore this message!</p>",
                    "Reset Password",
                    new EmailTemplate.EmailAuthenticator("wise.traveller.licenta@gmail.com",
                            "wisewise")));

            return templates;
        }

        public static Map<String, EmailTemplate> getTemplates() {
            return templates;
        }

        private Properties loadProperties() {
            properties = new Properties();
            properties.setProperty(SMTP_AUTH, String.valueOf(smtpAuth));
            properties.setProperty(SMTP_STARTLE, String.valueOf(smtpStartTlsEnable));
            properties.setProperty(SMTP_HOST, smtpHost);
            properties.setProperty(SMTP_PORT, String.valueOf(smtpPort));
            properties.setProperty(SSL_SOCKET_FACTORY_CLASS, smtpSocketFactoryClass);
            return properties;
        }
    }
    private static EmailConfiguration emailConfiguration = new EmailConfiguration();

    @SuppressWarnings("WeakerAccess")
    public void send(String to, Email email) {
        createThreadToWait(()->sendRun(to, email));
    }

    private void sendRun(String to, Email email) {
        try {
            trySend(to, email);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void trySend(String to, Email email) throws MessagingException {
        MimeMessage message = new MimeMessage(createNewSession(email.getType().getTemplate().getAuthenticator()));
        message.setFrom(email.getType().getTemplate().getAuthenticator().getAddress());
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(email.getSubject());
        message.setContent(email.getContent(), emailConfiguration.mimeType);
        Transport.send(message);
    }

    private Session createNewSession(EmailTemplate.EmailAuthenticator authenticator) {
        return Session.getInstance(emailConfiguration.getProperties(), authenticator);
    }

    private void createThreadToWait(Runnable runnable) {
        new Thread(()->{
            try {
                createThreadAndWait(runnable);
            } catch (Exception ignored) { }
        }).start();
    }

    private void createThreadAndWait(Runnable runnable) throws InterruptedException {
        Thread thread = new Thread(runnable);
        thread.start();
        thread.join(1000 * emailConfiguration.getTimeout());
        if (thread.isAlive())
            thread.interrupt();
    }

    static boolean isEmailValid(String email) {
        try {
            new InternetAddress(email).validate();
        } catch (AddressException ex) {
            return false;
        }
        return true;
    }
}
