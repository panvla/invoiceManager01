package com.vladimirpandurov.invoiceManager01B.event;

import com.vladimirpandurov.invoiceManager01B.enumeration.EventType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Getter
@Setter
public class NewUserEvent extends ApplicationEvent {

    private EventType type;
    private String email;

    public NewUserEvent(EventType type, String email) {
        super(email);
        this.type = type;
        this.email = email;
    }


}
