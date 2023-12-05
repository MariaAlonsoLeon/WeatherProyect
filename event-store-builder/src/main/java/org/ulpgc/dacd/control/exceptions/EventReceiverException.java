package org.ulpgc.dacd.control.exceptions;

public class EventReceiverException extends Exception {
    public EventReceiverException(String message) {
        super(message);
    }

    public EventReceiverException(String message, Throwable cause) {
        super(message, cause);
    }
}
