package ru.netology.galaxycloud.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FileDto {
    @JsonProperty("filename")
    private String fileName;
    private Long size;
    private String hash;
    private byte[] fileByte;
    private String type;
}
