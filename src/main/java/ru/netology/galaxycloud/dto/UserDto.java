package ru.netology.galaxycloud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    @Email
    @NotNull(message = "Invalid login: not Null")
    @NotEmpty(message = "Invalid login: not Empty")
    @NotBlank(message = "Invalid login: Must not contain only spaces")
    @Size(min = 3, max = 30, message = "Invalid login: Must be min 3 characters")
    private String login;

    @NotNull(message = "Invalid password: not Null")
    @NotEmpty(message = "Invalid password: not Empty")
    @NotBlank(message = "Invalid password: Must not contain only spaces")
    @Size(min = 6, max = 10, message = "Invalid login: Must be min 6 characters")
    private String password;
}
