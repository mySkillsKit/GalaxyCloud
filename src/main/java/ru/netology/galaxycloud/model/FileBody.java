package ru.netology.galaxycloud.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("filename")
    @NotNull(message = "Invalid name: not Null")
    @NotEmpty(message = "Invalid name: not Empty")
    @NotBlank(message = "Invalid name: Must not contain only spaces")
    private String name;
}