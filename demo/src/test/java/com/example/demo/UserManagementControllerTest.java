package com.example.demo;

import com.example.demo.entity.User;
import com.example.demo.security.JwtService;
import com.example.demo.service.EmailVerificationService;
import com.example.demo.service.MailService;
import com.example.demo.service.PasswordResetService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @MockBean
    private EmailVerificationService emailVerificationService;

    @MockBean
    private MailService mailService;

    @MockBean
    private PasswordResetService passwordResetService;

    private String userToken;

    @BeforeEach
    void setUp() {
        userToken = jwtService.generateUserToken(7L, "TestUser");
    }

    @Test
    void userCanRegister() throws Exception {
        User user = new User();
        user.setId(2L);
        user.setName("Taro");
        user.setEmail("taro@example.com");

        when(userService.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Taro\",\"email\":\"taro@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("taro@example.com"));

        verify(emailVerificationService).sendVerificationEmail(any(User.class));
        verify(mailService, never()).sendMail(any(), any(), any());
    }

    @Test
    void userCanGetOwnProfile() throws Exception {
        User user = new User();
        user.setId(7L);
        user.setName("Demo User");
        user.setEmail("user@example.com");

        when(userService.getUserById(7L)).thenReturn(user);

        mockMvc.perform(get("/api/user/profile").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Demo User"));
    }

    @Test
    void userCanUpdateOwnName() throws Exception {
        User user = new User();
        user.setId(7L);
        user.setName("Updated User");
        user.setEmail("user@example.com");

        when(userService.updateUserName(eq(7L), eq("Updated User"))).thenReturn(user);

        mockMvc.perform(put("/api/user/profile/name")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated User\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated User"));
    }

    @Test
    void userCanUpdateOwnPassword() throws Exception {
        User user = new User();
        user.setId(7L);
        user.setName("Demo User");
        user.setEmail("user@example.com");

        when(userService.updatePassword(eq(7L), eq("password456"))).thenReturn(user);

        mockMvc.perform(put("/api/user/profile/password")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"password456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("updated"));
    }

    @Test
    void userCanRequestPasswordResetMailFromProfile() throws Exception {
        doNothing().when(passwordResetService).createResetTokenForUser(7L);

        mockMvc.perform(post("/api/user/profile/password/reset-request")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("password reset requested"));
    }
}