package ru.netology.galaxycloud.exception;

public class InvalidInputData extends RuntimeException {

    private final long id;

    public InvalidInputData(String msg, long id) {
        super(msg);
        this.id = id;
    }

    public long getId() {
        return id;
    }

}
