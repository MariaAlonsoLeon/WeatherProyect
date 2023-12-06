package org.ulpgc.dacd.control.exceptions;

public class WeatherDataException extends Exception {
    public WeatherDataException() {
        super();
    }

    public WeatherDataException(String message) {
        super(message);
    }

    public WeatherDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public WeatherDataException(Throwable cause) {
        super(cause);
    }
}
