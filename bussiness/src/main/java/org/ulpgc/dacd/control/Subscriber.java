package org.ulpgc.dacd.control;

import org.ulpgc.dacd.control.exceptions.EventReceiverException;

public interface Subscriber {
    void start() throws EventReceiverException;
}