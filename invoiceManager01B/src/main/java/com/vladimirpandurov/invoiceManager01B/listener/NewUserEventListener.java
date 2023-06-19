package com.vladimirpandurov.invoiceManager01B.listener;

import com.vladimirpandurov.invoiceManager01B.event.NewUserEvent;
import com.vladimirpandurov.invoiceManager01B.service.EventService;
import com.vladimirpandurov.invoiceManager01B.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewUserEventListener {
    private final EventService eventService;
    private final HttpServletRequest request;

    @EventListener
    public void onNewUserEvent(NewUserEvent event) {
        log.info("NewUserEvent is fired");
        this.eventService.addUserEvent(event.getEmail(), event.getType(), RequestUtils.getDevice(request), RequestUtils.getIpAddress(request));
    }
}
