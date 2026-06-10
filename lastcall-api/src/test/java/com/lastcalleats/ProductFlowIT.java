package com.lastcalleats;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductFlowIT extends AbstractIntegrationTest {

    @Test
    void merchantPublishesListingAndUserCanBrowseIt() throws Exception {
        String merchantToken = registerMerchant(uniqueEmail("merchant"));
        long templateId = createTemplate(merchantToken);
        long listingId = createListing(merchantToken, templateId, 3);
        assertThat(listingId).isPositive();

        JsonNode browse = getJson("/api/products/browse?page=0&size=100", null);
        assertThat(browse.path("code").asInt()).isEqualTo(200);

        boolean found = false;
        for (JsonNode item : browse.path("data").path("content")) {
            if (item.path("id").asLong() == listingId
                    || item.path("listingId").asLong() == listingId) {
                found = true;
                break;
            }
        }
        assertThat(found).as("published listing should appear in public browse feed").isTrue();
    }

    @Test
    void merchantSeesOwnListingWithRemainingQuantity() throws Exception {
        String merchantToken = registerMerchant(uniqueEmail("merchant"));
        long templateId = createTemplate(merchantToken);
        long listingId = createListing(merchantToken, templateId, 5);

        JsonNode listings = getJson("/api/merchant/listings", merchantToken);
        JsonNode mine = null;
        for (JsonNode item : listings.path("data")) {
            if (item.path("id").asLong() == listingId) {
                mine = item;
                break;
            }
        }
        assertThat(mine).isNotNull();
        assertThat(mine.path("remainingQuantity").asInt()).isEqualTo(5);
        assertThat(mine.path("isAvailable").asBoolean()).isTrue();
    }
}
