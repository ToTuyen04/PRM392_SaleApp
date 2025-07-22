package com.salesapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * Configuration class để quản lý timezone cho VNPay payment
 * Đảm bảo consistency khi deploy lên các môi trường khác nhau
 */
@Configuration
public class TimeZoneConfig {
    
    // Timezone của Việt Nam
    public static final String VIETNAM_TIMEZONE_ID = "Asia/Ho_Chi_Minh";
    public static final String VNPAY_DATE_FORMAT = "yyyyMMddHHmmss";
    
    // VNPay timeout (phút)
    public static final int VNPAY_TIMEOUT_MINUTES = 30; // Tăng lên 30 phút cho deployment
    
    /**
     * Bean cung cấp TimeZone Việt Nam
     */
    @Bean(name = "vietnamTimeZone")
    public TimeZone vietnamTimeZone() {
        return TimeZone.getTimeZone(VIETNAM_TIMEZONE_ID);
    }
    
    /**
     * Bean cung cấp ZoneId Việt Nam cho Java 8+ time API
     */
    @Bean(name = "vietnamZoneId")
    public ZoneId vietnamZoneId() {
        return ZoneId.of(VIETNAM_TIMEZONE_ID);
    }
    
    /**
     * Bean cung cấp SimpleDateFormat với timezone Việt Nam
     */
    @Bean(name = "vnpayDateFormatter")
    public SimpleDateFormat vnpayDateFormatter() {
        SimpleDateFormat formatter = new SimpleDateFormat(VNPAY_DATE_FORMAT);
        formatter.setTimeZone(vietnamTimeZone());
        return formatter;
    }
    
    /**
     * Bean cung cấp DateTimeFormatter cho Java 8+ với timezone Việt Nam
     */
    @Bean(name = "vnpayDateTimeFormatter")
    public DateTimeFormatter vnpayDateTimeFormatter() {
        return DateTimeFormatter.ofPattern(VNPAY_DATE_FORMAT)
                .withZone(vietnamZoneId());
    }
    
    /**
     * Utility method để debug timezone information
     */
    public static void debugTimezoneInfo(String context) {
        System.out.println("=== TIMEZONE DEBUG [" + context + "] ===");
        System.out.println("System Default Timezone: " + TimeZone.getDefault().getID());
        System.out.println("Vietnam Timezone: " + VIETNAM_TIMEZONE_ID);
        
        TimeZone vnTimeZone = TimeZone.getTimeZone(VIETNAM_TIMEZONE_ID);
        long currentTime = System.currentTimeMillis();
        int offsetHours = vnTimeZone.getOffset(currentTime) / (1000 * 60 * 60);
        
        System.out.println("Vietnam GMT Offset: UTC+" + offsetHours);
        System.out.println("VNPay Timeout: " + VNPAY_TIMEOUT_MINUTES + " minutes");
        System.out.println("Current Time (Vietnam): " + 
            new SimpleDateFormat(VNPAY_DATE_FORMAT).format(currentTime));
        System.out.println("======================================");
    }
}
