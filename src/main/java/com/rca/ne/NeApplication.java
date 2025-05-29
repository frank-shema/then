package com.rca.ne;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@SpringBootApplication
public class NeApplication {

    public static void main(String[] args) {
        SpringApplication.run(NeApplication.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner(Environment environment) {
        return args -> {
            String port = environment.getProperty("server.port", "8080");
            String contextPath = environment.getProperty("server.servlet.context-path", "");
            String host = environment.getProperty("server.address", "localhost");

            System.out.println("\n----------------------------------------------------------");
            System.out.println("  Rwanda Government ERP System is running!");
            System.out.println("----------------------------------------------------------");
            System.out.println("  Access URLs:");
            System.out.println("  Local:      http://localhost:" + port + contextPath);
            System.out.println("  External:   http://" + host + ":" + port + contextPath);
            System.out.println("  Swagger UI: http://localhost:" + port + contextPath + "/swagger-ui.html");
            System.out.println("  API Docs:   http://localhost:" + port + contextPath + "/v3/api-docs");
            System.out.println("----------------------------------------------------------");

            // Print active profiles
            String[] profiles = environment.getActiveProfiles();
            if (profiles.length > 0) {
                System.out.println("  Active profiles: " + Arrays.toString(profiles));
                System.out.println("----------------------------------------------------------");
            }
        };
    }
}
