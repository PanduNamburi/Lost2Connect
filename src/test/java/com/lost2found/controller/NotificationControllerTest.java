package com.lost2found.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lost2found.dto.LoginRequest;
import com.lost2found.dto.RegisterRequest;
import com.lost2found.entity.NotificationType;
import com.lost2found.repository.NotificationRepository;
import com.lost2found.repository.UserRepository;
import com.lost2found.service.NotificationService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    private String authToken;
    private String userId;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        notificationRepository.deleteAll();

        RegisterRequest register = new RegisterRequest("Notif User", "notifuser", "notifuser@example.com", "Password123!", "+1111111111");
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)));

        LoginRequest login = new LoginRequest("notifuser", "Password123!");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        authToken = objectMapper.readTree(responseBody).path("data").path("accessToken").asText();
        userId = objectMapper.readTree(responseBody).path("data").path("id").asText();
    }

    @Test
    void testGetMyNotifications_AndMarkAsRead() throws Exception {
        // Send a notification to the user
        var notif = notificationService.sendNotification(userId, "Match Alert", "Potential match found for your item", NotificationType.MATCH_FOUND, "item123");

        // Get notifications
        mockMvc.perform(get("/api/v1/notifications/my")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].title").value("Match Alert"))
                .andExpect(jsonPath("$.data[0].isRead").value(false));

        // Mark as read
        mockMvc.perform(patch("/api/v1/notifications/" + notif.getId() + "/read")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isRead").value(true));
    }
}
