package com.lost2found.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lost2found.dto.ClaimItemRequest;
import com.lost2found.dto.CreateFoundItemRequest;
import com.lost2found.dto.CreateLostItemRequest;
import com.lost2found.dto.LoginRequest;
import com.lost2found.dto.RegisterRequest;
import com.lost2found.entity.ItemCategory;
import com.lost2found.repository.FoundItemRepository;
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

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SmartMatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LostItemRepository lostItemRepository;

    @Autowired
    private FoundItemRepository foundItemRepository;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        lostItemRepository.deleteAll();
        foundItemRepository.deleteAll();

        RegisterRequest register = new RegisterRequest("Match User", "matchuser", "matchuser@example.com", "Password123!", "+1111111111");
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)));

        LoginRequest login = new LoginRequest("matchuser", "Password123!");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        authToken = objectMapper.readTree(responseBody).path("data").path("accessToken").asText();
    }

    @Test
    void testSmartMatchEngine_CalculatesMatchScoreCorrectly() throws Exception {
        // 1. Create Lost Item
        CreateLostItemRequest lostReq = new CreateLostItemRequest(
                "Black Leather Wallet",
                "Lost black leather wallet containing driver license",
                ItemCategory.WALLETS_CARDS,
                "2026-09-07 10:00:00",
                "Central Station",
                "New York",
                40.7128,
                -74.0060,
                50.0,
                "+1234567890"
        );

        MvcResult lostResult = mockMvc.perform(post("/api/v1/lost-items")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lostReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String lostItemId = objectMapper.readTree(lostResult.getResponse().getContentAsString()).path("data").path("id").asText();

        // 2. Create Found Item matching category & location
        CreateFoundItemRequest foundReq = new CreateFoundItemRequest(
                "Leather Wallet with ID",
                "Found black leather wallet with cards inside",
                ItemCategory.WALLETS_CARDS,
                "2026-09-07 12:00:00",
                "Station Security Desk",
                "Central Station",
                "New York",
                40.7128,
                -74.0060,
                "+1987654321",
                List.of("What is the initial on the front?")
        );

        MvcResult foundResult = mockMvc.perform(post("/api/v1/found-items")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foundReq)))
                .andExpect(status().isCreated())
                .andReturn();

        String foundItemId = objectMapper.readTree(foundResult.getResponse().getContentAsString()).path("data").path("id").asText();

        // 3. Query Smart Match for Lost Item
        mockMvc.perform(get("/api/v1/matches/lost/" + lostItemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].foundItem.id").value(foundItemId))
                .andExpect(jsonPath("$.data[0].matchScore.isMatch").value(true))
                .andExpect(jsonPath("$.data[0].matchScore.categoryScore").value(40.0));

        // 4. Submit Claim
        ClaimItemRequest claimReq = new ClaimItemRequest(lostItemId, foundItemId, "Drivers license has name Match User", "+1234567890");
        mockMvc.perform(post("/api/v1/matches/claim")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(claimReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.foundItem.status").value("CLAIMED"))
                .andExpect(jsonPath("$.data.lostItem.status").value("CLAIMED"));
    }
}
