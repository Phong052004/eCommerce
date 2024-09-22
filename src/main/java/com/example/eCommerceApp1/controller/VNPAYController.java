package com.example.eCommerceApp1.controller;

import com.example.eCommerceApp1.service.VNPAYService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/payment")
public class VNPAYController {
    private final VNPAYService vnpayService;

    @PostMapping("/submitOrder")
    public String submitOrder(@RequestParam int orderTotal,
                              @RequestParam String orderInfo,
                              HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/api/v1/payment";
        return vnpayService.createOrder(request, orderTotal, orderInfo, baseUrl);
    }

    @GetMapping("/vnpay-payment-return")
    public String paymentCompleted(HttpServletRequest request) {
        int paymentStatus = vnpayService.orderReturn(request);

        return paymentStatus == 1 ? "orderSuccess" : "orderFail";
    }
}
