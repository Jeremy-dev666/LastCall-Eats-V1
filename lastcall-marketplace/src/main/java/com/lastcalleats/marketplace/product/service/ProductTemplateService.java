package com.lastcalleats.marketplace.product.service;

import com.lastcalleats.marketplace.product.dto.TemplateRequest;
import com.lastcalleats.marketplace.product.dto.TemplateResponse;

import java.util.List;

public interface ProductTemplateService {

    TemplateResponse create(Long merchantId, TemplateRequest request);

    List<TemplateResponse> getByMerchant(Long merchantId);

    TemplateResponse update(Long merchantId, Long templateId, TemplateRequest request);

    void delete(Long merchantId, Long templateId);
}
