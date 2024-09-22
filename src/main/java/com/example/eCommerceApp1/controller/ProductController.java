package com.example.eCommerceApp1.controller;

import com.example.eCommerceApp1.dto.product.*;
import com.example.eCommerceApp1.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/v1/product")
public class ProductController {
    private final ProductService productService;

    @Operation(summary = "Tạo ra sản phẩm mẫu")
    @PostMapping("/create-template")
    public void createProductTemplate(@RequestHeader("Authorization") String accessToken,
                                      @RequestBody ProductTemplateInput productTemplateInput) {
        productService.createProductTemplate(accessToken, productTemplateInput);
    }

    @Operation(summary = "Thêm thuộc tính")
    @PostMapping("/create-attribute")
    public void createAttributeAndAttributeValue(@RequestHeader("Authorization") String accessToken,
                                @RequestParam Long productTemplateId,
                                @RequestBody List<AttributeInput> attributeInputs) {
        productService.createAttributeAndAttributeValue(accessToken, productTemplateId, attributeInputs);
    }

    @Operation(summary = "Tạo ra sản phẩm con")
    @PostMapping("/create")
    public void createProducts(@RequestHeader("Authorization") String accessToken,
                               @RequestBody List<ProductInput> productInputs,
                               @RequestParam Long productTemplateId) {
        productService.createProducts(accessToken,productInputs, productTemplateId);
    }

    @Operation(summary = "Update sản phẩm sau khi thay đổi attribute")
    @PostMapping("/change-product-by-attribute")
    public void changeProductAttributeAfterChangingAttribute(@RequestHeader("Authorization") String accessToken,
                                                             @RequestBody List<ProductInput> productInputs,
                                                             @RequestParam Long productTemplateId) {
        productService.changeProductsAfterChangingAttribute(accessToken, productInputs, productTemplateId);
    }

    @Operation(summary = "Update sản phẩm")
    @PostMapping("/update")
    public void updateProducts(@RequestHeader("Authorization") String accessToken,
                               @RequestBody List<ProductInput> productInputs,
                               @RequestParam Long productTemplateId) {
        productService.updateProduct(accessToken, productInputs, productTemplateId);
    }

    @Operation(summary = "Thay đổi thông tin sản phẩm template")
    @PostMapping("/update-product-template")
    public void updateProductTemplate(@RequestHeader("Authorization") String accessToken,
                                      @RequestParam Long productTemplateId,
                                      @RequestBody ProductTemplateInput productTemplateInput) {
        productService.updateProductTemplate(accessToken,productTemplateId, productTemplateInput);
    }

    @Operation(summary = "Thêm attribute value")
    @PostMapping("/add-attribute-value")
    public void addAttributeValue(@RequestHeader("Authorization") String accessToken,
                                  @RequestParam Long productTemplateId,
                                  @RequestParam Long attributeId,
                                  @RequestBody List<AttributeValueInput> attributeValueInputs) {
        productService.addAttributeValue(accessToken, productTemplateId,attributeId, attributeValueInputs);
    }

    @Operation(summary = "Lấy ra thuộc tính của app")
    @GetMapping("/get-attribute-app")
    public List<AttributeOutput> getAttributeApp(@RequestHeader("Authorization") String accessToken) {
        return productService.getAttributeApp(accessToken);
    }

    @Operation(summary = "Lấy ra thuộc tính của shop")
    @GetMapping("/get-attribute-template")
    public List<AttributeOutput> getAttributeProduct(@RequestHeader("Authorization") String accessToken,
                                                     @RequestParam Long productTemplateId) {
        return productService.getAttributeProduct(accessToken, productTemplateId);
    }

    @Operation(summary = "Lấy ra thuộc tính giá trị")
    @GetMapping("/get-attribute-value-app")
    public List<AttributeValueOutput> getAttributeValueOfApp(@RequestHeader("Authorization") String accessToken,
                                                             @RequestParam Long attributeId) {
        return productService.getAttributeValueOfApp(accessToken, attributeId);
    }

    @Operation(summary = "Lấy ra attribute của product")
    @GetMapping("/get-attribute-value-template")
    public List<AttributeValueOutput> getAttributeValueOfProduct(@RequestHeader("Authorization") String accessToken,
                                                                 @RequestParam Long productTemplateId) {
        return productService.getAttributeValueOfProduct(accessToken, productTemplateId);
    }

    @Operation(summary = "Lấy ra sản phẩm template")
    @GetMapping("/get-products-template")
    public Page<ProductsTemplateOutput> getProductTemplate(@RequestParam Long shopId,
                                                           @ParameterObject Pageable pageable) {
        return productService.getProductsTemplate(shopId,pageable);
    }

    @Operation(summary = "Lấy ra sản phảm con")
    @GetMapping("/get-products")
    public List<ProductOutput> getProducts(@RequestParam Long productTemplateId) {
        return productService.getProducts(productTemplateId);
    }

    @Operation(summary = "Lấy ra sản phảm con theo thuộc tính")
    @GetMapping("/get-product")
    public ProductOutput getProductByAttributeValue(@RequestParam List<Long> attributeValueIds,
                                                    @RequestParam Long productTemplateId) {
        return productService.getProductByAttributeValues(attributeValueIds, productTemplateId);
    }

    @Operation(summary = "Lấy attribute và attributeValue trước khi tạo or update product")
    @GetMapping("/get-attribute-and-attribute-value")
    public List<AttributeOutput> getAttributeAndAttributeValue(@RequestParam Long productTemplateId,
                                                               @RequestHeader("Authorization") String accessToken) {
        return productService.getAttributeAndAttributeValueOfProduct(accessToken, productTemplateId);
    }

    @Operation(summary = "Xóa sản phẩm")
    @DeleteMapping("/delete-product-template")
    public void deleteProductTemplate(@RequestHeader("Authorization") String accessToken,
                                      @RequestParam Long productTemplateId) {
        productService.deleteProductTemplate(accessToken, productTemplateId);
    }

    @Operation(summary = "Xóa attribute shop")
    @DeleteMapping("/delete-attribute-shop")
    public void deleteAttributeShop(@RequestHeader("Authorization") String accessToken,
                                    @RequestParam Long productTemplateId,
                                    @RequestParam List<Long> attributeIds) {
        productService.deleteAttributeOfShop(accessToken, productTemplateId, attributeIds);
    }

    @Operation(summary = "Xóa attributeValue Shop")
    @DeleteMapping("/delete-attribute-value-shop")
    public void deleteAttributeValueShop(@RequestHeader("Authorization") String accessToken,
                                         @RequestParam Long productTemplateId,
                                         @RequestParam Long attributeId,
                                         @RequestParam List<Long> attributeValueIds) {
        productService.deleteAttributeValueOfShop(accessToken, productTemplateId, attributeId, attributeValueIds);
    }

    @Operation(summary = "Tìm kiếm sản phẩm")
    @GetMapping("/search")
    public Page<ProductsTemplateOutput> searchProductsTemplate(@RequestParam String search,
                                                               @ParameterObject Pageable pageable) {
        return productService.searchProductsTemplateBy(search, pageable);
    }

}