package epam.com.khshanovskyi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * This is entry point for starting application.
 */
@SpringBootApplication
@EnableCaching
@EnableScheduling
public class CryptoRecommendationsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoRecommendationsServiceApplication.class, args);
    }

}
