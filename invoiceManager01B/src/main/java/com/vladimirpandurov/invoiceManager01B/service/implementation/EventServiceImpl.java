package com.vladimirpandurov.invoiceManager01B.service.implementation;

import com.vladimirpandurov.invoiceManager01B.domain.UserEvent;
import com.vladimirpandurov.invoiceManager01B.enumeration.EventType;
import com.vladimirpandurov.invoiceManager01B.repository.EventRepository;
import com.vladimirpandurov.invoiceManager01B.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public Collection<UserEvent> getEventsByUserId(Long userId) {
        return this.eventRepository.getEventsByUserId(userId);
    }

    @Override
    public void addUserEvent(String email, EventType eventType, String device, String ipAddress) {

        this.eventRepository.addUserEvent(email, eventType, device, ipAddress);
    }

    @Override
    public void addUserEvent(Long userId, EventType eventType, String device, String ipAddress) {

    }
}
