package com.digicompass.backend.integration;


import com.digicompass.backend.repository.entity.RoleEntity;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.repository.repositories.RoleJpaRepository;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.digicompass.backend.application.interfaces.EmailService;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ResetPasswordControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userRepo;

    @Autowired
    private RoleJpaRepository roleRepo;

    @MockitoBean
    private EmailService emailService;


    @BeforeEach
    void setup() {
        RoleEntity role = roleRepo.findByRole("USER");
        if (role == null) {
            role = new RoleEntity();
            role.setRole("USER");
            roleRepo.save(role);
        }

        if (userRepo.findByEmail("test@mail.com") == null) {
            UserEntity user = new UserEntity();
            user.setUsername("testuser");
            user.setEmail("test@mail.com");
            user.setBirthDate(LocalDate.of(1995, 1, 1));
            user.setPassword("oldpassword123");
            user.setRole(role);
            userRepo.save(user);
        }
    }

    @Test
    void forgotPasswordShouldGenerateTokenAndSendEmail() throws Exception {

        mockMvc.perform(post("/password/forgot")
                        .param("email", "test@mail.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(emailService, times(1)).sendResetLink(anyString(), anyString());
    }

    @Test
    void resetPasswordShouldUpdateUserPassword() throws Exception {
        mockMvc.perform(post("/password/forgot")
                        .param("email", "test@mail.com"))
                .andExpect(status().isOk());

        var captor = org.mockito.ArgumentCaptor.forClass(String.class);
        verify(emailService).sendResetLink(captor.capture(), captor.capture());

        String token = captor.getAllValues().get(1);

        mockMvc.perform(post("/password/reset")
                        .param("token", token)
                        .param("password", "newSecret123"))
                .andExpect(status().isOk());

        UserEntity updatedUser = userRepo.findByEmail("test@mail.com");

        assertThat(updatedUser.getPassword())
                .isNotEqualTo("oldpassword123")
                .isNotBlank();
    }
}