package com.salesapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.server")
public class ServerConfig {
    
    private String host;
    private int port;
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public String getBaseUrl() {
        // ========== SERVER URL CONFIGURATION ==========
        // 🔧 DEPLOYMENT: Uncomment for production deployment
        return "https://saleapp-mspd.onrender.com";

        // 🔧 LOCAL: Uncomment for local development
        //return "http://" + host + ":" + port;
    }
}
