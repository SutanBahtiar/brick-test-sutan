package brick.scrape.client;

import brick.scrape.async.AsyncConfiguration;
import brick.scrape.async.AsyncConfigurationProperties;
import brick.scrape.mapper.ProductMapper;
import brick.scrape.model.Product;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static brick.scrape.client.HttpClientConstants.PAGE_ONE_URL;
import static brick.scrape.client.HttpClientConstants.PAGE_TWO_URL;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        AsyncConfiguration.class,
        HttpClientConfiguration.class,
        JsonMapper.class,
        ProductMapper.class,
        HttpClientServiceImpl.class
})
@EnableConfigurationProperties({
        AsyncConfigurationProperties.class
})
@TestPropertySource("classpath:application.properties")
public class HttpClientServiceIntegrationTest {

    @Autowired
    private HttpClientService httpClient;

    @Test
    public void testGetProducts() throws Exception {
        final CompletableFuture<List<Product>> productPageOne = httpClient.getProductsFuture(PAGE_ONE_URL);
        final CompletableFuture<List<Product>> productPageTwo = httpClient.getProductsFuture(PAGE_TWO_URL);
        CompletableFuture.allOf(productPageOne, productPageTwo).join();

        final List<Product> products = httpClient.getProducts(productPageOne.get(), productPageTwo.get());
        products.forEach(System.out::println);
    }

}