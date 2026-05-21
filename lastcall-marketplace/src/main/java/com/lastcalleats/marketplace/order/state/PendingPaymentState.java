package com.lastcalleats.marketplace.order.state;

import com.lastcalleats.marketplace.order.entity.OrderDO;

public class PendingPaymentState implements OrderState {

    @Override
    public String pay() {
        return OrderDO.OrderStatus.PAID.name();
    }

    @Override
    public String cancel() {
        return OrderDO.OrderStatus.CANCELLED.name();
    }
}
