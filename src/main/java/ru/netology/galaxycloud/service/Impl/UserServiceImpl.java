package ru.netology.galaxycloud.service.Impl;

import com.github.dockerjava.api.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.netology.galaxycloud.dto.UserDto;
import ru.netology.galaxycloud.entities.User;
import ru.netology.galaxycloud.entities.UserRole;
import ru.netology.galaxycloud.mapper.UserMapper;
import ru.netology.galaxycloud.repository.UserRepository;
import ru.netology.galaxycloud.service.UserService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.userDtoToUser(userDto);
        userRepository.findUserByLogin(user.getLogin())
                .orElseThrow(() -> new NotFoundException("This User already created"));
        user.setCreated(LocalDateTime.now());
        user.setRole(UserRole.ROLE_USER);
        return userMapper.userToUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getUserById(Long id) {
        User userFound = getUserFromStorage(id);
        return userMapper.userToUserDto(userFound);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id) {
        User user = userMapper.userDtoToUser(userDto);
        User updateUser = userRepository.findById(id).map(userFound -> {
            userFound.setLogin(user.getLogin());
            userFound.setPassword(user.getPassword());
            userFound.setUpdated(LocalDateTime.now());
            return userFound;
        }).orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.userToUserDto(userRepository.save(updateUser));
    }

    @Override
    public void deleteUserById(Long id) {
        getUserFromStorage(id);
        //TODO delete file
        userRepository.deleteById(id);
    }

    @Override
    public UserDto findUserByLogin(String login) {
        User userFound = userRepository.findUserByLogin(login)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.userToUserDto(userFound);
    }

    private User getUserFromStorage(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

}
