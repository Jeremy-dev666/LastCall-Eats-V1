package com.lastcalleats;

import com.lastcalleats.marketplace.order.dto.OrderRequest;
import com.lastcalleats.marketplace.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Documents inventory behavior under concurrent ordering. The invariant:
 * a listing with quantity N must never produce more than N active orders.
 *
 * <p>Measured on the current lock-free read-modify-write implementation
 * (2026-06-10, commit 6570d0b): 50 concurrent buyers against stock of 10
 * produced 50 successful orders — a 400% oversell. Disabled until the
 * Redis/Lua atomic deduction lands in P1 (T026–T032), which re-enables it.
 */
@org.junit.jupiter.api.Disabled("Known oversell race in OrderServiceImpl.createOrder; fixed by P1 T026-T032")
class OrderConcurrencyIT extends AbstractIntegrationTest {

    private static final int STOCK = 10;
    private static final int BUYERS = 50;

    @Autowired
    private OrderService orderService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void concurrentBuyersNeverOversellListing() throws Exception {
        String merchantToken = registerMerchant(uniqueEmail("merchant"));
        long templateId = createTemplate(merchantToken);
        long listingId = createListing(merchantToken, templateId, STOCK);

        List<Long> userIds = new ArrayList<>();
        for (int i = 0; i < BUYERS; i++) {
            userIds.add(jdbcTemplate.queryForObject(
                    "INSERT INTO \"user\" (email, password_hash, nickname) VALUES (?, 'x', 'buyer') RETURNING id",
                    Long.class, uniqueEmail("buyer")));
        }

        ExecutorService pool = Executors.newFixedThreadPool(BUYERS);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(BUYERS);
        AtomicInteger successes = new AtomicInteger();

        for (Long userId : userIds) {
            pool.submit(() -> {
                try {
                    start.await();
                    OrderRequest request = new OrderRequest();
                    request.setListingId(listingId);
                    orderService.createOrder(userId, request);
                    successes.incrementAndGet();
                } catch (Exception expectedForLosers) {
                    // sold out / race losers
                } finally {
                    done.countDown();
                }
            });
        }
        start.countDown();
        assertThat(done.await(60, TimeUnit.SECONDS)).isTrue();
        pool.shutdown();

        Integer orderCount = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM orders WHERE listing_id = ?", Integer.class, listingId);
        Integer remaining = jdbcTemplate.queryForObject(
                "SELECT remaining_quantity FROM product_listing WHERE id = ?", Integer.class, listingId);

        assertThat(successes.get())
                .as("successful orders (stock=%d, buyers=%d)", STOCK, BUYERS)
                .isEqualTo(STOCK);
        assertThat(orderCount).as("persisted orders").isEqualTo(STOCK);
        assertThat(remaining).as("remaining stock").isZero();
    }
}
