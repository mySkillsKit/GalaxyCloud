package ru.netology.galaxycloud.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.netology.galaxycloud.dto.UserDto;
import ru.netology.galaxycloud.entities.User;
import ru.netology.galaxycloud.entities.UserRole;
import ru.netology.galaxycloud.exception.InvalidInputData;
import ru.netology.galaxycloud.exception.UserNotFoundException;
import ru.netology.galaxycloud.mapper.UserMapper;
import ru.netology.galaxycloud.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private UserMapper userMapper;

    private User user;
    private UserDto userDto;

    @BeforeEach
    public void init() {
        userDto = UserDto.builder()
                .login("test@test.com")
                .password("test1234")
                .build();
        user = User.builder()
                .id(1L)
                .login("test@test.com")
                .password("test1234")
                .roles(Collections.singleton(UserRole.ROLE_USER))
                .build();
    }

    @Test
    public void createUser_thenSuccess() {
        Mockito.when(userMapper.userDtoToUser(userDto)).thenReturn(user);
        Mockito.when(userRepository.findUserByLogin(user.getLogin())).thenReturn(Optional.empty());

        userService.createUser(userDto);

        Mockito.verify(userRepository,
                Mockito.times(1)).findUserByLogin("test@test.com");
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    public void getUserById_thenSuccess() {
        //given
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        //when
        userService.getUserById(1L);
        //then
        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    public void updateUser_thenSuccess() {
        Mockito.when(userMapper.userDtoToUser(userDto)).thenReturn(user);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));

        userService.updateUser(userDto, 1L);

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    public void deleteUserById_thenSuccess() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));

        userService.deleteUserById(1L);

        Mockito.verify(userRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(userRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void findUserByLogin_thenSuccess() {
        Mockito.when(userRepository
                .findUserByLogin("test@test.com")).thenReturn(Optional.ofNullable(user));

        userService.findUserByLogin("test@test.com");

        Mockito.verify(userRepository,
                Mockito.times(1)).findUserByLogin("test@test.com");
    }

    @Test
    public void createUserWhenUserInStorage_thenError() {
        Mockito.when(userMapper.userDtoToUser(userDto)).thenReturn(user);
        Mockito.when(userRepository.findUserByLogin(user.getLogin())).thenReturn(Optional.ofNullable(user));

        Exception exception = assertThrows(InvalidInputData.class, () -> {
            userService.createUser(userDto);
        });

        String expectedMessage = "This User already exists in Storage";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void deleteUserByIdNotFoundInStorage_thenError() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUserById(1L);
        });

        String expectedMessage = "User not found by userID";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }
}