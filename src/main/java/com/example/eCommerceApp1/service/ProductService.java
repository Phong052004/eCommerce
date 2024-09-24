package com.example.eCommerceApp1.service;

import com.example.eCommerceApp1.common.Common;
import com.example.eCommerceApp1.dto.product.*;
import com.example.eCommerceApp1.enitty.CommentEntity;
import com.example.eCommerceApp1.enitty.ProductOrderMapEntity;
import com.example.eCommerceApp1.enitty.UserEntity;
import com.example.eCommerceApp1.enitty.VoucherEntity;
import com.example.eCommerceApp1.enitty.product.*;
import com.example.eCommerceApp1.helper.StringUtils;
import com.example.eCommerceApp1.mapper.product.ProductMapper;
import com.example.eCommerceApp1.mapper.product.ProductTemplateMapper;
import com.example.eCommerceApp1.repository.*;
import com.example.eCommerceApp1.repository.product.*;
import com.example.eCommerceApp1.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductTemplateRepository productTemplateRepository;
    private final ProductRepository productRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final ProductAttributeValueMapRepository productAttributeValueMapRepository;
    private final CustomRepository customRepository;
    private final ProductTemplateMapper productTemplateMapper;
    private final ProductMapper productMapper;
    private final UserRepository userRepository;
    private final TemplateAttributeMapRepository templateAttributeMapRepository;
    private final VoucherRepository voucherRepository;
    private final CommentRepository commentRepository;
    private final ProductOrderMapRepository productOrderMapRepository;

    @Transactional
    public void createProductTemplate(String accessToken,
                                      ProductTemplateInput productTemplateInput) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        ProductTemplateEntity productTemplateEntity = productTemplateMapper
                .getEntityFromInput(productTemplateInput);
        productTemplateEntity.setShopId(shopId);
        productTemplateEntity.setSold(0);
        productTemplateEntity.setAverageRating(0.0);
        productTemplateEntity.setCommentCount(0);
        productTemplateEntity.setLikeCount(0);
        productTemplateRepository.save(productTemplateEntity);

        shopEntity.setTotalProduct(shopEntity.getTotalProduct() + 1);
        userRepository.save(shopEntity);
    }

    @Transactional
    public void createAttributeAndAttributeValue(String accessToken, Long productTemplateId, List<AttributeInput> attributeInputs) {
        authenticateShop(accessToken, productTemplateId);
        for (AttributeInput attributeInput : attributeInputs) {
            if (attributeInput.getAttributeId() == null) {
                AttributeEntity attributeEntity = AttributeEntity.builder()
                        .name(attributeInput.getName())
                        .isOfShop(Boolean.TRUE)
                        .build();

                attributeRepository.save(attributeEntity);
                for (AttributeValueInput attributeValueInput : attributeInput.getAttributeValues()) {
                    AttributeValueEntity attributeValueEntity = AttributeValueEntity.builder()
                            .name(attributeValueInput.getName())
                            .attributeId(attributeEntity.getId())
                            .isOfShop(Boolean.TRUE)
                            .build();

                    attributeValueRepository.save(attributeValueEntity);

                    TemplateAttributeMapEntity templateAttributeMapEntity = TemplateAttributeMapEntity.builder()
                            .productTemplateId(productTemplateId)
                            .attributeId(attributeEntity.getId())
                            .attributeValueId(attributeValueEntity.getId())
                            .build();

                    templateAttributeMapRepository.save(templateAttributeMapEntity);
                }
            } else if(attributeInput.getIsOfShop().equals(Boolean.FALSE) && attributeInput.getExisted().equals(Boolean.TRUE)) {
                for (AttributeValueInput attributeValueInput : attributeInput.getAttributeValues()) {
                    TemplateAttributeMapEntity templateAttributeMapEntity = TemplateAttributeMapEntity.builder()
                            .productTemplateId(productTemplateId)
                            .attributeId(attributeInput.getAttributeId())
                            .attributeValueId(attributeValueInput.getAttributeValueId())
                            .build();

                    templateAttributeMapRepository.save(templateAttributeMapEntity);
                }
            }
        }
    }

    @Transactional
    public void createProducts(String accessToken, List<ProductInput> productInputs, Long productTemplateId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        ProductTemplateEntity productTemplateEntity = customRepository.getProductTemplateBy(productTemplateId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        if(!shopId.equals(productTemplateEntity.getShopId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        addProducts(productInputs, productTemplateEntity);
    }

    @Transactional
    public void updateProductTemplate(String accessToken,Long productTemplateId, ProductTemplateInput productTemplateInput) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        ProductTemplateEntity productTemplateEntity = customRepository.getProductTemplateBy(productTemplateId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        if(!shopId.equals(productTemplateEntity.getShopId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        productTemplateMapper.updateEntityFromInput(productTemplateEntity, productTemplateInput);
        productTemplateRepository.save(productTemplateEntity);
    }

    @Transactional
    public void addAttributeValue(String accessToken,
                                  Long productTemplateId,
                                  Long attributeId,
                                  List<AttributeValueInput> attributeValueInputs) {
        authenticateShop(accessToken, productTemplateId);
        AttributeEntity attributeEntity = customRepository.getAttributeBy(attributeId);
        if (attributeEntity.getIsOfShop().equals(Boolean.TRUE)) {
            for (AttributeValueInput attributeValueInput : attributeValueInputs) {
                TemplateAttributeMapEntity templateAttributeMapEntity = TemplateAttributeMapEntity.builder()
                        .attributeId(attributeId)
                        .attributeValueId(attributeValueInput.getAttributeValueId())
                        .productTemplateId(productTemplateId)
                        .build();

                templateAttributeMapRepository.save(templateAttributeMapEntity);
            }
        } else {
            for (AttributeValueInput attributeValueInput : attributeValueInputs) {
                AttributeValueEntity attributeValueEntity = AttributeValueEntity.builder()
                        .name(attributeValueInput.getName())
                        .isOfShop(Boolean.TRUE)
                        .build();
                attributeValueRepository.save(attributeValueEntity);

                TemplateAttributeMapEntity templateAttributeMapEntity = TemplateAttributeMapEntity.builder()
                        .attributeId(attributeId)
                        .attributeValueId(attributeValueEntity.getId())
                        .productTemplateId(productTemplateId)
                        .build();

                templateAttributeMapRepository.save(templateAttributeMapEntity);
            }
        }
    }

    @Transactional
    public void changeProductsAfterChangingAttribute(String accessToken, List<ProductInput> productInputs, Long productTemplateId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        ProductTemplateEntity productTemplateEntity = customRepository.getProductTemplateBy(productTemplateId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        if(!shopId.equals(productTemplateEntity.getShopId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        List<ProductAttributeValueMapEntity> productAttributeValueMapEntities =
                productAttributeValueMapRepository.findAllByProductTemplateId(productTemplateId);
        Set<Long> productIds = productAttributeValueMapEntities.stream()
                .map(ProductAttributeValueMapEntity::getProductId).collect(Collectors.toSet());
        productAttributeValueMapRepository.deleteAllByProductTemplateId(productTemplateId);
        productRepository.deleteAllByIdIn(productIds);
        addProducts(productInputs, productTemplateEntity);
    }

    @Transactional
    public void updateProduct(String accessToken, List<ProductInput> productInputs, Long productTemplateId) {
        authenticateShop(accessToken, productTemplateId);
        Set<Long> productIds = productAttributeValueMapRepository.findAllByProductTemplateId(productTemplateId)
                .stream().map(ProductAttributeValueMapEntity::getProductId).collect(Collectors.toSet());

        Map<Long, ProductEntity> productEntityMap = productRepository.findAllByIdIn(productIds)
                .stream().collect(Collectors.toMap(ProductEntity::getId, Function.identity()));

        for (ProductInput productInput : productInputs) {
            ProductEntity productEntity = productEntityMap.get(productInput.getProductId());
            productMapper.updateEntityFormInput(productEntity, productInput);
            productRepository.save(productEntity);
        }
    }

    @Transactional(readOnly = true)
    public List<AttributeOutput> getAttributeApp(String accessToken) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<AttributeEntity> attributeEntities = attributeRepository.findAllByIsOfShop(Boolean.FALSE);
        List<AttributeOutput> attributeOutputs = new ArrayList<>();

        for(AttributeEntity attributeEntity : attributeEntities) {
            AttributeOutput attributeOutput = AttributeOutput.builder()
                    .attributeId(attributeEntity.getId())
                    .name(attributeEntity.getName())
                    .existed(Boolean.TRUE)
                    .isOfShop(attributeEntity.getIsOfShop())
                    .build();
            attributeOutputs.add(attributeOutput);
        }

        return attributeOutputs;
    }

    @Transactional(readOnly = true)
    public List<AttributeOutput> getAttributeProduct(String accessToken, Long productTemplateId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<ProductAttributeValueMapEntity> productAttributeValueMapEntities = productAttributeValueMapRepository
                .findAllByProductTemplateId(productTemplateId);

        Set<Long> attributeIds = productAttributeValueMapEntities.stream()
                .map(ProductAttributeValueMapEntity::getAttributeId).collect(Collectors.toSet());

        List<AttributeEntity> attributeEntities = attributeRepository.findAllByIdIn(attributeIds);
        List<AttributeOutput> attributeOutputs = new ArrayList<>();

        for(AttributeEntity attributeEntity : attributeEntities) {
            AttributeOutput attributeOutput = AttributeOutput.builder()
                    .attributeId(attributeEntity.getId())
                    .name(attributeEntity.getName())
                    .isOfShop(attributeEntity.getIsOfShop())
                    .existed(Boolean.TRUE)
                    .build();
            attributeOutputs.add(attributeOutput);
        }
        return attributeOutputs;
    }

    @Transactional(readOnly = true)
    public List<AttributeValueOutput> getAttributeValueOfApp(String accessToken, Long attributeId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<AttributeValueEntity> attributeValueEntities = attributeValueRepository
                .findAllByAttributeIdAndIsOfShop(attributeId, Boolean.FALSE);
        List<AttributeValueOutput> attributeValueOutputs = new ArrayList<>();
        for(AttributeValueEntity attributeValueEntity : attributeValueEntities) {
            AttributeValueOutput attributeValueOutput = AttributeValueOutput.builder()
                    .attributeValueId(attributeValueEntity.getId())
                    .name(attributeValueEntity.getName())
                    .isOfShop(attributeValueEntity.getIsOfShop())
                    .existed(Boolean.TRUE)
                    .build();

            attributeValueOutputs.add(attributeValueOutput);
        }
        return attributeValueOutputs;
    }

    @Transactional(readOnly = true)
    public List<AttributeValueOutput> getAttributeValueOfProduct(String accessToken, Long productTemplateId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<ProductAttributeValueMapEntity> productAttributeValueMapEntities = productAttributeValueMapRepository
                .findAllByProductTemplateId(productTemplateId);

        Set<Long> attributeValueIds = productAttributeValueMapEntities.stream()
                .map(ProductAttributeValueMapEntity::getAttributeValueId).collect(Collectors.toSet());

        List<AttributeValueEntity> attributeValueEntities = attributeValueRepository.findAllByIdIn(attributeValueIds);

        List<AttributeValueOutput> attributeValueOutputs = new ArrayList<>();

        for(AttributeValueEntity attributeValueEntity : attributeValueEntities) {
            AttributeValueOutput attributeValueOutput = AttributeValueOutput.builder()
                    .attributeValueId(attributeValueEntity.getId())
                    .name(attributeValueEntity.getName())
                    .isOfShop(attributeValueEntity.getIsOfShop())
                    .existed(Boolean.TRUE)
                    .build();

            attributeValueOutputs.add(attributeValueOutput);
        }
        return attributeValueOutputs;
    }

    @Transactional(readOnly = true)
    public List<AttributeOutput> getAttributeAndAttributeValueOfProduct(String accessToken, Long productTemplateId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<TemplateAttributeMapEntity> templateAttributes = templateAttributeMapRepository
                .findAllByProductTemplateId(productTemplateId);

        Set<Long> attributeIds = templateAttributes.stream()
                .map(TemplateAttributeMapEntity::getAttributeId).collect(Collectors.toSet());

        Set<Long> attributeValueIds = templateAttributes.stream()
                .map(TemplateAttributeMapEntity::getAttributeValueId).collect(Collectors.toSet());

        Map<Long, List<Long>> attributeMap = templateAttributes.stream().collect(
                Collectors.groupingBy(
                        TemplateAttributeMapEntity::getAttributeId,
                        Collectors.mapping(TemplateAttributeMapEntity::getAttributeValueId, Collectors.toList())
                )
        );

        List<AttributeEntity> attributeEntities = attributeRepository.findAllByIdIn(attributeIds);
        Map<Long, AttributeValueEntity> attributeValueEntityMap = attributeValueRepository
                .findAllByIdIn(attributeValueIds).stream().collect(
                        Collectors.toMap(AttributeValueEntity::getId, Function.identity())
                );

        List<AttributeOutput> attributeOutputs = new ArrayList<>();
        for(AttributeEntity attributeEntity : attributeEntities) {
            List<Long> attributeValueIdList = attributeMap.get(attributeEntity.getId());
            List<AttributeValueOutput> attributeValueOutputs = new ArrayList<>();
            for (Long attributeValueId : attributeValueIdList) {
                AttributeValueEntity attributeValueEntity = attributeValueEntityMap.get(attributeValueId);
                AttributeValueOutput attributeValueOutput = AttributeValueOutput.builder()
                        .attributeValueId(attributeValueId)
                        .name(attributeValueEntity.getName())
                        .isOfShop(attributeValueEntity.getIsOfShop())
                        .existed(Boolean.TRUE)
                        .build();

                attributeValueOutputs.add(attributeValueOutput);
            }
            AttributeOutput attributeOutput = AttributeOutput.builder()
                    .name(attributeEntity.getName())
                    .attributeId(attributeEntity.getId())
                    .isOfShop(attributeEntity.getIsOfShop())
                    .existed(Boolean.TRUE)
                    .attributeValueOutputs(attributeValueOutputs)
                    .build();

            attributeOutputs.add(attributeOutput);
        }
        return attributeOutputs;
    }

    public void addProducts(List<ProductInput> productInputs, ProductTemplateEntity productTemplateEntity) {
        Set<Long> attributeValueIds = productInputs.stream()
                .flatMap(productInput -> productInput.getAttributeValueIds().stream())
                .collect(Collectors.toSet());

        Map<Long, Long> attributeValueIdMap = attributeValueRepository.findAllByIdIn(attributeValueIds)
                .stream().collect(Collectors.toMap(AttributeValueEntity::getId, AttributeValueEntity::getAttributeId));

        List<String> imageUrls = new ArrayList<>();
        for (ProductInput productInput : productInputs) {
            if (productInput.getPrice() < productTemplateEntity.getMinPrice() ||
                    productInput.getPrice() > productTemplateEntity.getMaxPrice()) {
                throw new RuntimeException(Common.ACTION_FAIL);
            }
            imageUrls.add(productInput.getImageUrl());

            ProductEntity productEntity = productMapper.getEntityFromInput(productInput);
            productEntity.setProductTemplateId(productTemplateEntity.getId());
            productRepository.save(productEntity);

            if (!productInput.getAttributeValueIds().isEmpty()) {
                for (Long attributeValueId : productInput.getAttributeValueIds()) {
                    ProductAttributeValueMapEntity productAttributeValueMapEntity = ProductAttributeValueMapEntity.builder()
                            .attributeValueId(attributeValueId)
                            .attributeId(attributeValueIdMap.get(attributeValueId))
                            .productId(productEntity.getId())
                            .productTemplateId(productTemplateEntity.getId())
                            .build();

                    productAttributeValueMapRepository.save(productAttributeValueMapEntity);
                }
            }
        }

        int totalQuantity = productInputs.stream().mapToInt(ProductInput::getQuantity).sum();
        productTemplateEntity.setImages(StringUtils.getStringFromList(imageUrls));
        productTemplateEntity.setAvatarImage(imageUrls.get(0));
        productTemplateEntity.setQuantity(totalQuantity);
        productTemplateRepository.save(productTemplateEntity);
    }

    @Transactional(readOnly = true)
    public Page<ProductsTemplateOutput> getProductsTemplate(Long shopId, Pageable pageable) {
        Page<ProductTemplateEntity> productTemplateEntities = productTemplateRepository.findAllByShopId(shopId,pageable);
        return getProductsTemplate(productTemplateEntities);
    }

    @Transactional(readOnly = true)
    public List<ProductOutput> getProducts(Long productTemplateId) {
        List<ProductEntity> productEntities = productRepository.findAllByProductTemplateId(productTemplateId);
        VoucherEntity voucherEntity = voucherRepository.findByProductTemplateId(productTemplateId);
        Map<Long, List<ProductOrderMapEntity>> productOrderMap = productOrderMapRepository
                .findAllByProductIdIn(
                        productEntities.stream().map(ProductEntity::getId).collect(Collectors.toList())
                ).stream().collect(Collectors.groupingBy(ProductOrderMapEntity::getProductId));
        List<ProductOutput> productOutPuts = new ArrayList<>();
        for(ProductEntity productEntity : productEntities) {
            int soldCount = productOrderMap.get(productEntity.getId())
                    .stream().mapToInt(ProductOrderMapEntity::getQuantityOrder).sum();
            ProductOutput productOutPut = ProductOutput.builder()
                    .productId(productEntity.getId())
                    .name(productEntity.getName())
                    .price(productEntity.getPrice())
                    .quantity(productEntity.getQuantity() - soldCount)
                    .imageUrl(productEntity.getImageUrl())
                    .discountedPrice(
                            (voucherEntity != null) ?
                                    (int)Math.ceil(productEntity.getPrice() * (1 - voucherEntity.getSaleOff())) : null)
                    .saleOff((voucherEntity != null) ? voucherEntity.getSaleOff() : null)
                    .existed(Boolean.TRUE)
                    .build();
            productOutPuts.add(productOutPut);
        }

        return productOutPuts;
    }

    @Transactional(readOnly = true)
    public ProductOutput getProductByAttributeValues(List<Long> attributeValueIds, Long productTemplateId) {
        List<ProductAttributeValueMapEntity> productAttributeValueMapEntities = productAttributeValueMapRepository
                .findAllByAttributeValueIdInAndProductTemplateId(attributeValueIds, productTemplateId);
        if(productAttributeValueMapEntities.isEmpty()) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        Map<Long, List<Long>> productEntityMap = productAttributeValueMapEntities.stream()
                .collect(Collectors.groupingBy(
                                ProductAttributeValueMapEntity::getProductId,
                                Collectors.mapping(ProductAttributeValueMapEntity::getAttributeValueId, Collectors.toList())
                        )
                );

        long productId = 0;
        for(ProductAttributeValueMapEntity productAttributeValueMapEntity : productAttributeValueMapEntities) {
            if(productEntityMap.get(productAttributeValueMapEntity.getProductId()).size() == attributeValueIds.size()) {
                productId = productAttributeValueMapEntity.getProductId();
                break;
            }
        }

        if(productId == 0) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        VoucherEntity voucherEntity = voucherRepository.findByProductTemplateId(productTemplateId);

        ProductEntity productEntity = customRepository.getProductBy(productId);
        return ProductOutput.builder()
                .productId(productEntity.getId())
                .name(productEntity.getName())
                .price(productEntity.getPrice())
                .quantity(productEntity.getQuantity())
                .imageUrl(productEntity.getImageUrl())
                .discountedPrice(
                        (voucherEntity != null) ?
                                (int)Math.ceil(productEntity.getPrice() * (1 - voucherEntity.getSaleOff())) : null)
                .saleOff((voucherEntity != null) ? voucherEntity.getSaleOff() : null)
                .existed(Boolean.TRUE)
                .build();
    }

    @Transactional
    public void deleteProductTemplate(String accessToken, Long productTemplateId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<ProductAttributeValueMapEntity> productAttributeValueMapEntities =
                productAttributeValueMapRepository.findAllByProductTemplateId(productTemplateId);

        Set<Long> attributeIds = productAttributeValueMapEntities.stream()
                .map(ProductAttributeValueMapEntity::getAttributeId).collect(Collectors.toSet());

        attributeRepository.deleteAllByIdInAndIsOfShop(attributeIds, Boolean.TRUE);

        Set<Long> attributeValueIds = productAttributeValueMapEntities
                .stream().map(ProductAttributeValueMapEntity::getAttributeValueId).collect(Collectors.toSet());

        attributeValueRepository.deleteAllByIdInAndIsOfShop(attributeValueIds, Boolean.TRUE);

        productTemplateRepository.deleteById(productTemplateId);
        productRepository.deleteAllByProductTemplateId(productTemplateId);
        productAttributeValueMapRepository.deleteAllByProductTemplateId(productTemplateId);
        templateAttributeMapRepository.deleteAllByProductTemplateId(productTemplateId);
        commentRepository.deleteAllByProductTemplateId(productTemplateId);
    }

    @Transactional
    public void deleteAttributeOfShop(String accessToken, Long productTemplateId, List<Long> attributeIds) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        ProductTemplateEntity productTemplateEntity = customRepository.getProductTemplateBy(productTemplateId);
        if(!shopId.equals(productTemplateEntity.getShopId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<TemplateAttributeMapEntity> templateAttributeMapEntities = templateAttributeMapRepository
                .findAllByProductTemplateId(productTemplateId);

        Set<Long> attributeIdSet = templateAttributeMapEntities.stream()
                .map(TemplateAttributeMapEntity::getAttributeId).collect(Collectors.toSet());

        for(Long attributeId : attributeIds) {
            if (!attributeIdSet.contains(attributeId)) {
                throw new RuntimeException(Common.ACTION_FAIL);
            }
        }

        List<AttributeEntity> attributeEntities = attributeRepository.findAllByIdIn(attributeIds);
        for (AttributeEntity attributeEntity : attributeEntities) {
            if (attributeEntity.getIsOfShop().equals(Boolean.TRUE)) {
                attributeRepository.deleteById(attributeEntity.getId());
                attributeValueRepository.deleteAllByAttributeIdAndIsOfShop(attributeEntity.getId(), Boolean.TRUE);
            }
        }

        templateAttributeMapRepository.deleteAllByAttributeIdIn(attributeIds);
    }

    @Transactional
    public void deleteAttributeValueOfShop(String accessToken,
                                              Long productTemplateId,
                                              Long attributeId,
                                              List<Long> attributeValueIds) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        ProductTemplateEntity productTemplateEntity = customRepository.getProductTemplateBy(productTemplateId);
        if(!shopId.equals(productTemplateEntity.getShopId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<TemplateAttributeMapEntity> templateAttributeMapEntities = templateAttributeMapRepository
                .findAllByProductTemplateId(productTemplateId);

        Set<Long> attributeValueIdSet = templateAttributeMapEntities.stream()
                .map(TemplateAttributeMapEntity::getAttributeValueId).collect(Collectors.toSet());

        for (Long attributeValueId : attributeValueIds) {
            if (!attributeValueIdSet.contains(attributeValueId)) {
                throw new RuntimeException(Common.ACTION_FAIL);
            }
        }

        AttributeEntity attributeEntity = customRepository.getAttributeBy(attributeId);
        if (attributeEntity.getIsOfShop().equals(Boolean.TRUE)) {
            attributeValueRepository.deleteAllByIdIn(attributeValueIds);
        }
        templateAttributeMapRepository.deleteAllByAttributeValueIdIn(attributeValueIds);
    }

    @Transactional
    public Page<ProductsTemplateOutput> searchProductsTemplateBy(String search, Pageable pageable) {
        Page<ProductTemplateEntity> productTemplateEntities = productTemplateRepository
                .searchProductTemplateEntitiesByString(search, pageable);

        if (Objects.isNull(productTemplateEntities) || productTemplateEntities.isEmpty()) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        return getProductsTemplate(productTemplateEntities);
    }

    public Page<ProductsTemplateOutput> getProductsTemplate(Page<ProductTemplateEntity> productTemplateEntities) {
        if(Objects.isNull(productTemplateEntities) || productTemplateEntities.isEmpty()) {
            return Page.empty();
        }

        List<Long> productTemplateIds = productTemplateEntities.stream().map(ProductTemplateEntity::getId).collect(Collectors.toList());

        Map<Long, VoucherEntity> voucherEntityMap = voucherRepository.findAllByProductTemplateIdIn(productTemplateIds)
                .stream().collect(Collectors.toMap(VoucherEntity::getProductTemplateId, Function.identity()));

        Map<Long, List<CommentEntity>> commentEntityMap = commentRepository.findAllByProductTemplateIdIn(productTemplateIds)
                .stream().collect(Collectors.groupingBy(CommentEntity::getProductTemplateId));

        Map<Long, List<ProductOrderMapEntity>> productOrderMap = productOrderMapRepository
                .findAllByProductTemplateIdIn(productTemplateIds)
                .stream().collect(Collectors.groupingBy(ProductOrderMapEntity::getProductTemplateId));


        return productTemplateEntities.map(
                productTemplateEntity -> {
                    VoucherEntity voucherEntity = voucherEntityMap.get(productTemplateEntity.getId());
                    List<CommentEntity> commentEntities = commentEntityMap.get(productTemplateEntity.getId());
                    List<ProductOrderMapEntity> productOrderMapEntities = productOrderMap.get(productTemplateEntity.getId());
                    ProductsTemplateOutput productsTemplateOutput = ProductsTemplateOutput.builder()
                            .productTemplateId(productTemplateEntity.getId())
                            .name(productTemplateEntity.getName())
                            .minPrice(productTemplateEntity.getMinPrice())
                            .maxPrice(productTemplateEntity.getMaxPrice())
                            .description(productTemplateEntity.getDescription())
                            .avatarImage(productTemplateEntity.getAvatarImage())
                            .build();

                    if(Objects.nonNull(voucherEntity)) {
                        productsTemplateOutput.setDiscountedPrice(
                                (int) Math.ceil(
                                        (double)(productTemplateEntity.getMinPrice() + productTemplateEntity.getMaxPrice()) /2
                                        * (1 - voucherEntity.getSaleOff())
                                )
                        );
                        productsTemplateOutput.setSaleOff(voucherEntity.getSaleOff());
                    }

                    int rating = commentEntities.stream().mapToInt(CommentEntity::getRating).sum();
                    productsTemplateOutput.setAverageRate(
                            (double) Math.round(rating/commentEntities.size() * 10) / 10
                    );

                    int soldCount = productOrderMapEntities
                            .stream().mapToInt(ProductOrderMapEntity::getQuantityOrder).sum();
                    productsTemplateOutput.setSoldCount(soldCount);
                    productsTemplateOutput.setQuantity(productTemplateEntity.getQuantity() - soldCount);
                    return productsTemplateOutput;
                }
        );
    }

    public void authenticateShop(String accessToken, Long productTemplateId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        ProductTemplateEntity productTemplateEntity = customRepository.getProductTemplateBy(productTemplateId);
        if(!shopId.equals(productTemplateEntity.getShopId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
    }
}
