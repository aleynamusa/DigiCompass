package com.digicompass.backend.integration;

import com.digicompass.backend.repository.entity.RoleEntity;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.repository.repositories.RoleJpaRepository;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.http.MediaType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class ResetPasswordControllerIntegrationTest extends BaseIntegrationTest {


    @Autowired
    private UserJpaRepository userRepo;

    @Autowired
    private RoleJpaRepository roleRepo;

    @Autowired
    private StringRedisTemplate redisTemplate;


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
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset link sent"));

    }

    @Test
    void resetPasswordShouldUpdateUserPassword() throws Exception {

        mockMvc.perform(post("/password/forgot")
                        .param("email", "test@mail.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset link sent"));

        Set<String> keys = redisTemplate.keys("*");
        assertThat(keys)
                .withFailMessage("No password reset token found in Redis")
                .isNotEmpty();

        String token = keys.iterator().next();

        String storedEmail = redisTemplate.opsForValue().get(token);
        assertThat(storedEmail).isEqualTo("test@mail.com");

        mockMvc.perform(post("/password/reset")
                        .param("token", token)
                        .param("password", "NewSecret123@"))
                .andExpect(status().isOk())
                .andExpect(content().string("Password successfully reset"));

        UserEntity updatedUser = userRepo.findByEmail("test@mail.com");

        assertThat(updatedUser.getPassword())
                .isNotBlank()
                .isNotEqualTo("oldpassword123");

        assertThat(redisTemplate.hasKey(token)).isFalse();
    }

}