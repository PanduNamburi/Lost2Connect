package com.lost2found.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lost2found.dto.CreateLostItemRequest;
import com.lost2found.dto.LoginRequest;
import com.lost2found.dto.RegisterRequest;
import com.lost2found.entity.ItemCategory;
import com.lost2found.repository.LostItemRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LostItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LostItemRepository lostItemRepository;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        lostItemRepository.deleteAll();

        // Register and Login user to obtain JWT
        RegisterRequest register = new RegisterRequest("User One", "userone", "userone@example.com", "Password123!", "+1111111111");
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)));

        LoginRequest login = new LoginRequest("userone", "Password123!");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        authToken = objectMapper.readTree(responseBody).path("data").path("accessToken").asText();
    }

    @Test
    void testCreateLostItem_Success() throws Exception {
        CreateLostItemRequest request = new CreateLostItemRequest(
                "iPhone 15 Pro",
                "Black titanium iPhone 15 Pro lost near library",
                ItemCategory.ELECTRONICS,
                "2026-09-07 14:00:00",
                "Main Library 2nd Floor",
                "San Francisco",
                37.7749,
                -122.4194,
                100.0,
                "+1234567890"
        );

        mockMvc.perform(post("/api/v1/lost-items")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("iPhone 15 Pro"))
                .andExpect(jsonPath("$.data.category").value("ELECTRONICS"))
                .andExpect(jsonPath("$.data.status").value("LOST"));
    }

    @Test
    void testSearchLostItems_PublicAccess() throws Exception {
        CreateLostItemRequest request = new CreateLostItemRequest(
                "MacBook Air M2",
                "Silver MacBook Air left in coffee shop",
                ItemCategory.ELECTRONICS,
                "2026-09-07 10:00:00",
                "Downtown Cafe",
                "Seattle",
                47.6062,
                -122.3321,
                50.0,
                "+1987654321"
        );

        mockMvc.perform(post("/api/v1/lost-items")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(get("/api/v1/lost-items/search")
                        .param("keyword", "MacBook")
                        .param("city", "Seattle")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title").value("MacBook Air M2"));
    }
}
