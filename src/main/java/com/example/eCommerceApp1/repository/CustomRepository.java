package com.example.eCommerceApp1.repository;

import com.example.eCommerceApp1.common.Common;
import com.example.eCommerceApp1.enitty.*;
import com.example.eCommerceApp1.enitty.product.*;
import com.example.eCommerceApp1.repository.product.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class CustomRepository {
    private final UserRepository userRepository;
    private final ProductTemplateRepository productTemplateRepository;
    private final ProductRepository productRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final ProductAttributeValueMapRepository productAttributeValueMapRepository;
    private final VoucherRepository voucherRepository;
    private final ProductCartMapRepository productCartMapRepository;
    private final UserOrderRepository userOrderRepository;
    private final ChatRepository chatRepository;

    public UserEntity getUserBy(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }

    public ProductTemplateEntity getProductTemplateBy(Long productTemplateId) {
        return productTemplateRepository.findById(productTemplateId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }

    public ProductEntity getProductBy(Long productId) {
        return productRepository.findById(productId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }

    public AttributeEntity getAttributeBy(Long attributeId) {
        return attributeRepository.findById(attributeId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }

    public AttributeValueEntity getAttributeValueBy(Long attributeValueId) {
        return attributeValueRepository.findById(attributeValueId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }

    public ProductAttributeValueMapEntity getProductAttributeValueBy(Long productAttributeValueId) {
        return productAttributeValueMapRepository.findById(productAttributeValueId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }

    public VoucherEntity getVoucherBy(Long voucherId) {
        return voucherRepository.findById(voucherId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }

    public ProductCartMapEntity getProductCartMapBy(Long productCartMapId) {
        return productCartMapRepository.findById(productCartMapId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }

    public UserOrderEntity getUserOrderBy(Long orderId) {
        return userOrderRepository.findById(orderId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }

    public ChatEntity getChatEntityBy(Long chatId) {
        return chatRepository.findById(chatId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }
}
