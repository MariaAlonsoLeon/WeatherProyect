package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.exceptions.EventReceiverException;

public interface Suscriber {
    void start() throws EventReceiverException;
}
