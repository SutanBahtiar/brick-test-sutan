package brick.scrape;

import brick.scrape.async.AsyncConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        AsyncConfigurationProperties.class
})
public class ScrapeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScrapeApplication.class, args);
    }

}
