package ru.netology.galaxycloud.service.Impl;

import com.github.dockerjava.api.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.netology.galaxycloud.entities.User;
import ru.netology.galaxycloud.repository.UserRepository;
import ru.netology.galaxycloud.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public User updateUser(User user, Long id) {
        User userFound = getUserById(id);
        user.setId(userFound.getId());
        return userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long id) {
        getUserById(id);
        userRepository.deleteById(id);
    }

    @Override
    public User findUserByLogin(String login) {
        return userRepository.findUserByLogin(login)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
