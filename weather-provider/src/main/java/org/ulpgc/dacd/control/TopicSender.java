package org.ulpgc.dacd.control;

import org.ulpgc.dacd.model.Weather;

public interface TopicSender {
    void sendMessage(Weather weather);
}
