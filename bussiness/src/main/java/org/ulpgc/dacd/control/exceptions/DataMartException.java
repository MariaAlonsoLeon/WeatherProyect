package org.ulpgc.dacd.control.exceptions;

public class DataMartException extends Exception {
    public DataMartException(String message) {
        super(message);
    }

    public DataMartException(String message, Throwable cause) {
        super(message, cause);
    }
}