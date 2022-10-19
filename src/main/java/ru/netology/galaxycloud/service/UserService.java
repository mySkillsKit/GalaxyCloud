package ru.netology.galaxycloud.service;

import ru.netology.galaxycloud.entities.User;

public interface UserService {

    User createUser(User user);

    User getUserById(Long id);

    User updateUser(User user, Long id);

    void deleteUserById(Long id);

    User findUserByLogin(String login);

}
