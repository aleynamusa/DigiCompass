package com.digicompass.backend.repository;

import com.digicompass.backend.repository.entity.RoleEntity;
import com.digicompass.backend.repository.repositories.RoleJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@TestPropertySource(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create"
})
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")

class RoleJpaRepositoryTest {

    @Autowired
    private RoleJpaRepository roleRepository;

    @Test
    void shouldSaveAndFindRoleByRoleName() {
        RoleEntity role = new RoleEntity();
        role.setRole("ADMIN");
        RoleEntity savedRole = roleRepository.save(role);

        assertThat(savedRole.getId()).isNotNull();

        RoleEntity foundRole = roleRepository.findByRole("ADMIN");
        assertThat(foundRole).isNotNull();
        assertThat(foundRole.getRole()).isEqualTo("ADMIN");
    }

    @Test
    void findByRoleShouldReturnNullIfNotFound() {
        RoleEntity role = roleRepository.findByRole("NON_EXISTENT_ROLE");
        assertThat(role).isNull();
    }
}
