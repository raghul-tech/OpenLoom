package com.raghultech.openloom.exception;

@SuppressWarnings("serial")
public class OpenLoomFileException extends RuntimeException {
    public  OpenLoomFileException(String message) {
        super(message);
    }

    public  OpenLoomFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
