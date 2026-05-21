package com.lastcalleats.marketplace.product.service.impl;

import com.lastcalleats.common.exception.ErrorCode;
import com.lastcalleats.common.util.Assert;
import com.lastcalleats.identity.repository.MerchantRepo;
import com.lastcalleats.marketplace.product.dto.TemplateRequest;
import com.lastcalleats.marketplace.product.dto.TemplateResponse;
import com.lastcalleats.marketplace.product.entity.ProductTemplateDO;
import com.lastcalleats.marketplace.product.repository.ProductTemplateRepo;
import com.lastcalleats.marketplace.product.service.ProductTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductTemplateServiceImpl implements ProductTemplateService {

    private final ProductTemplateRepo templateRepo;
    private final MerchantRepo merchantRepo;

    @Override
    @Transactional
    public TemplateResponse create(Long merchantId, TemplateRequest request) {
        Assert.isTrue(merchantRepo.existsById(merchantId), ErrorCode.MERCHANT_NOT_FOUND);

        ProductTemplateDO template = ProductTemplateDO.builder()
                .merchantId(merchantId)
                .name(request.getName())
                .description(request.getDescription())
                .originalPrice(request.getOriginalPrice())
                .build();

        templateRepo.save(template);
        return toResponse(template);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TemplateResponse> getByMerchant(Long merchantId) {
        return templateRepo.findByMerchantIdAndIsActiveTrue(merchantId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TemplateResponse update(Long merchantId, Long templateId, TemplateRequest request) {
        ProductTemplateDO template = findTemplate(templateId);
        Assert.equals(template.getMerchantId(), merchantId, ErrorCode.FORBIDDEN);

        template.setName(request.getName());
        template.setDescription(request.getDescription());
        template.setOriginalPrice(request.getOriginalPrice());
        templateRepo.save(template);

        return toResponse(template);
    }

    @Override
    @Transactional
    public void delete(Long merchantId, Long templateId) {
        ProductTemplateDO template = findTemplate(templateId);
        Assert.equals(template.getMerchantId(), merchantId, ErrorCode.FORBIDDEN);

        template.setIsActive(false);
        templateRepo.save(template);
    }

    private ProductTemplateDO findTemplate(Long templateId) {
        return templateRepo.findById(templateId)
                .orElseThrow(() -> new com.lastcalleats.common.exception.BusinessException(ErrorCode.TEMPLATE_NOT_FOUND));
    }

    private TemplateResponse toResponse(ProductTemplateDO template) {
        return TemplateResponse.builder()
                .id(template.getId())
                .merchantId(template.getMerchantId())
                .name(template.getName())
                .description(template.getDescription())
                .originalPrice(template.getOriginalPrice())
                .isActive(template.getIsActive())
                .createdAt(template.getCreatedAt())
                .build();
    }
}
