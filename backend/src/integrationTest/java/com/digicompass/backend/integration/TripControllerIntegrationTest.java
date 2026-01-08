package com.digicompass.backend.integration;

import com.digicompass.backend.configuration.UserPrincipal;
import com.digicompass.backend.controller.dto.request.TripRequestDto;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@Rollback
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class TripControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;



    private UserEntity createAndSaveTestUser(String username, String email) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setPassword("pw");
        return userRepository.saveAndFlush(user);
    }

    private UserEntity testUser;

    TripRequestDto dto = new TripRequestDto();


    @BeforeEach
    void setUp() {
        testUser = createAndSaveTestUser("testuser1", "test1@mail.com");

        dto.setName("Test Trip");
        dto.setDescription("Test Description");
        dto.setPlannedDate(LocalDateTime.now().plusDays(1));
        dto.setRouteId(1L);
        dto.setAccessibility(true);
    }


    @Test
    void shouldCreateTrip_success() throws Exception {
        mockMvc.perform(post("/trip")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}
