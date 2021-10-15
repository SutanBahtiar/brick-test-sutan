package brick.scrape.client;

import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static brick.scrape.client.HttpClientConstants.MAX_TOTAL;

@Configuration
public class HttpClientConfiguration {

    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager
                = new PoolingHttpClientConnectionManager();
        poolingHttpClientConnectionManager.setMaxTotal(MAX_TOTAL);
        return poolingHttpClientConnectionManager;
    }

    @Bean
    public CloseableHttpClient httpClient() {
        final BasicCookieStore cookieStore = new BasicCookieStore();

        return HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .setConnectionManager(poolingHttpClientConnectionManager())
                .build();
    }

}
