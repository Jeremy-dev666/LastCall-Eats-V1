package com.lastcalleats.marketplace.order.factory;

import com.lastcalleats.common.util.PickupCodeUtil;
import com.lastcalleats.marketplace.order.entity.PickupCodeDO;
import org.springframework.stereotype.Component;

@Component
public class NumericCodeGenerator implements PickupCodeGenerator {

    @Override
    public String getType() {
        return "NUMERIC";
    }

    @Override
    public PickupCodeDO generate(Long orderId) {
        return PickupCodeDO.builder()
                .orderId(orderId)
                .numericCode(PickupCodeUtil.generateNumericCode())
                .qrCode("ORDER:" + orderId)
                .build();
    }
}
