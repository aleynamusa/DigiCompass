package com.digicompass.backend.integration;

import com.digicompass.backend.repository.entity.RoleEntity;
import com.digicompass.backend.repository.entity.UserEntity;
import com.digicompass.backend.repository.repositories.RoleJpaRepository;
import com.digicompass.backend.repository.repositories.UserJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class UserControllerIntegrationTest  extends BaseIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userRepo;

    @Autowired
    private RoleJpaRepository roleRepo;

    UserEntity user = new UserEntity();

    @BeforeEach
    void setup() {
        RoleEntity role = roleRepo.findByRole("USER");
        if (role == null) {
            role = new RoleEntity();
            role.setRole("USER");
            role = roleRepo.save(role);
        }

        user = userRepo.findByEmail("test@mail.com");
        if (user == null) {
            user = new UserEntity();
            user.setUsername("testuser");
            user.setEmail("test@mail.com");
            user.setBirthDate(LocalDate.of(1995, 1, 1));
            user.setPassword("oldpassword123");
            user.setRole(role);
            user = userRepo.save(user);
        }
    }

    @AfterEach
    void tearDown() {
        if (user != null && user.getId() != null) {
            userRepo.deleteById(user.getId());
        }
    }

    @Test
    void shouldReturnUserById() throws Exception {
        mockMvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@mail.com"))
                .andExpect(jsonPath("$.birthDate").value("1995-01-01"));
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
                .andExpect(status().isOk());
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
                .andExpect(status().isBadRequest())
                .andExpect(content().string("File must not be empty"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingProfilePictureForMissingUser() throws Exception {
        userRepo.deleteById(user.getId());

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
                        .param("username", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username", is("testuser")));
    }

}
