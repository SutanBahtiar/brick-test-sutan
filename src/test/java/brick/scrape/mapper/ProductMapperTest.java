package brick.scrape.mapper;

import brick.scrape.model.AceSearchProduct;
import brick.scrape.model.AceShop;
import brick.scrape.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static brick.scrape.util.TestUtils.getContent;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {JsonMapper.class})
public class ProductMapperTest {

    private ProductMapper productMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        productMapper = new ProductMapper(objectMapper);
    }

    @Test
    public void testGetProduct() throws URISyntaxException, IOException {
        final List<AceSearchProduct> productsContent = testGetProductsContent();
        final Map<String, AceShop> shopsContent = testGetShopsContent();
        final List<Product> products = productMapper.getProducts(productsContent, shopsContent);
        assertEquals(productsContent.size(), products.size());
        products.forEach(System.out::println);
    }

    public List<AceSearchProduct> testGetProductsContent() throws URISyntaxException, IOException {
        final String dataProduct = testGetDataProduct();
        final List<AceSearchProduct> products = productMapper.getProductsContent(dataProduct);
        assertEquals(60, products.size());

        final AceSearchProduct product = products.get(0);
        assertEquals("AceShop717871", product.getShop().getId());
        assertEquals("5", product.getRating());
        assertEquals("IPhone 12 Pro Garansi Resmi Ibox", product.getName());
        assertEquals("Rp16.099.000", product.getPrice());

        return products;
    }

    public Map<String, AceShop> testGetShopsContent() throws URISyntaxException, IOException {
        final String dataProduct = testGetDataProduct();
        final Map<String, AceShop> shopMap = productMapper.getShopsContent(dataProduct);
        assertEquals(18, shopMap.size());

        final AceShop shop = shopMap.get("AceShop717871");
        assertEquals("Cv.Sentra inti pratama", shop.getName());
        assertEquals("Jakarta Barat", shop.getLocation());

        return shopMap;
    }

    public String testGetDataProduct() throws URISyntaxException, IOException {
        final String content = getContent("page_one.html");
        return productMapper.getContent(content);
    }
}