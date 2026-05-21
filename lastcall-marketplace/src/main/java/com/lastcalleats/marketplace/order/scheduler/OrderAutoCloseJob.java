package com.lastcalleats.marketplace.order.scheduler;

import com.lastcalleats.marketplace.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderAutoCloseJob {

    private final OrderService orderService;

    @Value("${app.order.payment-timeout-minutes:15}")
    private int paymentTimeoutMinutes;

    @Scheduled(cron = "0 */1 * * * ?")
    public void autoCloseExpiredOrders() {
        log.info("Running unpaid order auto-close task...");
        try {
            orderService.closeExpiredOrders(paymentTimeoutMinutes);
        } catch (Exception e) {
            log.error("Order auto-close task failed", e);
        }
    }
}
