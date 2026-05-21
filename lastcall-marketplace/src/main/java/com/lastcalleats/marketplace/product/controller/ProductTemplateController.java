package com.lastcalleats.marketplace.product.controller;

import com.lastcalleats.common.response.ApiResponse;
import com.lastcalleats.marketplace.product.dto.TemplateRequest;
import com.lastcalleats.marketplace.product.dto.TemplateResponse;
import com.lastcalleats.marketplace.product.service.ProductTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant/templates")
@RequiredArgsConstructor
public class ProductTemplateController {

    private final ProductTemplateService templateService;

    @PostMapping
    public ApiResponse<TemplateResponse> create(
            @AuthenticationPrincipal Long merchantId,
            @Valid @RequestBody TemplateRequest request) {
        return ApiResponse.success(templateService.create(merchantId, request));
    }

    @GetMapping
    public ApiResponse<List<TemplateResponse>> list(@AuthenticationPrincipal Long merchantId) {
        return ApiResponse.success(templateService.getByMerchant(merchantId));
    }

    @PutMapping("/{templateId}")
    public ApiResponse<TemplateResponse> update(
            @AuthenticationPrincipal Long merchantId,
            @PathVariable Long templateId,
            @Valid @RequestBody TemplateRequest request) {
        return ApiResponse.success(templateService.update(merchantId, templateId, request));
    }

    @DeleteMapping("/{templateId}")
    public ApiResponse<Void> delete(
            @AuthenticationPrincipal Long merchantId,
            @PathVariable Long templateId) {
        templateService.delete(merchantId, templateId);
        return ApiResponse.success();
    }
}
