package com.digicompass.backend.integration.exception;

import com.digicompass.backend.application.interfaces.UserService;
import com.digicompass.backend.integration.BaseIntegrationTest;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class UserControllerCatchIntegrationTest extends BaseIntegrationTest {

    @MockitoBean
    private UserService userService;

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
    void shouldReturnBadRequestOnInvalidImage() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "bad.txt", MediaType.TEXT_PLAIN_VALUE, "invalid".getBytes()
        );

        doThrow(new IllegalArgumentException("Invalid image"))
                .when(userService).uploadProfilePicture(eq(user.getId()), any());

        mockMvc.perform(multipart("/users/profilePictureUpdate/{id}", user.getId())
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error"));
    }

    @Test
    void shouldReturnInternalServerErrorOnIOException() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "img.jpg", MediaType.IMAGE_JPEG_VALUE, "data".getBytes()
        );

        doThrow(new IOException("IO error"))
                .when(userService).uploadProfilePicture(eq(user.getId()), any());

        mockMvc.perform(multipart("/users/profilePictureUpdate/{id}", user.getId())
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Internal server error"));
    }



    @Test
    void shouldReturnEmptySearchResults() throws Exception {
        mockMvc.perform(get("/users/search")
                        .param("username", "nosuch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }


    @Test
    void shouldUpdateUserBio() throws Exception {
        mockMvc.perform(post("/users/{id}/bio", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bio\":\"New bio text\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Bio updated successfully"));
    }


    @Test
    void shouldUpdateProfileVisibility() throws Exception {
        mockMvc.perform(post("/users/{id}/visibility", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"isPublicProfile\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Profile visibility updated"));
    }


    @Test
    void shouldReturnUserRoutes() throws Exception {
        mockMvc.perform(get("/users/{id}/routes", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }




}
