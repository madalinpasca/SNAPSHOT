package com.madalin.wisetraveller.model.enums;

import com.madalin.wisetraveller.model.EmailTemplate;
import com.madalin.wisetraveller.service.EmailService;

public enum EmailType {
    Activation,
    ResetPassword;

    public EmailTemplate getTemplate() {
        EmailTemplate template = EmailService.EmailConfiguration.getTemplates().get(this.toString());
        if (template == null)
            throw new RuntimeException("Template not defined");
        return template;
    }
}
