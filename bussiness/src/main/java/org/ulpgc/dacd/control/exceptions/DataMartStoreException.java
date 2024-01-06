package org.ulpgc.dacd.control.exceptions;

public class DataMartStoreException extends Exception {
    public DataMartStoreException(String message) {
        super(message);
    }

    public DataMartStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}