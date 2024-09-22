package com.example.eCommerceApp1.controller;

import com.example.eCommerceApp1.dto.category.CategoryOutput;
import com.example.eCommerceApp1.dto.product.ProductsTemplateOutput;
import com.example.eCommerceApp1.service.CategoryService;
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
@RequestMapping("/api/v1/category")
public class CategoryController {
    private CategoryService categoryService;

    @Operation(summary = "Lấy ra category")
    @GetMapping("/get-category")
    public List<CategoryOutput> getCategory() {
        return categoryService.getCategories();
    }

    @Operation(summary = "Thêm category vào product")
    @PostMapping("/add-category-to-template")
    public void addCategoryToTemplate(@RequestHeader("Authorization") String accessToken,
                                      @RequestParam Long productTemplateId,
                                      @RequestParam Long categoryId) {
        categoryService.addCategoryToTemplate(accessToken, productTemplateId, categoryId);
    }

    @Operation(summary = "Xóa category của product")
    @DeleteMapping("/delete-category-from-product")
    public void deleteCategoryFromProduct(@RequestHeader("Authorization") String accessToken,
                                          @RequestParam Long productTemplateId,
                                          @RequestParam Long categoryId) {
        categoryService.deleteCategoryFromTemplate(accessToken, productTemplateId, categoryId);
    }

    @Operation(summary = "Lấy ra sản phẩm theo category")
    @GetMapping("/get-template-by-category")
    public Page<ProductsTemplateOutput> getTemplateByCategory(@RequestParam Long categoryId,
                                                              @ParameterObject Pageable pageable) {
        return categoryService.getProductsByCategory(categoryId, pageable);
    }
}
