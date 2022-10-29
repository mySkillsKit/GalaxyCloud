package ru.netology.galaxycloud.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Login {
   @JsonProperty("auth-token")
   private String authToken;
}
