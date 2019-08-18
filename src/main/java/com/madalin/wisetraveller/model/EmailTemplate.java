package com.madalin.wisetraveller.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailTemplate {
    private List<Object> contentArguments = new ArrayList<>();
    private List<Object> subjectArguments = new ArrayList<>();
    private String contentFormat;
    private String subjectFormat;
    private EmailAuthenticator authenticator;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmailAuthenticator extends Authenticator {
        private String username;
        private String password;
        public InternetAddress getAddress() {
            try {
                return new InternetAddress(getUsername());
            } catch (AddressException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(getUsername(), getPassword());
        }
    }

    public String getSubject(Object[] arguments) {
        return completeTemplate(subjectFormat, subjectArguments, arguments);
    }

    public String getContent(Object[] arguments) {
        return completeTemplate(contentFormat, contentArguments, arguments);
    }

    private String completeTemplate(String format, List<Object> staticArguments, Object[] variableArguments) {
        Object[] args = new Object[staticArguments.size() + variableArguments.length];
        for (int i = 0, staticArgumentsSize = staticArguments.size(); i < staticArgumentsSize; i++) {
            args[i] = staticArguments.get(i);
        }
        for (int i = 0,
             variableArgumentsLength = variableArguments.length,
             staticArgumentsSize = staticArguments.size();
             i < variableArgumentsLength;
             i++) {
            args[i + staticArgumentsSize] = variableArguments[i];
        }
        return String.format(format, args);
    }
}
