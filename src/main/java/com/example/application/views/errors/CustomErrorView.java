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
        String errorMessage = "An error occurred";
        int statusCode = 500;

        if (parameter.getException() instanceof AccessDeniedException) {
            errorMessage = "Access denied: You do not have permission to access this resource.";
            statusCode = 403;
        } else if (parameter.getException() instanceof NotFoundException) {
            errorMessage = "Page Not Found: The resource could not be located.";
            statusCode = 404;
        }

        removeAll();
        add(new H1("Error " + statusCode));
        add(new Paragraph(errorMessage));

        return statusCode;
    }
}
