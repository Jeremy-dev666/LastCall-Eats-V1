package com.lastcalleats;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Shared base for integration tests: one PostgreSQL Testcontainer and one
 * Spring context per JVM. Flyway migrates the schema on context startup.
 * Tests isolate themselves with unique emails instead of cleaning tables.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected String uniqueEmail(String prefix) {
        return prefix + "-" + UUID.randomUUID() + "@test.local";
    }

    protected JsonNode postJson(String path, String token, Object body) throws Exception {
        MockHttpServletRequestBuilder req = post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body));
        if (token != null) {
            req = req.header("Authorization", "Bearer " + token);
        }
        String response = mockMvc.perform(req).andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response);
    }

    protected JsonNode getJson(String path, String token) throws Exception {
        MockHttpServletRequestBuilder req = get(path);
        if (token != null) {
            req = req.header("Authorization", "Bearer " + token);
        }
        String response = mockMvc.perform(req).andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response);
    }

    protected String registerUser(String email) throws Exception {
        JsonNode res = postJson("/api/auth/register/user", null,
                Map.of("email", email, "password", "password123", "nickname", "Tester"));
        return res.path("data").path("token").asText();
    }

    protected String registerMerchant(String email) throws Exception {
        JsonNode res = postJson("/api/auth/register/merchant", null,
                Map.of("email", email, "password", "password123",
                        "name", "Test Kitchen", "address", "1 Test St, Boston, MA"));
        return res.path("data").path("token").asText();
    }

    protected long createTemplate(String merchantToken) throws Exception {
        JsonNode res = postJson("/api/merchant/templates", merchantToken,
                Map.of("name", "Surprise Bag", "description", "integration test bag",
                        "originalPrice", 10.00));
        return res.path("data").path("id").asLong();
    }

    protected long createListing(String merchantToken, long templateId, int quantity) throws Exception {
        JsonNode res = postJson("/api/merchant/listings", merchantToken,
                Map.of("templateId", templateId, "discountPrice", 4.50, "quantity", quantity,
                        "pickupStart", "17:00", "pickupEnd", "19:00",
                        "date", LocalDate.now().toString()));
        return res.path("data").path("id").asLong();
    }

    protected JsonNode createOrder(String userToken, long listingId) throws Exception {
        return postJson("/api/orders", userToken, Map.of("listingId", listingId));
    }
}
