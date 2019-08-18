package com.madalin.wisetraveller.model;

import com.madalin.wisetraveller.model.enums.EmailType;
import lombok.Getter;

@Getter
public class Email {
    private Email() {}

    private EmailType type;

    public Object[] contentArgs = new Object[0];

    public Object[] subjectArgs = new Object[0];

    public String getSubject() {
        return type.getTemplate().getSubject(subjectArgs);
    }

    public String getContent() {
        return type.getTemplate().getContent(contentArgs);
    }

    public static class Builder {
        private Email target = new Email();

        public Builder(EmailType type) {
            target.type = type;
        }

        public Builder contentArguments(Object... args) {
            target.contentArgs = args;
            return this;
        }

        public Builder subjectArguments(Object... args) {
            target.subjectArgs = args;
            return this;
        }

        public Email build() {
            return target;
        }
    }
}
