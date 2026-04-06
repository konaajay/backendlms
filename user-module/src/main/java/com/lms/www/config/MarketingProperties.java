package com.lms.www.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "marketing")
public class MarketingProperties {
    private Firebase firebase = new Firebase();
    private Resend resend = new Resend();

    public Firebase getFirebase() { return firebase; }
    public void setFirebase(Firebase firebase) { this.firebase = firebase; }

    public Resend getResend() { return resend; }
    public void setResend(Resend resend) { this.resend = resend; }

    public static class Firebase {
        private String serverKey;
        private String projectName;

        public String getServerKey() { return serverKey; }
        public void setServerKey(String serverKey) { this.serverKey = serverKey; }
        public String getProjectName() { return projectName; }
        public void setProjectName(String projectName) { this.projectName = projectName; }
    }

    public static class Resend {
        private String apiKey;
        private String fromEmail;

        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getFromEmail() { return fromEmail; }
        public void setFromEmail(String fromEmail) { this.fromEmail = fromEmail; }
    }
}
