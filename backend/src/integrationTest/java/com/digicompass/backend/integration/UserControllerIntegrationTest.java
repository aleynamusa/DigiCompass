package com.digicompass.backend.integration;

import com.digicompass.backend.repository.entity.RoleEntity;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.repository.repositories.RoleJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import java.time.LocalDate;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RoleJpaRepository roleRepo;

    UserEntity user = new UserEntity();

    @BeforeEach
    void setup() {
        RoleEntity role = roleRepo.findByRole("ADMIN");
        if (role == null) {
            role = new RoleEntity();
            role.setRole("ADMIN");
            role = roleRepo.save(role);
        }

            user = new UserEntity();
            user.setUsername("admin1");
            user.setEmail("admin1@mail.com");
            user.setBirthDate(LocalDate.of(1995, 1, 1));
            user.setImageUrl("imageurl");
            user.setPassword("oldpassword123");
            user.setBio("Bio");
            user.setRole(role);
            user.setPublicProfile(true);
            user = userRepository.save(user);

    }

    @AfterEach
    void tearDown() {
        if (user != null && user.getId() != null) {
            userRepository.deleteById(user.getId());
        }
    }

    @Test
    void shouldReturnUserById() throws Exception {
        mockMvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.birthDate").value(user.getBirthDate().toString()))
                .andExpect(jsonPath("$.imageUrl", containsString("imageurl")))
                .andExpect(jsonPath("$.bio").value(user.getBio()))
                .andExpect(jsonPath("$.publicProfile").value(user.isPublicProfile()));
    }

    @Test
    void shouldReturnInternalServerErrorWhenUserNotFound() throws Exception {
        mockMvc.perform(get("/users/9999"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void usernameShouldBeTaken() throws Exception {
        mockMvc.perform(get("/users/usernames")
                        .param("username", "testuser")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(false)))
                .andExpect(jsonPath("$.message", is("Username is taken")));
    }

    @Test
    void usernameShouldBeAvailable() throws Exception {
        mockMvc.perform(get("/users/usernames")
                        .param("username", "newUser123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.message", is("Username is available")));
    }

    @Test
    void emailShouldBeTaken() throws Exception {
        mockMvc.perform(get("/users/emails")
                        .param("email", "test@mail.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(false)))
                .andExpect(jsonPath("$.message", is("Email is already registered")));
    }

    @Test
    void emailShouldBeAvailable() throws Exception {
        mockMvc.perform(get("/users/emails")
                        .param("email", "newUser123@gmail.com")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available", is(true)))
                .andExpect(jsonPath("$.message", is("Email is available")));
    }

    @Test
    void shouldUpdateProfilePicture() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "dummy image content".getBytes()
        );

        mockMvc.perform(multipart("/users/profilePictureUpdate/{id}", user.getId())
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profile picture updated successfully"));
    }

    @Test
    void shouldRejectEmptyProfilePicture() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[0]
        );

        mockMvc.perform(multipart("/users/profilePictureUpdate/{id}", user.getId())
                        .file(emptyFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingProfilePictureForMissingUser() throws Exception {
        userRepository.deleteById(user.getId());

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, "bytes".getBytes()
        );

        mockMvc.perform(multipart("/users/profilePictureUpdate/{id}", user.getId())
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No such element server error"));
    }

    @Test
    void shouldReturnInternalServerErrorForInvalidUserRoutes() throws Exception {
        mockMvc.perform(get("/users/{id}/routes", 99999))
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldReturnUserSearchResults() throws Exception {
        mockMvc.perform(get("/users/search")
                        .param("username", "admin1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(user.getId()))
                .andExpect(jsonPath("$[0].username").value(user.getUsername()))
                .andExpect(jsonPath("$[0].email").value(user.getEmail()))
                .andExpect(jsonPath("$[0].birthDate").value(user.getBirthDate().toString()))
                .andExpect(jsonPath("$[0].imageUrl", containsString("imageurl")))
                .andExpect(jsonPath("$[0].bio").value(user.getBio()))
                .andExpect(jsonPath("$[0].publicProfile").value(user.isPublicProfile()));
    }

}
