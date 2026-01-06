package com.digicompass.backend.integration;

import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
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


    private UserEntity createAndSaveTestUser(String username, String email) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setPassword("pw");
        return userRepository.saveAndFlush(user);
    }

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = createAndSaveTestUser("testuser1", "test1@mail.com");
    }


    @Test
    void shouldFavoriteRoute_success() throws Exception {
        mockMvc.perform(post("/action/favorite")
                        .param("routeId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Route favorited successfully."));
    }

    @Test
    void shouldUnfavoriteRoute_success() throws Exception {
        mockMvc.perform(post("/action/unfavorite")
                        .param("routeId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Route unfavorited successfully."));
    }

    @Test
    void isLikedRouteShould_returnTrue() throws Exception {

        mockMvc.perform(post("/action/favorite")
                        .param("routeId", "1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/action/isLiked")
                        .param("routeId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }



    @Test
    void shouldIsLikedRoute_returnFalse() throws Exception {

        mockMvc.perform(get("/action/isLiked")
                        .param("routeId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }


}