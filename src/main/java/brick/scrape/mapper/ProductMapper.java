package brick.scrape.mapper;

import brick.scrape.model.AceSearchProduct;
import brick.scrape.model.AceShop;
import brick.scrape.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class ProductMapper {

    private static final String PATTERN_DATA_PRODUCT = "ROOT_QUERY.searchProduct.*AceSearchProduct.*AceSearchProductData\"}.(.+?)Data";
    private static final String PATTERN_PRODUCT = "AceSearchProduct\\d+\":(.+?)(AceSearchProduct\"})";
    private static final String PATTERN_SHOP = "(AceShop\\d+)\":(.+?),\"AceSearchProduct";

    private static final Pattern patternDataProduct = Pattern.compile(PATTERN_DATA_PRODUCT);
    private static final Pattern patternProduct = Pattern.compile(PATTERN_PRODUCT);
    private static final Pattern patternShop = Pattern.compile(PATTERN_SHOP);

    private final ObjectMapper objectMapper;

    @Autowired
    public ProductMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String getContent(String content) {
        final Matcher matcher = patternDataProduct.matcher(content);
        if (matcher.find()) return matcher.group(1);
        return null;
    }

    public List<AceSearchProduct> getProductsContent(String content) throws JsonProcessingException {
        final List<AceSearchProduct> list = new ArrayList<>();
        final Matcher matcher = patternProduct.matcher(content);
        while (matcher.find()) {
            final String product = matcher.group(1) + matcher.group(2);
            final AceSearchProduct aceSearchProduct = objectMapper
                    .readValue(product, AceSearchProduct.class);
            list.add(aceSearchProduct);
        }
        return list;
    }

    public Map<String, AceShop> getShopsContent(String content) throws JsonProcessingException {
        final Map<String, AceShop> shopMap = new HashMap<>();
        final Matcher matcher = patternShop.matcher(content);
        while (matcher.find()) {
            final String key = matcher.group(1);
            final AceShop aceShop = objectMapper.readValue(matcher.group(2), AceShop.class);
            shopMap.put(key, aceShop);
        }
        return shopMap;
    }

    public List<Product> getProducts(List<AceSearchProduct> productsContent,
                                     Map<String, AceShop> shopsContent) {
        final List<Product> products = new ArrayList<>();
        for (AceSearchProduct productContent : productsContent) {
            final Product product = new Product();
            product.setProductName(productContent.getName());
            product.setImageLink(productContent.getImageUrl());
            product.setPrice(productContent.getPrice());
            product.setRating(productContent.getRating());
            product.setStoreName(shopsContent.get(productContent.getShop().getId()).getName());
            products.add(product);
        }
        return products;
    }

    public List<Product> getProducts(List<Product> productPageOne,
                                     List<Product> productPageTwo) {
        final List<Product> products = new ArrayList<>();
        products.addAll(productPageOne);
        
        /*
         in page one and page two have 60 product
         we just need 100 products, so we just take 40 products on page two
         */
        int idx = 0;
        for (Product product : productPageTwo) {
            if (idx >= 40) break;
            ++idx;
            products.add(product);
        }
        return products;
    }

    public List<Product> getProducts(String pageContent) throws Exception {
        try {
            // get content page
            final String content = getContent(pageContent);

            // get product
            final List<AceSearchProduct> productsContent = getProductsContent(content);

            // get shop
            final Map<String, AceShop> shopsContent = getShopsContent(content);

            // mapping the product
            return getProducts(productsContent, shopsContent);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }

        throw new Exception("Error when parse the content");
    }
}
