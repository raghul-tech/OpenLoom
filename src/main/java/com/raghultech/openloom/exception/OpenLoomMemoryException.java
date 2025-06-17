package com.raghultech.openloom.exception;

@SuppressWarnings("serial")
public class OpenLoomMemoryException extends RuntimeException {
    public OpenLoomMemoryException(String message, Throwable cause) {
        super(message, cause);
    }
    public  OpenLoomMemoryException() {
    super("The file is too large to fit in memory. ");	
    }
    public OpenLoomMemoryException(String message) {
        super(message);
    }
}
