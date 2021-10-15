package brick.scrape.client;

import brick.scrape.model.Product;

import java.util.List;

public interface HttpClientService {

    List<Product> getProducts();

}
