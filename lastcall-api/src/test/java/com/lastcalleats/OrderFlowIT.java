package com.lastcalleats;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderFlowIT extends AbstractIntegrationTest {

    @Test
    void orderingDecrementsStockUntilSoldOut() throws Exception {
        String merchantToken = registerMerchant(uniqueEmail("merchant"));
        long templateId = createTemplate(merchantToken);
        long listingId = createListing(merchantToken, templateId, 2);

        String alice = registerUser(uniqueEmail("alice"));
        String bob = registerUser(uniqueEmail("bob"));
        String carol = registerUser(uniqueEmail("carol"));

        JsonNode first = createOrder(alice, listingId);
        assertThat(first.path("code").asInt()).isEqualTo(200);
        assertThat(first.path("data").path("status").asText()).isEqualTo("PENDING_PAYMENT");
        assertThat(first.path("data").path("numericCode").asText()).hasSize(6);

        // same user cannot order the same listing twice
        JsonNode duplicate = createOrder(alice, listingId);
        assertThat(duplicate.path("code").asInt()).isEqualTo(400);

        JsonNode second = createOrder(bob, listingId);
        assertThat(second.path("code").asInt()).isEqualTo(200);

        // stock exhausted -> third user is rejected
        JsonNode third = createOrder(carol, listingId);
        assertThat(third.path("code").asInt()).isEqualTo(400);

        JsonNode listings = getJson("/api/merchant/listings", merchantToken);
        for (JsonNode item : listings.path("data")) {
            if (item.path("id").asLong() == listingId) {
                assertThat(item.path("remainingQuantity").asInt()).isZero();
                assertThat(item.path("isAvailable").asBoolean()).isFalse();
            }
        }
    }

    @Test
    void userCannotReadAnotherUsersOrder() throws Exception {
        String merchantToken = registerMerchant(uniqueEmail("merchant"));
        long templateId = createTemplate(merchantToken);
        long listingId = createListing(merchantToken, templateId, 1);

        String owner = registerUser(uniqueEmail("owner"));
        String intruder = registerUser(uniqueEmail("intruder"));

        long orderId = createOrder(owner, listingId).path("data").path("id").asLong();

        JsonNode forbidden = getJson("/api/orders/" + orderId, intruder);
        assertThat(forbidden.path("code").asInt()).isNotEqualTo(200);
    }
}
