package com.example.application;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;

@Push
@PWA(name = "Campaigner", shortName = "Campaigner")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@Theme(value = "campaigner")
public class AppShellConfig implements AppShellConfigurator {
    // No Methods currently
    // Add when needed: e.g. when using HTML-head (analytics, SEO, favicon), configuring Content Security Policies etc...
}
