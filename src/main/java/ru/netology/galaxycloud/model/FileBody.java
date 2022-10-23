package ru.netology.galaxycloud.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileBody {

    @NotNull(message = "Invalid name: not Null")
    @NotEmpty(message = "Invalid name: not Empty")
    @NotBlank(message = "Invalid name: Must not contain only spaces")
    @Pattern(regexp = "\\w", message = "Invalid name: [0-9A-Za-z_]")
    private String name;

}
