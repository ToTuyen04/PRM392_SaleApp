package com.salesapp.controller;

import com.salesapp.controller.v1.VNPAYController;
import com.salesapp.service.OrderService;
import com.salesapp.service.VNPAYService;
import com.salesapp.dto.response.VNPayResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VNPAYController.class)
public class VNPayCallbackTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VNPAYService vnPayService;

    @MockBean
    private OrderService orderService;

    @Test
    public void testSuccessfulPaymentCallback() throws Exception {
        // Mock VNPay response - Thanh toán thành công
        VNPayResponse mockResponse = VNPayResponse.builder()
                .orderInfo("123")
                .paymentTime("20250710202150")
                .transactionID("15066816")
                .totalPrice("10000000")
                .status(1) // Thành công
                .build();

        when(vnPayService.orderReturn(any())).thenReturn(mockResponse);

        // Test callback với parameters giống VNPay thật
        mockMvc.perform(get("/v1/vnpay/payment-callback")
                        .param("vnp_Amount", "10000000")
                        .param("vnp_BankCode", "NCB")
                        .param("vnp_BankTranNo", "VNP15066816")
                        .param("vnp_CardType", "ATM")
                        .param("vnp_OrderInfo", "123")
                        .param("vnp_PayDate", "20250710202150")
                        .param("vnp_ResponseCode", "00")
                        .param("vnp_TmnCode", "I4CFWC18")
                        .param("vnp_TransactionNo", "15066816")
                        .param("vnp_TransactionStatus", "00")
                        .param("vnp_TxnRef", "17236222")
                        .param("vnp_SecureHash", "valid_hash"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(1000))
                .andExpect(jsonPath("$.message").value("Payment successful"))
                .andExpect(jsonPath("$.data.status").value(1));
    }

    @Test
    public void testFailedPaymentCallback() throws Exception {
        // Mock VNPay response - Thanh toán thất bại
        VNPayResponse mockResponse = VNPayResponse.builder()
                .orderInfo("123")
                .paymentTime("20250710202150")
                .transactionID("15066816")
                .totalPrice("10000000")
                .status(0) // Thất bại
                .build();

        when(vnPayService.orderReturn(any())).thenReturn(mockResponse);

        mockMvc.perform(get("/v1/vnpay/payment-callback")
                        .param("vnp_Amount", "10000000")
                        .param("vnp_OrderInfo", "123")
                        .param("vnp_ResponseCode", "24") // Lỗi
                        .param("vnp_TransactionStatus", "02"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(2000))
                .andExpect(jsonPath("$.message").value("Payment failed"));
    }
}
