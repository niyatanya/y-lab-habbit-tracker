package org.home;

import org.home.logging.annotations.EnableExecutionTimeLogging;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableExecutionTimeLogging
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
