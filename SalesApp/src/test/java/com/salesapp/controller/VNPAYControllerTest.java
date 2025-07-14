package com.salesapp.controller;

import com.salesapp.controller.v1.VNPAYController;
import com.salesapp.service.OrderService;
import com.salesapp.service.VNPAYService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VNPAYController.class)
public class VNPAYControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VNPAYService vnPayService;

    @MockBean
    private OrderService orderService;

    @Test
    public void testCreatePayment() throws Exception {
        // Mock VNPay service response
        String mockPaymentUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=10000000&vnp_Command=pay";
        when(vnPayService.createOrder(any(), anyInt(), anyString(), anyString()))
                .thenReturn(mockPaymentUrl);

        mockMvc.perform(post("/v1/vnpay/create-payment")
                        .param("amount", "100000")
                        .param("orderInfo", "Test Order")
                        .param("returnUrl", "http://localhost:3000/result"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(1000))
                .andExpect(jsonPath("$.message").value("VNPay payment URL created successfully"))
                .andExpect(jsonPath("$.data").value(mockPaymentUrl));
    }
}
