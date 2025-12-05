package com.digicompass.backend.integration;


import com.digicompass.backend.application.interfaces.EmailService;
import com.digicompass.backend.application.interfaces.PasswordResetService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ResetPasswordControllerCatchIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private PasswordResetService passwordResetService;

    @Test
    void resetPasswordShouldReturnErrorWhenServiceThrows() throws Exception {
        Mockito.doThrow(new RuntimeException("Invalid token"))
                .when(passwordResetService)
                .resetPassword("badToken", "newPassword");

        mockMvc.perform(post("/password/reset")
                        .param("token", "badToken")
                        .param("password", "newPassword"))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void forgotPasswordShouldReturnBadRequestWhenServiceThrows() throws Exception {
        Mockito.doThrow(new RuntimeException("Failed to send email"))
                .when(passwordResetService)
                .createPasswordResetToken("invalid@mail.com");

        mockMvc.perform(post("/password/forgot")
                        .param("email", "invalid@mail.com"))
                .andExpect(status().isBadRequest());
    }

}
