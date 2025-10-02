package service.sllbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import service.sllbackend.dev.DataLoader;

@SpringBootApplication
public class SllBackendApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SllBackendApplication.class, args);
        DataLoader dataLoader = context.getBean(DataLoader.class);
        dataLoader.run();
    }

}
