package ru.netology.galaxycloud.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import ru.netology.galaxycloud.dto.UserDto;
import ru.netology.galaxycloud.entities.User;
import ru.netology.galaxycloud.exception.InvalidInputData;
import ru.netology.galaxycloud.exception.UserNotFoundException;
import ru.netology.galaxycloud.model.Login;
import ru.netology.galaxycloud.repository.UserRepository;
import ru.netology.galaxycloud.security.JwtProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public Login login(@NonNull UserDto userDto) {
        log.info("Find user in database by login: {}", userDto.getLogin());
        User userFromDatabase = findUserInStorage(userDto.getLogin());
        if (isEquals(userDto, userFromDatabase)) {
            String accessToken = jwtProvider.generateAccessToken(userFromDatabase);
            return new Login(accessToken);
        } else {
            throw new InvalidInputData("Wrong password", 0);
        }
    }

    private boolean isEquals(UserDto userDto, User userFromDatabase) {
        return passwordEncoder.matches(userDto.getPassword(), userFromDatabase.getPassword());
    }

    public String logout(String authToken, HttpServletRequest request,
                         HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currUser = findUserInStorage(auth.getName());
        SecurityContextLogoutHandler securityContextLogoutHandler =
                new SecurityContextLogoutHandler();
        if (currUser != null) {
            securityContextLogoutHandler.logout(request, response, auth);
            jwtProvider.addAuthTokenInBlackList(authToken);
            log.info("AuthToken add in blacklist : {}", authToken);
            return currUser.getLogin();
        }
        return null;
    }

    private User findUserInStorage(String login) {
        return userRepository.findUserByLogin(login).orElseThrow(() ->
                new UserNotFoundException("User not found by login", 0));
    }
}