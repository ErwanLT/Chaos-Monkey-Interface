package fr.eletutour.chaosmonkey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ChaosMonkeyIHMApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChaosMonkeyIHMApplication.class);
    }
}
