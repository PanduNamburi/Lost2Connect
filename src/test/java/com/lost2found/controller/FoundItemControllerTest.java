package com.lost2found.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lost2found.dto.CreateFoundItemRequest;
import com.lost2found.dto.LoginRequest;
import com.lost2found.dto.RegisterRequest;
import com.lost2found.entity.ItemCategory;
import com.lost2found.repository.FoundItemRepository;
import com.lost2found.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FoundItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoundItemRepository foundItemRepository;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        foundItemRepository.deleteAll();

        RegisterRequest register = new RegisterRequest("Finder One", "finderone", "finderone@example.com", "Password123!", "+1111111111");
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)));

        LoginRequest login = new LoginRequest("finderone", "Password123!");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        authToken = objectMapper.readTree(responseBody).path("data").path("accessToken").asText();
    }

    @Test
    void testCreateFoundItem_Success() throws Exception {
        CreateFoundItemRequest request = new CreateFoundItemRequest(
                "Car Keys with Keychain",
                "Set of Toyota car keys found near park bench",
                ItemCategory.KEYS,
                "2026-09-07 16:30:00",
                "Security Desk - Gate 2",
                "Golden Gate Park",
                "San Francisco",
                37.7694,
                -122.4862,
                "+1234567890",
                List.of("What brand logo is on the keychain?")
        );

        mockMvc.perform(post("/api/v1/found-items")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Car Keys with Keychain"))
                .andExpect(jsonPath("$.data.category").value("KEYS"))
                .andExpect(jsonPath("$.data.status").value("FOUND"))
                .andExpect(jsonPath("$.data.storageLocation").value("Security Desk - Gate 2"));
    }

    @Test
    void testSearchFoundItems_PublicAccess() throws Exception {
        CreateFoundItemRequest request = new CreateFoundItemRequest(
                "Ray-Ban Sunglasses",
                "Black polarized sunglasses in brown leather case",
                ItemCategory.OTHER,
                "2026-09-07 11:00:00",
                "Lost & Found Vault Desk 3",
                "Central Terminal",
                "Chicago",
                41.8781,
                -87.6298,
                "+1987654321",
                List.of("Are there any initials engraved on the case?")
        );

        mockMvc.perform(post("/api/v1/found-items")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(get("/api/v1/found-items/search")
                        .param("keyword", "Ray-Ban")
                        .param("city", "Chicago")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("Ray-Ban Sunglasses"));
    }
}
