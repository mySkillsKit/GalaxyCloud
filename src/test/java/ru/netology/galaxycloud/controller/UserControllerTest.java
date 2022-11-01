package ru.netology.galaxycloud.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.netology.galaxycloud.dto.UserDto;
import ru.netology.galaxycloud.service.UserService;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String AUTH_TOKEN = "auth-token";
    private static final String VALUE_TOKEN = "Bearer ***anyToken***";
    private static final String LOGIN = "test@test.com";
    private static final String PASSWORD = "test1234";

    MockMvc mockMvc;
    ObjectMapper objectMapper;
    UserService userService;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(
                new UserController(userService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateUser_thenSuccess() throws Exception {
        UserDto userDto = UserDto.builder()
                .login(LOGIN).password(PASSWORD).build();

        Mockito.when(userService.createUser(userDto)).thenReturn(userDto);

        mockMvc.perform(post("/users/create")
                        .header(AUTH_TOKEN, VALUE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
    }

    @Test
    void testUpdateUser_thenSuccess() throws Exception {
        UserDto userDto = UserDto.builder()
                .login(LOGIN).password(PASSWORD).build();
        UserDto updateUserDto = UserDto.builder()
                .login(LOGIN).password("newPassword12").build();
        Mockito.when(userService.updateUser(userDto, 2L)).thenReturn(updateUserDto);

        mockMvc.perform(put("/users/update/{id}", "2")
                        .header(AUTH_TOKEN, VALUE_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(updateUserDto)));
    }

    @Test
    void testGetUserById_thenSuccess() throws Exception {
        UserDto userDto = UserDto.builder()
                .login(LOGIN).password(PASSWORD).build();
        Mockito.when(userService.getUserById(2L)).thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", "2")
                        .header(AUTH_TOKEN, VALUE_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
    }

    @Test
    void testDeleteUserById_thenSuccess() throws Exception {
        mockMvc.perform(delete("/users/delete/{id}", "2")
                        .header(AUTH_TOKEN, VALUE_TOKEN))
                .andExpect(status().isOk());
    }
}
