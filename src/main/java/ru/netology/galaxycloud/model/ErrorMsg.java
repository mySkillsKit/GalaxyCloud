package ru.netology.galaxycloud.model;

import lombok.Data;

@Data
public class ErrorMsg {
    private final String message;
    private final long id;
}
