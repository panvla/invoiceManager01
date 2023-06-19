package com.vladimirpandurov.invoiceManager01B.repository.implementation;

import com.vladimirpandurov.invoiceManager01B.domain.UserEvent;
import com.vladimirpandurov.invoiceManager01B.enumeration.EventType;
import com.vladimirpandurov.invoiceManager01B.repository.EventRepository;
import com.vladimirpandurov.invoiceManager01B.rowmapper.UserEventRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;

import static com.vladimirpandurov.invoiceManager01B.query.EventQuery.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class EventRepositoryImpl implements EventRepository {



    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Collection<UserEvent> getEventsByUserId(Long userId) {
        return jdbc.query(SELECT_EVENTS_BY_USER_ID_QUERY, Map.of("id", userId), new UserEventRowMapper());
    }

    @Override
    public void addUserEvent(String email, EventType eventType, String device, String ipAddress) {
        System.out.println("***" + eventType.toString());
        jdbc.update(INSERT_EVENT_BY_USER_EMAIL_QUERY, Map.of("email", email, "type", eventType.toString(), "device", device, "ipAddress", ipAddress));
    }

    @Override
    public void addUserEvent(Long userId, EventType eventType, String device, String ipAddress) {

    }
}
