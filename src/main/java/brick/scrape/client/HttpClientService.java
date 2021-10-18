package brick.scrape.client;

import brick.scrape.model.Product;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface HttpClientService {

    List<Product> getProducts(List<Product> productPageOne,
                              List<Product> productPageTwo);

    CompletableFuture<List<Product>> getProductsFuture(String url) throws Exception;

}
