package ru.geekbrains.WowVendorTeamHelper.dto;

import java.util.Map;

public class EmailContext {

    private String from;
    private String to;
    private String subject;
    private String email;
    private String template;
    private Map<String, Object> properties;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    private EmailContext() {
    }

    public static Builder createBuilder() {
        return new Builder();
    }

    public static class Builder {

        private final EmailContext emailContext;

        private Builder() {
            this.emailContext = new EmailContext();
        }

        public Builder withFrom(String from) {
            this.emailContext.from = from;
            return this;
        }

        public Builder withTo(String to) {
            this.emailContext.to = to;
            return this;
        }

        public Builder withSubject(String subject) {
            this.emailContext.subject = subject;
            return this;
        }

        public Builder withEmail(String email) {
            this.emailContext.email = email;
            return this;
        }

        public Builder withTemplate(String template) {
            this.emailContext.template = template;
            return this;
        }

        public Builder withProperties(Map<String, Object> properties) {
            this.emailContext.properties = properties;
            return this;
        }

        public EmailContext build() {
            return this.emailContext;
        }
    }
}
