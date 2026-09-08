package com.lost2found.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lost2found.dto.ClaimItemRequest;
import com.lost2found.dto.CreateFoundItemRequest;
import com.lost2found.dto.CreateLostItemRequest;
import com.lost2found.dto.LoginRequest;
import com.lost2found.dto.RegisterRequest;
import com.lost2found.dto.UpdateClaimStatusRequest;
import com.lost2found.entity.ClaimStatus;
import com.lost2found.entity.ItemCategory;
import com.lost2found.repository.ClaimRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClaimControllerTest {

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

    @Autowired
    private ClaimRepository claimRepository;

    private String finderToken;
    private String claimantToken;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        lostItemRepository.deleteAll();
        foundItemRepository.deleteAll();
        claimRepository.deleteAll();

        // Register Finder
        RegisterRequest finderReg = new RegisterRequest("Finder User", "finderuser", "finderuser@example.com", "Password123!", "+1111111111");
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(finderReg)));

        LoginRequest finderLogin = new LoginRequest("finderuser", "Password123!");
        MvcResult fResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(finderLogin)))
                .andReturn();
        finderToken = objectMapper.readTree(fResult.getResponse().getContentAsString()).path("data").path("accessToken").asText();

        // Register Claimant
        RegisterRequest claimantReg = new RegisterRequest("Claimant User", "claimantuser", "claimantuser@example.com", "Password123!", "+2222222222");
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(claimantReg)));

        LoginRequest claimantLogin = new LoginRequest("claimantuser", "Password123!");
        MvcResult cResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(claimantLogin)))
                .andReturn();
        claimantToken = objectMapper.readTree(cResult.getResponse().getContentAsString()).path("data").path("accessToken").asText();
    }

    @Test
    void testClaimLifecycle_SubmitAndApprove() throws Exception {
        // 1. Claimant posts Lost Item
        CreateLostItemRequest lostReq = new CreateLostItemRequest(
                "Gold Ring", "Lost 18k gold wedding ring", ItemCategory.JEWELRY,
                "2026-09-07 12:00:00", "Central Park", "New York", 40.7851, -73.9682, 200.0, "+2222222222"
        );
        MvcResult lostRes = mockMvc.perform(post("/api/v1/lost-items")
                        .header("Authorization", "Bearer " + claimantToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lostReq)))
                .andReturn();
        String lostId = objectMapper.readTree(lostRes.getResponse().getContentAsString()).path("data").path("id").asText();

        // 2. Finder posts Found Item
        CreateFoundItemRequest foundReq = new CreateFoundItemRequest(
                "Gold Band Ring", "Found gold wedding band ring in grass", ItemCategory.JEWELRY,
                "2026-09-07 14:00:00", "Security Desk", "Central Park", "New York", 40.7851, -73.9682, "+1111111111", List.of("What inscription is inside?")
        );
        MvcResult foundRes = mockMvc.perform(post("/api/v1/found-items")
                        .header("Authorization", "Bearer " + finderToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(foundReq)))
                .andReturn();
        String foundId = objectMapper.readTree(foundRes.getResponse().getContentAsString()).path("data").path("id").asText();

        // 3. Claimant Submits Claim
        ClaimItemRequest claimReq = new ClaimItemRequest(lostId, foundId, "Inscription inside reads Forever & Always", "+2222222222");
        MvcResult claimRes = mockMvc.perform(post("/api/v1/claims")
                        .header("Authorization", "Bearer " + claimantToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(claimReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andReturn();
        String claimId = objectMapper.readTree(claimRes.getResponse().getContentAsString()).path("data").path("id").asText();

        // 4. Finder approves claim
        UpdateClaimStatusRequest approveReq = new UpdateClaimStatusRequest(ClaimStatus.APPROVED);
        mockMvc.perform(patch("/api/v1/claims/" + claimId + "/status")
                        .header("Authorization", "Bearer " + finderToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approveReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("APPROVED"));

        // 5. Verify claimant notification received
        mockMvc.perform(get("/api/v1/notifications/my")
                        .header("Authorization", "Bearer " + claimantToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].type").value("CLAIM_APPROVED"));
    }
}
