package com.digicompass.backend.integration;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = com.digicompass.backend.BackendApplication.class
)
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)// context is being removed from the cache
@AutoConfigureMockMvc(addFilters = false)
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public abstract class BaseIntegrationTest {
    private static final DockerImageName POSTGIS_IMAGE = DockerImageName
            .parse("postgis/postgis:17-3.5")
            .asCompatibleSubstituteFor("postgres");

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGIS_IMAGE)
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    // Redis Testcontainer
    @Container
    static GenericContainer<?> redis =
            new GenericContainer<>("redis:7-alpine")
                    .withExposedPorts(6379);

    @DynamicPropertySource
    static void registerPgProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
        registry.add("spring.flyway.clean-on-migrate", () -> true);
        registry.add("spring.flyway.clean-disabled", () -> false);

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);

//        registry.add("spring.flyway.post-clean-sql", () ->
//                "ALTER SEQUENCE routes_id_seq RESTART WITH 1;" +
//                        "ALTER SEQUENCE users_id_seq RESTART WITH 1;" +
//                        "ALTER SEQUENCE rating_id_seq RESTART WITH 1;"
//        );
    }

}
