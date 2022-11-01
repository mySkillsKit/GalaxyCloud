package ru.netology.galaxycloud.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.galaxycloud.dto.UserDto;
import ru.netology.galaxycloud.entities.User;
import ru.netology.galaxycloud.entities.UserRole;
import ru.netology.galaxycloud.exception.InvalidInputData;
import ru.netology.galaxycloud.exception.UserNotFoundException;
import ru.netology.galaxycloud.mapper.UserMapper;
import ru.netology.galaxycloud.repository.UserRepository;
import ru.netology.galaxycloud.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        log.info("Mapped user request: {}", user);
        findUserInStorageByLogin(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreated(LocalDateTime.now());
        user.setRoles(Collections.singleton(UserRole.ROLE_USER));
        log.info("Creating new user: {}", user);
        return userMapper.userToUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getUserById(Long id) {
        User userFound = getUserFromStorage(id);
        log.info("User found by ID: {} : {} in Storage", id, userFound);
        return userMapper.userToUserDto(userFound);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id) {
        User user = userMapper.userDtoToUser(userDto);
        log.info("Mapped user request: {}", user);
        User updateUser = userRepository.findById(id).map(userFound -> {
            userFound.setPassword(passwordEncoder.encode(user.getPassword()));
            userFound.setUpdated(LocalDateTime.now());
            return userFound;
        }).orElseThrow(() -> new UserNotFoundException("User not found by userID", id));
        log.info("Updated user by ID: {} : {} ", id, updateUser);
        return userMapper.userToUserDto(userRepository.save(updateUser));
    }

    @Override
    public void deleteUserById(Long id) {
        log.info("Find user by ID: {}", id);
        getUserFromStorage(id);
        log.info("Deleted user by ID: {}", id);
        userRepository.deleteById(id);
    }

    @Override
    public UserDto findUserByLogin(String login) {
        log.info("Find user by Login: {}", login);
        User userFound = userRepository.findUserByLogin(login)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found by login:" + login, 0));
        log.info("Found user by Login: {} --> {}", login, userFound);
        return userMapper.userToUserDto(userFound);
    }

    private User getUserFromStorage(Long id) {
        log.info("Search user in the Storage by ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found by userID", id));
    }

    private void findUserInStorageByLogin(User user) {
        log.info("Find user by Login: {}", user.getLogin());
        userRepository.findUserByLogin(user.getLogin()).ifPresent(s -> {
            throw new InvalidInputData("This User already exists in Storage." +
                    " Change login and try again:" + user.getLogin(), 0);
        });
    }
}
