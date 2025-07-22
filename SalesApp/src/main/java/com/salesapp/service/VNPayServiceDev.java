package com.salesapp.service;

import com.salesapp.config.TimeZoneConfig;
import com.salesapp.config.VNPAYConfig;
import com.salesapp.dto.response.VNPayResponse;
import com.salesapp.exception.AppException;
import com.salesapp.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Primary // This will override the original VNPayService
@RequiredArgsConstructor
public class VNPayServiceDev {

    private final TimeZoneConfig timeZoneConfig;

    public String createOrder(HttpServletRequest request, long amount, String orderInfo, String returnUrl) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = VNPAYConfig.getRandomNumber(8);
        String vnp_IpAddr = VNPAYConfig.getIpAddress(request);
        String vnp_TmnCode = VNPAYConfig.vnp_TmnCode;
        String orderType = "order-type";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount)); // Amount đã được nhân 100 ở controller
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = "vn";
        vnp_Params.put("vnp_Locale", locate);

        returnUrl += returnUrl.contains("?") ? "&" : "?";
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        // ========== TIMEZONE CONFIGURATION FOR DEPLOYMENT ==========
        // Sử dụng TimeZoneConfig để đảm bảo timezone consistency
        TimeZoneConfig.debugTimezoneInfo("VNPay CreateOrder");
        
        TimeZone vietnamTimeZone = timeZoneConfig.vietnamTimeZone();
        Calendar cld = Calendar.getInstance(vietnamTimeZone);
        SimpleDateFormat formatter = timeZoneConfig.vnpayDateFormatter();
        
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // Sử dụng timeout từ config
        cld.add(Calendar.MINUTE, TimeZoneConfig.VNPAY_TIMEOUT_MINUTES);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        
        // Debug thông tin thời gian cho deployment
        System.out.println("=== VNPAY PAYMENT TIME DEBUG ===");
        System.out.println("Create Date: " + vnp_CreateDate);
        System.out.println("Expire Date: " + vnp_ExpireDate);
        System.out.println("Timeout Minutes: " + TimeZoneConfig.VNPAY_TIMEOUT_MINUTES);
        System.out.println("===============================");

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPAYConfig.hmacSHA512(VNPAYConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPAYConfig.vnp_PayUrl + "?" + queryUrl;

        // Debug logs for deployment
        System.out.println("=== VNPay URL Debug ===");
        System.out.println("Amount: " + amount);
        System.out.println("Order Info: " + orderInfo);
        System.out.println("Return URL: " + returnUrl);
        System.out.println("Payment URL: " + paymentUrl);
        System.out.println("======================");

        return paymentUrl;
    }

    public VNPayResponse orderReturn(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }
        
        String signValue = VNPAYConfig.hashAllFields(fields);
        System.out.printf("order info : " + request.getParameter("vnp_OrderInfo"));
        System.out.println("total money : " + request.getParameter("vnp_Amount"));
        
        // Debug signature information
        System.out.println("=== SIGNATURE DEBUG ===");
        System.out.println("VNPay SecureHash: " + vnp_SecureHash);
        System.out.println("Calculated Hash: " + signValue);
        System.out.println("Hash Match: " + signValue.equals(vnp_SecureHash));
        System.out.println("Response Code: " + request.getParameter("vnp_ResponseCode"));
        System.out.println("Transaction Status: " + request.getParameter("vnp_TransactionStatus"));
        System.out.println("=====================");

        int paymentStatus;
        
        // BYPASS SIGNATURE CHECK FOR DEVELOPMENT
        boolean isDevelopment = true; // Set to false in production
        boolean isValidSignature = signValue.equals(vnp_SecureHash) || isDevelopment;
        
        if (isValidSignature) {
            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
                System.out.println("PAYMENT SUCCESS - Transaction Status: 00");
                paymentStatus = 1;
            } else {
                System.out.println("PAYMENT FAILED - Transaction Status: " + request.getParameter("vnp_TransactionStatus"));
                paymentStatus = 0;
            }
        } else {
            paymentStatus = -1;
            System.out.println("SIGNATURE INVALID - Throwing exception");
            throw new AppException(ErrorCode.PAYMENT_INVALID_SIGN);
        }

        VNPayResponse response = VNPayResponse.builder()
                .orderInfo(request.getParameter("vnp_OrderInfo"))
                .paymentTime(request.getParameter("vnp_PayDate"))
                .transactionID(request.getParameter("vnp_TransactionNo"))
                .totalPrice(request.getParameter("vnp_Amount"))
                .status(paymentStatus)
                .build();
        return response;
    }
}
