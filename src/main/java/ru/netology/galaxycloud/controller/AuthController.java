package ru.netology.galaxycloud.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.galaxycloud.dto.UserDto;
import ru.netology.galaxycloud.model.Login;
import ru.netology.galaxycloud.service.AuthService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Tag(name = "Authorization", description = "Authorization User")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Authorization")
    @ApiResponse(responseCode = "200", description = "Success authorization",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Login.class))})
    @ApiResponse(responseCode = "400", description = "Error input data",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @ApiResponse(responseCode = "403", description = "Unauthorized error",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @ApiResponse(responseCode = "404", description = "Login not found",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @ApiResponse(responseCode = "500", description = "Error authorization",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @PostMapping("/login")
    public ResponseEntity<Login> login(@RequestBody UserDto userDto) {
        log.info("User try login : {}", userDto);
        Login token = authService.login(userDto);
        log.info("Success login user: {} auth-token {}", userDto, token.getAuthToken());
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "Logout")
    @ApiResponse(responseCode = "200", description = "Success logout")
    @ApiResponse(responseCode = "400", description = "Error input data",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE)})
    @SecurityRequirement(name = "Bearer-token-auth")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@NotNull @RequestHeader(value = "auth-token") String authToken,
                                       HttpServletRequest request, HttpServletResponse response) {
        String logoutLoginUser = authService.logout(authToken, request, response);
        if (logoutLoginUser != null) {
            log.info("Success logout user login: {} auth-token: {} ", logoutLoginUser, authToken);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}