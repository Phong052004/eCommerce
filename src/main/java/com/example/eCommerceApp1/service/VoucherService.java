package com.example.eCommerceApp1.service;

import com.example.eCommerceApp1.common.Common;
import com.example.eCommerceApp1.dto.voucher.VoucherInput;
import com.example.eCommerceApp1.dto.voucher.VoucherOutput;
import com.example.eCommerceApp1.enitty.UserEntity;
import com.example.eCommerceApp1.enitty.UserVoucherMapEntity;
import com.example.eCommerceApp1.enitty.VoucherEntity;
import com.example.eCommerceApp1.enitty.product.ProductTemplateEntity;
import com.example.eCommerceApp1.mapper.VoucherMapper;
import com.example.eCommerceApp1.repository.CustomRepository;
import com.example.eCommerceApp1.repository.UserRepository;
import com.example.eCommerceApp1.repository.UserVoucherMapRepository;
import com.example.eCommerceApp1.repository.VoucherRepository;
import com.example.eCommerceApp1.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@EnableScheduling
public class VoucherService {
    private final VoucherRepository voucherRepository;
    private final UserVoucherMapRepository userVoucherMapRepository;
    private final CustomRepository customRepository;
    private final VoucherMapper voucherMapper;
    private final UserRepository userRepository;
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final Random RANDOM = new SecureRandom();

    @Transactional
    public void createVoucherShop(String accessToken, VoucherInput voucherInput) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if (shopEntity.getIsShop().equals(Boolean.FALSE)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        VoucherEntity voucherEntity = voucherMapper.getEntityFromInput(voucherInput);
        voucherEntity.setCode(generateUniqueCode());
        voucherEntity.setShopId(shopId);
        voucherRepository.save(voucherEntity);
    }

    @Transactional
    public void createVoucherProduct(String accessToken, VoucherInput voucherInput, Long productTemplateId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if (shopEntity.getIsShop().equals(Boolean.FALSE)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        ProductTemplateEntity productTemplateEntity = customRepository.getProductTemplateBy(productTemplateId);
        if (!productTemplateEntity.getShopId().equals(shopId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        VoucherEntity voucherEntity = voucherMapper.getEntityFromInput(voucherInput);
        voucherEntity.setProductTemplateId(productTemplateId);
        voucherEntity.setCode(generateUniqueCode());
        voucherRepository.save(voucherEntity);
    }

    @Transactional(readOnly = true)
    public List<VoucherOutput> getVoucherShop(Long shopId) {
        List<VoucherEntity> voucherEntities = voucherRepository.findAllByShopId(shopId);
        List<VoucherOutput> voucherOutputs = new ArrayList<>();
        for (VoucherEntity voucherEntity : voucherEntities) {
            VoucherOutput voucherOutput = VoucherOutput.builder()
                    .shopId(voucherEntity.getShopId())
                    .name(voucherEntity.getName())
                    .code(voucherEntity.getCode())
                    .saleOff(voucherEntity.getSaleOff())
                    .startDate(voucherEntity.getStartDate())
                    .endDate(voucherEntity.getEndDate())
                    .build();
            voucherOutputs.add(voucherOutput);
        }
        return voucherOutputs;
    }

    @Transactional
    public void addVoucherShopByUser(String accessToken, Long voucherId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        VoucherEntity voucherEntity = customRepository.getVoucherBy(voucherId);
        if (voucherEntity.getShopId() == null) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        if (Boolean.TRUE.equals(userVoucherMapRepository.existsByVoucherId(voucherId))) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        UserVoucherMapEntity userVoucherMapEntity = UserVoucherMapEntity.builder()
                .voucherId(voucherId)
                .userId(userId)
                .build();
        userVoucherMapRepository.save(userVoucherMapEntity);
    }

    @Transactional
    public Page<VoucherOutput> getVoucherShopByUser(String accessToken , Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        List<UserVoucherMapEntity> userVoucherMapEntities = userVoucherMapRepository.findAllByUserId(userId);
        Page<VoucherEntity> voucherEntityPage = voucherRepository.findAllByIdIn(
                userVoucherMapEntities.stream().map(UserVoucherMapEntity::getVoucherId)
                        .collect(Collectors.toList()), pageable
        );

        if (Objects.isNull(voucherEntityPage) || voucherEntityPage.isEmpty()) {
            return Page.empty();
        }

        Map<Long, UserEntity> userEntityMap = userRepository.findAllByIdIn(
                voucherEntityPage.stream().map(VoucherEntity::getShopId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        return voucherEntityPage.map(
                voucherEntity -> {
                    UserEntity userEntity = userEntityMap.get(voucherEntity.getShopId());
                    VoucherOutput voucherOutput = VoucherOutput.builder()
                            .shopId(voucherEntity.getShopId())
                            .nameShop(userEntity.getFullName())
                            .imageShop(userEntity.getImage())
                            .name(voucherEntity.getName())
                            .code(voucherEntity.getCode())
                            .saleOff(voucherEntity.getSaleOff())
                            .startDate(voucherEntity.getStartDate())
                            .endDate(voucherEntity.getEndDate())
                            .build();
                    return voucherOutput;
                }
        );
    }

    @Transactional
    public void deleteVoucherShop(String accessToken, Long voucherId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        VoucherEntity voucherEntity = customRepository.getVoucherBy(voucherId);
        if (voucherEntity.getShopId().equals(shopId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        userVoucherMapRepository.deleteAllByVoucherId(voucherId);
        voucherRepository.deleteById(voucherId);
    }

    @Transactional
    public void deleteVoucherProduct(String accessToken, Long productTemplateId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        VoucherEntity voucherEntity = customRepository.getVoucherBy(productTemplateId);
        if (voucherEntity.getShopId().equals(shopId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        voucherRepository.deleteByProductTemplateId(productTemplateId);
    }

    @Transactional
    public VoucherOutput searchVoucherShop(String code) {
        VoucherEntity voucherEntity = voucherRepository.searchByCode(code);
        UserEntity userEntity = customRepository.getUserBy(voucherEntity.getShopId());
        return VoucherOutput.builder()
                .shopId(voucherEntity.getShopId())
                .nameShop(userEntity.getFullName())
                .imageShop(userEntity.getImage())
                .name(voucherEntity.getName())
                .code(voucherEntity.getCode())
                .saleOff(voucherEntity.getSaleOff())
                .startDate(voucherEntity.getStartDate())
                .endDate(voucherEntity.getEndDate())
                .build();
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void checkExpirationDateOfVoucher() {
        List<Long> voucherIds = voucherRepository.searchExpiredVouchers(LocalDateTime.now())
                .stream().map(VoucherEntity::getId).collect(Collectors.toList());
        voucherRepository.deleteAllByIdIn(voucherIds);
        userVoucherMapRepository.deleteAllByVoucherIdIn(voucherIds);
    }

    public String generateUniqueCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        long timestamp = Instant.now().toEpochMilli();
        return sb.toString() + timestamp;
    }
}
