package pl.put.erasmusbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ErasmusBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ErasmusBackendApplication.class, args);
    }

}
