package com.example.quanlytoanhanhom4.app;

import javafx.application.Application;

public final class ApplicationLauncher {

    private ApplicationLauncher() {
        // Entry point holder
    }

    public static void main(String[] args) {
        Application.launch(BuildingManagementApplication.class, args);
    }
}


