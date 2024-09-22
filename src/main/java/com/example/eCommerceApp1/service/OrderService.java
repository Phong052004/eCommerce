package com.example.eCommerceApp1.service;

import com.example.eCommerceApp1.common.Common;
import com.example.eCommerceApp1.dto.order.OrderInput;
import com.example.eCommerceApp1.dto.order.OrderOutput;
import com.example.eCommerceApp1.dto.order.ProductOrderInput;
import com.example.eCommerceApp1.dto.order.ProductsInput;
import com.example.eCommerceApp1.enitty.ProductOrderMapEntity;
import com.example.eCommerceApp1.enitty.UserOrderEntity;
import com.example.eCommerceApp1.enitty.product.ProductEntity;
import com.example.eCommerceApp1.enitty.product.ProductTemplateEntity;
import com.example.eCommerceApp1.mapper.ProductOrderMapper;
import com.example.eCommerceApp1.mapper.UserOrderMapper;
import com.example.eCommerceApp1.repository.CustomRepository;
import com.example.eCommerceApp1.repository.ProductCartMapRepository;
import com.example.eCommerceApp1.repository.ProductOrderMapRepository;
import com.example.eCommerceApp1.repository.UserOrderRepository;
import com.example.eCommerceApp1.repository.product.ProductRepository;
import com.example.eCommerceApp1.repository.product.ProductTemplateRepository;
import com.example.eCommerceApp1.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderService {
    private final UserOrderRepository userOrderRepository;
    private final ProductOrderMapRepository productOrderMapRepository;
    private final ProductRepository productRepository;
    private final ProductCartMapRepository productCartMapRepository;
    private final UserOrderMapper userOrderMapper;
    private final ProductOrderMapper productOrderMapper;
    private final CustomRepository customRepository;
    private final ProductTemplateRepository productTemplateRepository;

    @Transactional
    public void orderProducts(String accessToken, OrderInput orderInput) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        List<Long> productIds = orderInput.getOrderProducts().stream()
                .flatMap(order -> order.getProductOrderInputs().stream())
                .map(ProductOrderInput::getProductId)
                .collect(Collectors.toList());

        List<ProductEntity> productEntities = productRepository.findAllByIdIn(productIds);

        Set<Long> productTemplateIds = productEntities.stream()
                .map(ProductEntity::getProductTemplateId).collect(Collectors.toSet());

        Map<Long, ProductEntity> productEntityMap = productEntities.stream().collect(
                Collectors.toMap(ProductEntity::getId, productEntity -> productEntity)
        );

        Map<Long, ProductTemplateEntity> productTemplateEntityMap = productTemplateRepository
                .findAllByIdIn(productTemplateIds).stream().collect(
                        Collectors.toMap(ProductTemplateEntity::getId, productTemplateEntity -> productTemplateEntity)
                );

        for(ProductEntity productEntity : productEntities) {
            if(productEntity.getQuantity() <= 0) {
                throw new RuntimeException(Common.ACTION_FAIL);
            }
        }

        List<Long> shoppingCartIds = new ArrayList<>();
        for (ProductsInput productsInput : orderInput.getOrderProducts()) {
            UserOrderEntity userOrderEntity = UserOrderEntity.builder()
                    .userId(userId)
                    .name(orderInput.getName())
                    .address(orderInput.getAddress())
                    .phoneNumber(orderInput.getPhoneNumber())
                    .stateOrder(Common.WAIT_FOR_PAY)
                    .createAt(LocalDate.now())
                    .build();
            userOrderRepository.save(userOrderEntity);

            for (ProductOrderInput productOrderInput : productsInput.getProductOrderInputs()) {
                ProductEntity productEntity = productEntityMap.get(productOrderInput.getProductId());
                ProductTemplateEntity productTemplateEntity = productTemplateEntityMap.get(productOrderInput.getProductTemplateId());
                ProductOrderMapEntity productOrderMapEntity = ProductOrderMapEntity.builder()
                        .productId(productOrderInput.getProductId())
                        .orderId(userOrderEntity.getId())
                        .shopId(productsInput.getShopId())
                        .productTemplateId(productOrderInput.getProductTemplateId())
                        .quantityOrder(productOrderInput.getQuantityOrder())
                        .nameProduct(productOrderInput.getName())
                        .imageProduct(productOrderInput.getImage())
                        .price(productOrderInput.getPrice())
                        .build();

                productOrderMapRepository.save(productOrderMapEntity);
                productEntity.setQuantity(productEntity.getQuantity() - productOrderInput.getQuantityOrder());
                productRepository.save(productEntity);
                productTemplateEntity.setQuantity(productTemplateEntity.getQuantity() - productOrderInput.getQuantityOrder());
                productTemplateRepository.save(productTemplateEntity);

                shoppingCartIds.add(productOrderInput.getShoppingCartId());
            }
        }

        productCartMapRepository.deleteAllByIdIn(shoppingCartIds);
    }

    @Transactional(readOnly = true)
    public Page<OrderOutput> getAllProductOrder(String accessToken, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        Page<UserOrderEntity> userOrderEntities = userOrderRepository.findAllByUserId(userId, pageable);
        List<Long> orderIds = userOrderEntities
                .stream().map(UserOrderEntity::getId).collect(Collectors.toList());
        List<ProductOrderMapEntity> productOrderMapEntities = productOrderMapRepository
                .findAllByOrderIdIn(orderIds);

        Map<Long, List<ProductOrderMapEntity>> productOrderMap = productOrderMapEntities.stream()
                .collect(Collectors.groupingBy(ProductOrderMapEntity::getOrderId));

        return userOrderEntities.map(
                shoppingOrderEntity -> {
                    OrderOutput orderOutput = userOrderMapper.getOutputFromEntity(shoppingOrderEntity);
                    List<ProductOrderMapEntity> productOrderMapEntityList = productOrderMap.get(shoppingOrderEntity.getId());

                    List<ProductOrderInput> productOrderOutputs = new ArrayList<>();
                    for(ProductOrderMapEntity productOrderMapEntity : productOrderMapEntityList) {
                        ProductOrderInput productOrderOutput = productOrderMapper.getOutputFromEntity(productOrderMapEntity);
                        productOrderOutputs.add(productOrderOutput);
                    }

                    orderOutput.setProductOrderOutputs(productOrderOutputs);
                    return orderOutput;
                }
        );
    }

    @Transactional(readOnly = true)
    public Page<OrderOutput> getProductsByState(String accessToken, Pageable pageable, String state) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        Page<UserOrderEntity> userOrderEntities = userOrderRepository.findAllByUserIdAndStateOrder(userId, state, pageable);
        List<Long> orderIds = userOrderEntities
                .stream().map(UserOrderEntity::getId).collect(Collectors.toList());
        List<ProductOrderMapEntity> productOrderMapEntities = productOrderMapRepository
                .findAllByOrderIdIn(orderIds);

        Map<Long, List<ProductOrderMapEntity>> productOrderMap = productOrderMapEntities.stream()
                .collect(Collectors.groupingBy(ProductOrderMapEntity::getOrderId));

        return userOrderEntities.map(
                shoppingOrderEntity -> {
                    OrderOutput orderOutput = userOrderMapper.getOutputFromEntity(shoppingOrderEntity);
                    List<ProductOrderMapEntity> productOrderMapEntityList = productOrderMap.get(shoppingOrderEntity.getId());

                    List<ProductOrderInput> productOrderOutputs = new ArrayList<>();
                    for(ProductOrderMapEntity productOrderMapEntity : productOrderMapEntityList) {
                        ProductOrderInput productOrderOutput = productOrderMapper.getOutputFromEntity(productOrderMapEntity);
                        productOrderOutputs.add(productOrderOutput);
                    }

                    orderOutput.setProductOrderOutputs(productOrderOutputs);
                    return orderOutput;
                }
        );
    }

    @Transactional
    public void cancelOrder(String accessToken, Long orderId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserOrderEntity userOrderEntity = customRepository.getUserOrderBy(orderId);
        if(!userOrderEntity.getUserId().equals(userId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        if(!userOrderEntity.getStateOrder().equals(Common.WAIT_FOR_PAY)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        userOrderEntity.setStateOrder(Common.CANCELLED);
        userOrderRepository.save(userOrderEntity);

        List<ProductOrderMapEntity> productOrderMapEntities = productOrderMapRepository.findAllByOrderId(orderId);

        Map<Long, ProductOrderMapEntity> productOrderMapEntityMap = productOrderMapEntities
                .stream().collect(Collectors.toMap(ProductOrderMapEntity::getProductId, Function.identity()));

        List<Long> productIds = productOrderMapEntities
                .stream().map(ProductOrderMapEntity::getProductId).collect(Collectors.toList());

        List<ProductEntity> productEntities = productRepository.findAllByIdIn(productIds);

        Set<Long> productTemplateIds = productEntities.stream().map(ProductEntity::getProductTemplateId).collect(Collectors.toSet());

        Map<Long, ProductTemplateEntity> productTemplateEntityMap = productTemplateRepository.findAllByIdIn(productTemplateIds)
                .stream().collect(Collectors.toMap(ProductTemplateEntity::getId, Function.identity()));

        for(ProductEntity productEntity : productEntities) {
            ProductOrderMapEntity productOrderMapEntity = productOrderMapEntityMap.get(productEntity.getId());
            ProductTemplateEntity productTemplateEntity = productTemplateEntityMap.get(productEntity.getProductTemplateId());

            productEntity.setQuantity(productEntity.getQuantity() + productOrderMapEntity.getQuantityOrder());
            productTemplateEntity.setQuantity(productTemplateEntity.getQuantity() + productOrderMapEntity.getQuantityOrder());

            productRepository.save(productEntity);
            productTemplateRepository.save(productTemplateEntity);
        }
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void checkExpirationDateOfOrder() {
        List<Long> userOrderIds = userOrderRepository.findAllFailedOrder(Common.WAIT_FOR_PAY)
                .stream().map(UserOrderEntity::getId).collect(Collectors.toList());
        userOrderRepository.deleteAllByIdIn(userOrderIds);
        productOrderMapRepository.findAllByOrderIdIn(userOrderIds);
    }
}
