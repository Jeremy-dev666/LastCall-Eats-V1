package com.lastcalleats.marketplace.order.factory;

import com.lastcalleats.marketplace.order.entity.PickupCodeDO;

public interface PickupCodeGenerator {

    String getType();

    PickupCodeDO generate(Long orderId);
}
