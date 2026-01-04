package com.digicompass.backend.integration;

import com.digicompass.backend.configuration.UserPrincipal;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@Transactional
@Rollback
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class UserActionsIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserJpaRepository userRepository;

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

    @BeforeEach
    void setUp() {
        testUser = createAndSaveTestUser("testuser", "test@mail.com");
        testPrincipal = createUserPrincipal(testUser);
    }

    private UsernamePasswordAuthenticationToken createAuthToken(UserPrincipal principal) {
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
    }

    @Test
    void shouldFavoriteRoute_success() throws Exception {
        mockMvc.perform(post("/action/favorite")
                        .param("routeId", "1")
                        .with(authentication(createAuthToken(testPrincipal))))
                .andExpect(status().isOk())
                .andExpect(content().string("Route favorited successfully."));
    }

    @Test
    void shouldUnfavoriteRoute_success() throws Exception {
        mockMvc.perform(post("/action/unfavorite")
                        .param("routeId", "1")
                        .with(authentication(createAuthToken(testPrincipal))))
                .andExpect(status().isOk())
                .andExpect(content().string("Route unfavorited successfully."));
    }

    @Test
    void isLikedRouteShould_returnTrue() throws Exception {

        mockMvc.perform(post("/action/favorite")
                        .param("routeId", "1")
                        .with(authentication(createAuthToken(testPrincipal))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/action/isLiked")
                        .param("routeId", "1")
                        .with(authentication(createAuthToken(testPrincipal))))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void shouldIsLikedRoute_returnFalse() throws Exception {

        mockMvc.perform(get("/action/isLiked")
                        .param("routeId", "1")
                .with(authentication(createAuthToken(testPrincipal))))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }
}