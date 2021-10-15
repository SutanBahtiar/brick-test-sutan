package brick.scrape.client;

import brick.scrape.mapper.ProductMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        HttpClientConfiguration.class,
        JsonMapper.class,
        ProductMapper.class,
        HttpClientServiceImpl.class
})
public class HttpClientServiceIntegrationTest {

    @Autowired
    private HttpClientService httpClient;

    //        @Test
    public void testGetProducts() {
        System.out.println(httpClient.getProducts());
    }

}