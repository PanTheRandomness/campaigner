package com.example.application.error;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.server.ErrorEvent;
import com.vaadin.flow.server.ErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CustomErrorHandler implements ErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorHandler.class);

    //TODO: Figure out why errors won't route to /error

    @Override
    public void error(ErrorEvent errorEvent) {
        logger.error("Something went wrong!", errorEvent.getThrowable());
        if(UI.getCurrent() != null) {
            UI.getCurrent().access(() -> {
                Notification.show("An internal error occurred.");
            });
            UI.getCurrent().navigate("/error");
        }
    }
}
