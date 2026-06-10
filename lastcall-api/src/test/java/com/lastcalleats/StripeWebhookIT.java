package com.lastcalleats;

import com.fasterxml.jackson.databind.JsonNode;
import com.stripe.Stripe;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StripeWebhookIT extends AbstractIntegrationTest {

    /** Must match app.stripe.webhook-secret in application-test.yml. */
    private static final String WEBHOOK_SECRET = "whsec_test_secret";

    @Test
    void paymentSucceededWebhookMarksOrderPaid() throws Exception {
        String merchantToken = registerMerchant(uniqueEmail("merchant"));
        long templateId = createTemplate(merchantToken);
        long listingId = createListing(merchantToken, templateId, 1);
        String user = registerUser(uniqueEmail("user"));
        long orderId = createOrder(user, listingId).path("data").path("id").asLong();

        String payload = paymentIntentEvent("payment_intent.succeeded", orderId);

        mockMvc.perform(post("/api/payment/webhook")
                        .contentType(APPLICATION_JSON)
                        .content(payload)
                        .header("Stripe-Signature", sign(payload)))
                .andExpect(status().isOk());

        JsonNode order = getJson("/api/orders/" + orderId, user);
        assertThat(order.path("data").path("status").asText()).isEqualTo("PAID");
    }

    @Test
    void webhookWithInvalidSignatureIsRejected() throws Exception {
        String payload = paymentIntentEvent("payment_intent.succeeded", 999999L);

        mockMvc.perform(post("/api/payment/webhook")
                        .contentType(APPLICATION_JSON)
                        .content(payload)
                        .header("Stripe-Signature", "t=12345,v1=deadbeef"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void replayedSucceededWebhookDoesNotBreakPaidOrder() throws Exception {
        String merchantToken = registerMerchant(uniqueEmail("merchant"));
        long templateId = createTemplate(merchantToken);
        long listingId = createListing(merchantToken, templateId, 1);
        String user = registerUser(uniqueEmail("user"));
        long orderId = createOrder(user, listingId).path("data").path("id").asLong();

        String payload = paymentIntentEvent("payment_intent.succeeded", orderId);
        mockMvc.perform(post("/api/payment/webhook")
                        .contentType(APPLICATION_JSON)
                        .content(payload)
                        .header("Stripe-Signature", sign(payload)))
                .andExpect(status().isOk());

        // Stripe may deliver the same event more than once; replay must not corrupt state
        mockMvc.perform(post("/api/payment/webhook")
                        .contentType(APPLICATION_JSON)
                        .content(payload)
                        .header("Stripe-Signature", sign(payload)))
                .andReturn();

        JsonNode order = getJson("/api/orders/" + orderId, user);
        assertThat(order.path("data").path("status").asText()).isEqualTo("PAID");
    }

    private String paymentIntentEvent(String type, long orderId) {
        return """
                {
                  "id": "evt_test_%d",
                  "object": "event",
                  "api_version": "%s",
                  "type": "%s",
                  "data": {
                    "object": {
                      "id": "pi_test_%d",
                      "object": "payment_intent",
                      "metadata": {"orderId": "%d"}
                    }
                  }
                }
                """.formatted(orderId, Stripe.API_VERSION, type, orderId, orderId);
    }

    /** Computes a valid Stripe-Signature header the same way Stripe's SDK expects it. */
    private String sign(String payload) throws Exception {
        long timestamp = Instant.now().getEpochSecond();
        String signedPayload = timestamp + "." + payload;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(WEBHOOK_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        String v1 = HexFormat.of().formatHex(mac.doFinal(signedPayload.getBytes(StandardCharsets.UTF_8)));
        return "t=" + timestamp + ",v1=" + v1;
    }
}
