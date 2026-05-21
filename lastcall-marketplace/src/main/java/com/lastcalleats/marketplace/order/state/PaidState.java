package com.lastcalleats.marketplace.order.state;

import com.lastcalleats.marketplace.order.entity.OrderDO;

public class PaidState implements OrderState {

    @Override
    public String complete() {
        return OrderDO.OrderStatus.COMPLETED.name();
    }

    @Override
    public String cancel() {
        return OrderDO.OrderStatus.CANCELLED.name();
    }
}
