package com.example.eCommerceApp1.service;

import com.example.eCommerceApp1.common.Common;
import com.example.eCommerceApp1.dto.category.CategoryOutput;
import com.example.eCommerceApp1.dto.product.ProductsTemplateOutput;
import com.example.eCommerceApp1.enitty.CategoryEntity;
import com.example.eCommerceApp1.enitty.TemplateCategoryMapEntity;
import com.example.eCommerceApp1.enitty.UserEntity;
import com.example.eCommerceApp1.enitty.product.ProductTemplateEntity;
import com.example.eCommerceApp1.repository.CategoryRepository;
import com.example.eCommerceApp1.repository.CustomRepository;
import com.example.eCommerceApp1.repository.TemplateCategoryMapRepository;
import com.example.eCommerceApp1.repository.product.ProductTemplateRepository;
import com.example.eCommerceApp1.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final TemplateCategoryMapRepository templateCategoryMapRepository;
    private final ProductTemplateRepository productTemplateRepository;
    private final CustomRepository customRepository;

    @Transactional
    public List<CategoryOutput> getCategories() {
        List<CategoryEntity> categoryEntities = categoryRepository.findAll();
        List<CategoryOutput> categoryOutputs = new ArrayList<>();
        for (CategoryEntity categoryEntity : categoryEntities) {
            CategoryOutput categoryOutput = CategoryOutput.builder()
                    .categoryId(categoryEntity.getId())
                    .categoryName(categoryEntity.getName())
                    .build();
            categoryOutputs.add(categoryOutput);
        }
        return categoryOutputs;
    }

    @Transactional
    public void addCategoryToTemplate(String accessToken, Long productTemplateId, Long categoryId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if (shopEntity.getIsShop().equals(Boolean.TRUE)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        ProductTemplateEntity productTemplateEntity = customRepository.getProductTemplateBy(productTemplateId);
        if (!productTemplateEntity.getShopId().equals(shopId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        TemplateCategoryMapEntity templateCategoryMapEntity = TemplateCategoryMapEntity.builder()
                .categoryId(categoryId)
                .templateProductId(productTemplateId)
                .build();

        templateCategoryMapRepository.save(templateCategoryMapEntity);
    }

    @Transactional
    public void deleteCategoryFromTemplate(String accessToken, Long productTemplateId, Long categoryId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if (shopEntity.getIsShop().equals(Boolean.TRUE)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        ProductTemplateEntity productTemplateEntity = customRepository.getProductTemplateBy(productTemplateId);
        if (!productTemplateEntity.getShopId().equals(shopId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        TemplateCategoryMapEntity templateCategoryMapEntity = templateCategoryMapRepository
                .findByCategoryIdAndTemplateProductId(categoryId, productTemplateId);
        if (Objects.isNull(templateCategoryMapEntity)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        templateCategoryMapRepository.delete(templateCategoryMapEntity);
    }

    @Transactional(readOnly = true)
    public Page<ProductsTemplateOutput> getProductsByCategory(Long categoryId, Pageable pageable) {
        List<TemplateCategoryMapEntity> templateCategoryMapEntityPage = templateCategoryMapRepository
                .findAllByCategoryId(categoryId);

        List<Long> productTemplateIds= templateCategoryMapEntityPage
                .stream().map(TemplateCategoryMapEntity::getTemplateProductId).collect(Collectors.toList());

        Page<ProductTemplateEntity> productTemplateEntityPage = productTemplateRepository
                .findAllByIdIn(productTemplateIds, pageable);
        if (Objects.isNull(productTemplateEntityPage) || productTemplateEntityPage.isEmpty()) {
            return Page.empty();
        }

        return productTemplateEntityPage.map(
                productTemplateEntity -> {
                    ProductsTemplateOutput productsTemplateOutput = ProductsTemplateOutput.builder()
                            .productTemplateId(productTemplateEntity.getId())
                            .name(productTemplateEntity.getName())
                            .minPrice(productTemplateEntity.getMinPrice())
                            .maxPrice(productTemplateEntity.getMaxPrice())
                            .description(productTemplateEntity.getDescription())
                            .quantity(productTemplateEntity.getQuantity())
                            .avatarImage(productTemplateEntity.getAvatarImage())
                            .soldCount(productTemplateEntity.getSold())
                            .averageRate(productTemplateEntity.getAverageRating())
                            .build();
                    return productsTemplateOutput;
                }
        );
    }
}
