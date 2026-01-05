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
import java.util.List;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
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


    @Autowired
    private MockMvc mockMvc;

    private UserEntity createAndSaveTestUser(String username, String email) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setPassword("pw");
        return userRepository.saveAndFlush(user);
    }

    private UserPrincipal createUserPrincipal(UserEntity user) {
        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    private UserEntity testUser;
    private UserPrincipal testPrincipal;

    TripRequestDto dto = new TripRequestDto();


    @BeforeEach
    void setUp() {
        testUser = createAndSaveTestUser("testuser", "test@mail.com");
        testPrincipal = createUserPrincipal(testUser);

        dto.setName("Test Trip");
        dto.setDescription("Test Description");
        dto.setPlannedDate(LocalDateTime.now().plusDays(1));
        dto.setRouteId(1L);
        dto.setAccessibility(true);
    }

    private UsernamePasswordAuthenticationToken createAuthToken(UserPrincipal principal) {
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
    }

    @Test
    void shouldCreateTrip_success() throws Exception {
        mockMvc.perform(post("/trip")
                        .with(authentication(createAuthToken(testPrincipal)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}
