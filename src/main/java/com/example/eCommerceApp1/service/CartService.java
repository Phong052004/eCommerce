package com.example.eCommerceApp1.service;

import com.example.eCommerceApp1.common.Common;
import com.example.eCommerceApp1.dto.cart.ChangeProductInput;
import com.example.eCommerceApp1.dto.cart.ProductCartInput;
import com.example.eCommerceApp1.dto.cart.ProductCartMapOutput;
import com.example.eCommerceApp1.dto.cart.ProductsOfShopOutput;
import com.example.eCommerceApp1.enitty.ProductCartMapEntity;
import com.example.eCommerceApp1.enitty.UserEntity;
import com.example.eCommerceApp1.enitty.VoucherEntity;
import com.example.eCommerceApp1.enitty.product.ProductAttributeValueMapEntity;
import com.example.eCommerceApp1.enitty.product.ProductEntity;
import com.example.eCommerceApp1.mapper.ProductCartMapper;
import com.example.eCommerceApp1.repository.CustomRepository;
import com.example.eCommerceApp1.repository.ProductCartMapRepository;
import com.example.eCommerceApp1.repository.UserRepository;
import com.example.eCommerceApp1.repository.VoucherRepository;
import com.example.eCommerceApp1.repository.product.ProductAttributeValueMapRepository;
import com.example.eCommerceApp1.repository.product.ProductRepository;
import com.example.eCommerceApp1.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CartService {
    private final ProductCartMapRepository productCartMapRepository;
    private final ProductCartMapper productCartMapper;
    private final CustomRepository customRepository;
    private final ProductAttributeValueMapRepository productAttributeValueMapRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final VoucherRepository voucherRepository;

    @Transactional
    public void addProductToCart(String accessToken, ProductCartInput productCartInput) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        ProductCartMapEntity productCartMapEntityExist = productCartMapRepository
                .findByProductIdAndUserId(productCartInput.getProductId(), userId);
        if (productCartMapEntityExist != null) {
            productCartMapEntityExist.setQuantityOrder(
                    productCartInput.getQuantityOrder() + productCartMapEntityExist.getQuantityOrder()
            );
            productCartMapEntityExist.setCreatedAt(LocalDateTime.now());
            productCartMapRepository.save(productCartMapEntityExist);
        } else {
            ProductCartMapEntity productCartMapEntity = productCartMapper.getEntityFromInput(productCartInput);
            productCartMapEntity.setUserId(userId);
            productCartMapEntity.setCreatedAt(LocalDateTime.now());
            productCartMapRepository.save(productCartMapEntity);
        }
    }

    @Transactional
    public void changeProductInCart(String accessToken, ChangeProductInput changeProductInput) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        ProductCartMapEntity productCartMapEntity = customRepository.getProductCartMapBy(changeProductInput.getCartId());
        if (productCartMapEntity == null || !productCartMapEntity.getUserId().equals(userId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<ProductAttributeValueMapEntity> productAttributeValueMapEntities = productAttributeValueMapRepository
                .findAllByProductId(productCartMapEntity.getProductId());
        if (!productAttributeValueMapEntities.get(0).getProductTemplateId().equals(productCartMapEntity.getProductTemplateId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        ProductEntity productEntity = customRepository.getProductBy(productCartMapEntity.getProductId());
        productCartMapEntity.setProductId(productEntity.getId());
        productCartMapEntity.setQuantityOrder(changeProductInput.getOrderQuantity());
        productCartMapEntity.setCreatedAt(LocalDateTime.now());
        productCartMapRepository.save(productCartMapEntity);
    }

    @Transactional
    public void removeProductFromCart(String accessToken, Long cartId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        ProductCartMapEntity productCartMapEntity = customRepository.getProductCartMapBy(cartId);
        if (productCartMapEntity == null || !productCartMapEntity.getUserId().equals(userId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        productCartMapRepository.delete(productCartMapEntity);
    }

    @Transactional(readOnly = true)
    public Page<ProductCartMapOutput> getProductCartMapBy(String accessToken, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        Page<ProductCartMapEntity> productCartMapEntityPage = productCartMapRepository.findAllByUserId(userId, pageable);

        if (Objects.isNull(productCartMapEntityPage) || productCartMapEntityPage.isEmpty()) {
            return Page.empty();
        }
        Set<Long> shopIds = productCartMapEntityPage.stream()
                .map(ProductCartMapEntity::getShopId)
                .collect(Collectors.toSet());

        Map<Long, ProductEntity> productEntityMap = productRepository.findAllByIdIn(
                productCartMapEntityPage.stream().map(ProductCartMapEntity::getProductId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(ProductEntity::getId, Function.identity()));
        Map<Long, UserEntity> shopEntityMap = userRepository.findAllByIdIn(shopIds)
                .stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        Map<Long, List<ProductCartMapEntity>> shoppingCartMap = productCartMapEntityPage.stream()
                .collect(Collectors.groupingBy(ProductCartMapEntity::getShopId));

        List<ProductCartMapOutput> shoppingCartOutputs = new ArrayList<>();
        for(Long shopId : shopIds) {
            List<ProductCartMapEntity> productCartEntityList = shoppingCartMap.get(shopId);
            List<ProductsOfShopOutput> productsOfShopOutputList = new ArrayList<>();
            UserEntity shopEntity = shopEntityMap.get(shopId);
            productCartEntityList.forEach(
                    productCartMapEntity -> {
                        ProductEntity productEntity = productEntityMap.get(productCartMapEntity.getProductId());
                        ProductsOfShopOutput productsOfShopOutput = ProductsOfShopOutput.builder()
                                .productTemplateId(productCartMapEntity.getProductTemplateId())
                                .productId(productCartMapEntity.getProductId())
                                .productName(productEntity.getName())
                                .price(productEntity.getPrice())
                                .quantityOrder(productCartMapEntity.getQuantityOrder())
                                .imageUrl(productEntity.getImageUrl())
                                .totalPrice(productEntity.getPrice() * productCartMapEntity.getQuantityOrder())
                                .build();

                        productsOfShopOutputList.add(productsOfShopOutput);
                    }
            );

            ProductCartMapOutput productCartMapOutput = ProductCartMapOutput.builder()
                    .shopId(shopId)
                    .nameShop(shopEntity.getFullName())
                    .productsOutput(productsOfShopOutputList)
                    .build();

            shoppingCartOutputs.add(productCartMapOutput);
        }
        return new PageImpl<>(shoppingCartOutputs, pageable, productCartMapEntityPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<ProductCartMapOutput> getProductBeforeOrdering(String accessToken, List<Long> cartIds) {
        Long userId =  TokenHelper.getUserIdFromToken(accessToken);
        List<ProductCartMapEntity> productCartMapEntities = productCartMapRepository
                .findAllByIdIn(cartIds);
        for (ProductCartMapEntity productCartMapEntity : productCartMapEntities) {
            if (!productCartMapEntity.getUserId().equals(userId)) {
                throw new RuntimeException(Common.ACTION_FAIL);
            }
        }

        Set<Long> productTemplateIds = productCartMapEntities.stream().map(
                ProductCartMapEntity::getProductTemplateId
        ).collect(Collectors.toSet());

        Set<Long> shopIds = productCartMapEntities.stream()
                .map(ProductCartMapEntity::getShopId)
                .collect(Collectors.toSet());

        Map<Long, VoucherEntity> voucherEntityMap = voucherRepository.findAllByProductTemplateIdIn(
                productTemplateIds
        ).stream().collect(Collectors.toMap(VoucherEntity::getProductTemplateId, Function.identity()));

        Map<Long, ProductEntity> productEntityMap = productRepository.findAllByIdIn(
                productCartMapEntities.stream().map(ProductCartMapEntity::getProductId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(ProductEntity::getId, Function.identity()));

        Map<Long, UserEntity> shopEntityMap = userRepository.findAllByIdIn(shopIds)
                .stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        Map<Long, List<ProductCartMapEntity>> shoppingCartMap = productCartMapEntities.stream()
                .collect(Collectors.groupingBy(ProductCartMapEntity::getShopId));

        List<ProductCartMapOutput> shoppingCartOutputs = new ArrayList<>();
        for(Long shopId : shopIds) {
            List<ProductCartMapEntity> productCartEntityList = shoppingCartMap.get(shopId);
            List<ProductsOfShopOutput> productsOfShopOutputList = new ArrayList<>();
            UserEntity shopEntity = shopEntityMap.get(shopId);
            int totalPrice = 0;
            for(ProductCartMapEntity productCartMapEntity : productCartEntityList) {
                VoucherEntity voucherEntity = voucherEntityMap.get(productCartMapEntity.getProductTemplateId());
                ProductEntity productEntity = productEntityMap.get(productCartMapEntity.getProductId());
                int price = 0;
                if (voucherEntity != null) {
                    price = (int)Math.round(productEntity.getPrice() * (1 - voucherEntity.getSaleOff()));
                } else {
                    price = productEntity.getPrice();
                }
                ProductsOfShopOutput productsOfShopOutput = ProductsOfShopOutput.builder()
                        .productTemplateId(productCartMapEntity.getProductTemplateId())
                        .productId(productCartMapEntity.getProductId())
                        .productName(productEntity.getName())
                        .price(price)
                        .quantityOrder(productCartMapEntity.getQuantityOrder())
                        .imageUrl(productEntity.getImageUrl())
                        .totalPrice(price * productCartMapEntity.getQuantityOrder())
                        .build();

                productsOfShopOutputList.add(productsOfShopOutput);
                totalPrice = totalPrice + price * productCartMapEntity.getQuantityOrder();
            }

            ProductCartMapOutput productCartMapOutput = ProductCartMapOutput.builder()
                    .shopId(shopId)
                    .nameShop(shopEntity.getFullName())
                    .productsOutput(productsOfShopOutputList)
                    .totalPrice(totalPrice)
                    .build();

            shoppingCartOutputs.add(productCartMapOutput);
        }
        return shoppingCartOutputs;
    }
}
