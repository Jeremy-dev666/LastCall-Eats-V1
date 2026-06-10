package com.lastcalleats;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthFlowIT extends AbstractIntegrationTest {

    @Test
    void registerLoginAndAccessProtectedEndpoint() throws Exception {
        String email = uniqueEmail("auth");

        JsonNode registered = postJson("/api/auth/register/user", null,
                Map.of("email", email, "password", "password123", "nickname", "Alice"));
        assertThat(registered.path("code").asInt()).isEqualTo(200);
        assertThat(registered.path("data").path("token").asText()).isNotBlank();

        JsonNode login = postJson("/api/auth/login/user", null,
                Map.of("email", email, "password", "password123"));
        String token = login.path("data").path("token").asText();
        assertThat(token).isNotBlank();
        assertThat(login.path("data").path("role").asText()).isEqualTo("USER");

        mockMvc.perform(get("/api/orders").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpointRejectsMissingAndInvalidToken() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(get("/api/orders").header("Authorization", "Bearer not-a-real-token"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void loginFailsWithWrongPassword() throws Exception {
        String email = uniqueEmail("auth-wrong-pw");
        registerUser(email);

        JsonNode login = postJson("/api/auth/login/user",
                null, Map.of("email", email, "password", "wrong-password"));
        assertThat(login.path("code").asInt()).isNotEqualTo(200);
    }
}
