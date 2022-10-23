package ru.netology.galaxycloud.exception;

import com.github.dockerjava.api.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {

    private final long id;

    public UserNotFoundException(String msg, long id) {
        super(msg);
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
