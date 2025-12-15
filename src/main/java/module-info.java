module com.example.quanlytoanhanhom4 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // Logging - SLF4J API
    requires org.slf4j;

    // Logging - Logback implementation
    requires ch.qos.logback.classic;
    requires ch.qos.logback.core;

    // Connection Pooling - HikariCP
    requires com.zaxxer.hikari;

    // BCrypt - jbcrypt is not a module, it will be an automatic module
    // The module name is derived from the JAR name: jbcrypt-0.4.jar -> jbcrypt
    requires jbcrypt;

    // Flyway for database migrations
    // Automatic module names: flyway-core-10.7.1.jar -> flyway.core
    // flyway-mysql-10.7.1.jar -> flyway.mysql
    requires flyway.core;
    requires flyway.mysql;
    requires java.desktop;

    opens com.example.quanlytoanhanhom4.controller to javafx.fxml;
    opens com.example.quanlytoanhanhom4.controller.auth to javafx.fxml;
    opens com.example.quanlytoanhanhom4.ui to javafx.fxml;
    opens com.example.quanlytoanhanhom4.ui.auth to javafx.fxml;
    // Open config package for Flyway reflection access
    opens com.example.quanlytoanhanhom4.config;
    // Note: Resources (migration files) are accessed via classpath, not as a Java package

    exports com.example.quanlytoanhanhom4.app;
    exports com.example.quanlytoanhanhom4.config;
    exports com.example.quanlytoanhanhom4.controller;
    exports com.example.quanlytoanhanhom4.controller.auth;
    exports com.example.quanlytoanhanhom4.model;
    exports com.example.quanlytoanhanhom4.service;
    exports com.example.quanlytoanhanhom4.service.auth;
    exports com.example.quanlytoanhanhom4.ui;
    exports com.example.quanlytoanhanhom4.ui.auth;
    exports com.example.quanlytoanhanhom4.util;
}