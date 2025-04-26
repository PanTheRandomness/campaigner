package com.example.application.views.errors;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.access.AccessDeniedException;

@PermitAll
@PageTitle("Error")
@Route("error")
public class CustomErrorView extends VerticalLayout implements HasErrorParameter<Exception> {

    public CustomErrorView() {
        setSpacing(true);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<Exception> parameter) {
        String errorMessage = getTranslation("error.occurred");
        int statusCode = 500;

        if (parameter.getException() instanceof AccessDeniedException) {
            errorMessage = getTranslation("error.denied");
            statusCode = 403;
        } else if (parameter.getException() instanceof NotFoundException) {
            errorMessage = getTranslation("error.notfound");
            statusCode = 404;
        }

        removeAll();
        add(new H1(getTranslation("error") + " " + statusCode));
        add(new Paragraph(errorMessage));

        return statusCode;
    }
}
