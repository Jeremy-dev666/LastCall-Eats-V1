package com.lastcalleats.marketplace.order.state;

import com.lastcalleats.common.exception.BusinessException;
import com.lastcalleats.common.exception.ErrorCode;
import com.lastcalleats.marketplace.order.entity.OrderDO;

public interface OrderState {

    default String pay() {
        throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID);
    }

    default String complete() {
        throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID);
    }

    default String cancel() {
        throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID);
    }

    static OrderState fromStatus(OrderDO.OrderStatus status) {
        return switch (status) {
            case PENDING_PAYMENT -> new PendingPaymentState();
            case PAID -> new PaidState();
            case COMPLETED -> new CompletedState();
            case CANCELLED -> new CancelledState();
        };
    }
}
