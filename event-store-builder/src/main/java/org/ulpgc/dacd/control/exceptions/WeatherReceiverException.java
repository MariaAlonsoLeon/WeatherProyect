package org.ulpgc.dacd.control.exceptions;

public class WeatherReceiverException extends Exception {
    public WeatherReceiverException(String message) {
        super(message);
    }

    public WeatherReceiverException(String message, Throwable cause) {
        super(message, cause);
    }
}
