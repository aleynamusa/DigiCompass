import com.digicompass.backend.controller.mapper.ReviewMapperController;
import com.digicompass.backend.repository.entity.RoleEntity;
import com.digicompass.backend.repository.entity.RouteEntity;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.repository.repositories.ReviewJpaRepository;
import com.digicompass.backend.repository.repositories.RouteJpaRepository;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import com.digicompass.backend.application.mapper.ReviewMapper;
import com.digicompass.backend.application.models.Review;
import com.digicompass.backend.application.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = com.digicompass.backend.BackendApplication.class
)
@AutoConfigureMockMvc
@Testcontainers
class ReviewControllerIntegrationTest {

    private static final DockerImageName POSTGIS_IMAGE = DockerImageName
            .parse("postgis/postgis:17-3.5")
            .asCompatibleSubstituteFor("postgres");

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGIS_IMAGE)
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerPgProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReviewJpaRepository reviewRepository;

    @Autowired
    private RouteJpaRepository routeRepository;

    @Autowired
    private UserJpaRepository userRepository;
    @Autowired
    private ReviewMapperController reviewMapperController;
    @Autowired
    private ReviewMapper reviewMapper;

    @BeforeEach
    void cleanDatabase() {
        reviewRepository.deleteAll();
    }

    @Test
    void createReview_savesToDatabase() throws Exception {
        long countBefore = reviewRepository.count();

        mockMvc.perform(multipart("/review")
                        .file(new MockMultipartFile("images", "img.jpg", "image/jpeg", "fake".getBytes()))
                        .param("routeId", "1")
                        .param("review", "Great route!")
                        .param("userId.id", "1")
                        .param("userId.username", "TestUser")
                        .param("createdAt", "2025-11-12T20:00:00")
                        .param("updatedAt", "2025-11-12T20:00:00")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());

        assertThat(reviewRepository.count()).isEqualTo(countBefore + 1);
    }

//    @Test
//    void getReviewsByRoute_returnsReviews() throws Exception {
//
//        UserEntity user = new UserEntity();
//
//        user.setUsername("lele");
//        user.setEmail("lele@example.com");
//        user.setPassword("dummy");
//        user.setRole(new RoleEntity(1L, "user"));
//        user = userRepository.save(user);
//
//        RouteEntity route = new RouteEntity();
//        RouteEntity savedRoute = routeRepository.save(route);
//        Long generatedId = savedRoute.getId();
//        route.setName("Test Route");
//        route.setDescription("Just a dummy route for testing");
//        route.setCreatedAt(LocalDateTime.now());
//        route.setUpdatedAt(LocalDateTime.now());
//        route.setCreatedByUserId(user);
//        route.setDistance(5.0F);
//        route.setDuration("1.30");
//        routeRepository.save(route);
//
//
//        Review review = new Review();
//        review.setReview("Nice route!");
//        review.setRouteId(route.getId());
//        review.setUserId(new User(user.getId(), user.getUsername()));
//        review.setCreatedAt(LocalDateTime.now());
//        review.setUpdatedAt(LocalDateTime.now());
//        reviewRepository.save(reviewMapper.toEntity(review));
//
//        mockMvc.perform(get("/review/route/{routeId}", 10L))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].review").value("Nice route!"));
//    }

}