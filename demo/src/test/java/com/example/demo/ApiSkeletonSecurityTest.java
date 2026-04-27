package com.example.demo;

import com.example.demo.security.JwtService;
import com.example.demo.service.AuthService;
import com.example.demo.service.MenuService;
import com.example.demo.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiSkeletonSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private AuthService authService;

    @MockBean
    private MenuService menuService;

    @MockBean
    private ReservationService reservationService;

    @Test
    void authLoginEndpointIsPublic() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void userEndpointRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/user/menus/today"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpointRejectsUserToken() throws Exception {
        String userToken = jwtService.generateUserToken(1L, "TestUser");

        mockMvc.perform(get("/api/admin/menus")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpointAllowsAdminToken() throws Exception {
        String adminToken = jwtService.generateAdminToken(1L, "AdminUser");

        mockMvc.perform(get("/api/admin/menus")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void invalidTokenIsRejected() throws Exception {
        mockMvc.perform(get("/api/admin/menus")
                        .header("Authorization", "Bearer invalid-jwt-token"))
                .andExpect(status().isUnauthorized());
    }
}