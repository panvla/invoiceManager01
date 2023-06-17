package com.vladimirpandurov.invoiceManager01B.service;

import com.vladimirpandurov.invoiceManager01B.domain.UserEvent;
import com.vladimirpandurov.invoiceManager01B.enumeration.EventType;

import java.util.Collection;

public interface EventService {
    Collection<UserEvent> getEventsByUserId(Long userId);
    void addUserEvent(String email, EventType eventType, String device, String ipAddress);
    void addUserEvent(Long userId, EventType eventType, String device, String ipAddress);
}
