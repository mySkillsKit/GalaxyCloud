package ru.netology.galaxycloud.service.Impl;

import com.github.dockerjava.api.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.netology.galaxycloud.dto.UserDto;
import ru.netology.galaxycloud.entities.User;
import ru.netology.galaxycloud.entities.UserRole;
import ru.netology.galaxycloud.mapper.UserMapper;
import ru.netology.galaxycloud.repository.UserRepository;
import ru.netology.galaxycloud.service.UserService;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        log.info("Mapped user request: {}", user);
        userRepository.findUserByLogin(user.getLogin())
                .ifPresent(s -> {
                    throw new RuntimeException("This User already created:" + user);
                });
        user.setCreated(LocalDateTime.now());
        user.setRole(UserRole.ROLE_USER);
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
            userFound.setLogin(user.getLogin());
            userFound.setPassword(user.getPassword());
            userFound.setUpdated(LocalDateTime.now());
            return userFound;
        }).orElseThrow(() -> new NotFoundException("User not found by userId:" + id));
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
                        new NotFoundException("User not found by login:" + login));
        log.info("Found user by Login: {} --> {}", login, userFound);
        return userMapper.userToUserDto(userFound);
    }

    private User getUserFromStorage(Long id) {
        log.info("Search user in the Storage by ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("User not found by userId:" + id));
    }

}
