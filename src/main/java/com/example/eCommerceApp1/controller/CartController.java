package com.example.eCommerceApp1.controller;

import com.example.eCommerceApp1.dto.cart.ChangeProductInput;
import com.example.eCommerceApp1.dto.cart.ProductCartInput;
import com.example.eCommerceApp1.dto.cart.ProductCartMapOutput;
import com.example.eCommerceApp1.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/cart")
public class CartController {
    private final CartService cartService;

    @Operation(summary = "Thêm sản phẩm vào giỏ hàng")
    @PostMapping("/add")
    public void addProductToCart(@RequestHeader("Authorization") String accessToken,
                                 @RequestBody ProductCartInput productCartInput) {
        cartService.addProductToCart(accessToken, productCartInput);
    }

    @Operation(summary = "Thay đổi sản phẩm trong cart")
    @PostMapping("/change")
    public void changeProductInCart(@RequestHeader("Authorization") String accessToken,
                                    @RequestBody ChangeProductInput changeProductInput) {
        cartService.changeProductInCart(accessToken, changeProductInput);
    }

    @Operation(summary = "Xóa product trong cart")
    @DeleteMapping("/delete")
    public void deleteProductInCart(@RequestHeader("Authorization") String accessToken,
                                    @RequestParam Long cartId) {
        cartService.removeProductFromCart(accessToken, cartId);
    }

    @Operation(summary = "Lấy ra sản phẩm trong cart")
    @GetMapping("/get")
    public Page<ProductCartMapOutput> getProductsInCart(@RequestHeader("Authorization") String accessToken,
                                                        @ParameterObject Pageable pageable) {
        return cartService.getProductCartMapBy(accessToken, pageable);
    }

    @Operation(summary = "Lấy ra sản phẩm chuẩn bị order")
    @GetMapping("/get-prodcuts-before-ordering")
    public List<ProductCartMapOutput> getProductsInCartBeforeOrdering(@RequestHeader("Authorization") String accessToken,
                                                                      @RequestParam List<Long> cartIds) {
        return cartService.getProductBeforeOrdering(accessToken, cartIds);
    }
}
