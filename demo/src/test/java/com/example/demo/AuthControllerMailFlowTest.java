package com.example.demo;

import com.example.demo.service.AuthService;
import com.example.demo.service.EmailVerificationService;
import com.example.demo.service.PasswordResetService;
import com.example.demo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerMailFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordResetService passwordResetService;

    @MockBean
    private EmailVerificationService emailVerificationService;

    @Test
    void userCanVerifyEmailWithToken() throws Exception {
        doNothing().when(emailVerificationService).verifyEmail("verify-token");

        mockMvc.perform(get("/api/auth/verify-email").param("token", "verify-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("メール認証が完了しました"));
    }
}