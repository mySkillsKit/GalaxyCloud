package ru.netology.galaxycloud.exception;

import com.github.dockerjava.api.exception.NotFoundException;

public class FileNotFoundException extends NotFoundException {

    private final long id;

    public FileNotFoundException(String msg, long id) {
        super(msg);
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
