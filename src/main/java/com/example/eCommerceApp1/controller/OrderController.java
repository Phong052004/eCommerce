package com.example.eCommerceApp1.controller;

import com.example.eCommerceApp1.dto.order.OrderInput;
import com.example.eCommerceApp1.dto.order.OrderOutput;
import com.example.eCommerceApp1.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Đặt hàng")
    @PostMapping("/order")
    public void orderProducts(@RequestHeader("Authorization") String accessToken,
                              @RequestBody OrderInput orderInput) {
        orderService.orderProducts(accessToken,orderInput);
    }

    @Operation(summary = "Xem đơn hàng")
    @GetMapping("/get-orders")
    public Page<OrderOutput> getAllOrder(@RequestHeader("Authorization") String accessToken,
                                         @ParameterObject Pageable pageable) {
        return orderService.getAllProductOrder(accessToken, pageable);
    }

    @Operation(summary = "Lấy đơn hàng theo trạng thái")
    @GetMapping("/get-orders-by-state")
    public Page<OrderOutput> getOrdersByState(@RequestHeader("Authorization") String accessToken,
                                                      @RequestParam String state,
                                                      @ParameterObject Pageable pageable) {
        return orderService.getProductsByState(accessToken, pageable, state);
    }

    @Operation(summary = "Hủy đơn hàng")
    @PostMapping("/cancel")
    public void cancelOrder(@RequestHeader("Authorization") String accessToken,
                            @RequestParam Long orderId) {
        orderService.cancelOrder(accessToken, orderId);
    }
}
