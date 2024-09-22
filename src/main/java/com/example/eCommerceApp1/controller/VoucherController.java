package com.example.eCommerceApp1.controller;

import com.example.eCommerceApp1.dto.voucher.VoucherInput;
import com.example.eCommerceApp1.dto.voucher.VoucherOutput;
import com.example.eCommerceApp1.service.VoucherService;
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
@RequestMapping("/api/v1/voucher")
public class VoucherController {
    private final VoucherService voucherService;

    @Operation(summary = "Tạo voucher shop")
    @PostMapping("/create-voucher-shop")
    public void createVoucherShop(@RequestHeader("Authorization") String accessToken,
                                  @RequestBody VoucherInput voucherInput) {
        voucherService.createVoucherShop(accessToken, voucherInput);
    }

    @Operation(summary = "Tạo voucher sản phẩm")
    @PostMapping("/create-voucher-product")
    public void createVoucherProduct(@RequestHeader("Authorization") String accessToken,
                                     @RequestBody VoucherInput voucherInput,
                                     @RequestParam Long productTemplateId) {
        voucherService.createVoucherProduct(accessToken, voucherInput, productTemplateId);
    }

    @Operation(summary = "Lấy ra voucher shop")
    @GetMapping("/get-voucher-shop")
    public List<VoucherOutput> getVoucherShop(@RequestParam Long shopId) {
        return voucherService.getVoucherShop(shopId);
    }

    @Operation(summary = "Thêm voucher vào kho voucher")
    @PostMapping("/add-voucher-shop")
    public void addVoucherShopByUser(@RequestHeader("Authorization") String accessToken,
                                     @RequestParam Long voucherId) {
        voucherService.addVoucherShopByUser(accessToken, voucherId);
    }

    @Operation(summary = "Lấy ra voucher shop trong kho")
    @GetMapping("/get-voucher-shop-by-user")
    public Page<VoucherOutput> getVoucherShopByUser(@RequestHeader("Authorization") String accessToken,
                                                    @ParameterObject Pageable pageable) {
        return voucherService.getVoucherShopByUser(accessToken, pageable);
    }

    @Operation(summary = "Xóa voucher shop")
    @DeleteMapping("/delete-voucher-shop")
    public void deleteVoucherShop(@RequestHeader("Authorization") String accessToken,
                                  @RequestParam Long voucherId) {
        voucherService.deleteVoucherShop(accessToken, voucherId);
    }

    @Operation(summary = "Xóa voucher sản phẩm")
    @DeleteMapping("/delete-voucher-product")
    public void deleteVoucherProduct(@RequestParam Long productTemplateId,
                                     @RequestHeader("Authorization") String accessToken) {
        voucherService.deleteVoucherProduct(accessToken, productTemplateId);
    }

    @Operation(summary = "Lấy voucher theo mã code")
    @GetMapping("/get-voucher-by-code")
    public VoucherOutput searchVoucherByCode(@RequestParam String code) {
        return voucherService.searchVoucherShop(code);
    }
}
