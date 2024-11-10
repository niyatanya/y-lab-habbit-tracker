package org.home;

import org.home.logging.annotations.EnableExecutionTimeLogging;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for starting the Spring web application.
 */
@SpringBootApplication
@EnableExecutionTimeLogging
public class Application {

    /**
     * Entry point of the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
