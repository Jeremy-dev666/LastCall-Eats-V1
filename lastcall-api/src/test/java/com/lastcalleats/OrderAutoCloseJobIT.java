package com.lastcalleats;

import com.lastcalleats.marketplace.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class OrderAutoCloseJobIT extends AbstractIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void expiredPendingOrderIsCancelledAndStockRestored() throws Exception {
        String merchantToken = registerMerchant(uniqueEmail("merchant"));
        long templateId = createTemplate(merchantToken);
        long listingId = createListing(merchantToken, templateId, 1);

        String user = registerUser(uniqueEmail("user"));
        long orderId = createOrder(user, listingId).path("data").path("id").asLong();

        // simulate the 15-minute payment window having elapsed
        jdbcTemplate.update(
                "UPDATE orders SET created_at = created_at - INTERVAL '20 minutes' WHERE id = ?", orderId);

        orderService.closeExpiredOrders(15);

        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM orders WHERE id = ?", String.class, orderId);
        assertThat(status).isEqualTo("CANCELLED");

        Integer remaining = jdbcTemplate.queryForObject(
                "SELECT remaining_quantity FROM product_listing WHERE id = ?", Integer.class, listingId);
        Boolean available = jdbcTemplate.queryForObject(
                "SELECT is_available FROM product_listing WHERE id = ?", Boolean.class, listingId);
        assertThat(remaining).isEqualTo(1);
        assertThat(available).isTrue();
    }

    @Test
    void recentPendingOrderIsLeftUntouched() throws Exception {
        String merchantToken = registerMerchant(uniqueEmail("merchant"));
        long templateId = createTemplate(merchantToken);
        long listingId = createListing(merchantToken, templateId, 1);

        String user = registerUser(uniqueEmail("user"));
        long orderId = createOrder(user, listingId).path("data").path("id").asLong();

        orderService.closeExpiredOrders(15);

        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM orders WHERE id = ?", String.class, orderId);
        assertThat(status).isEqualTo("PENDING_PAYMENT");
    }
}
