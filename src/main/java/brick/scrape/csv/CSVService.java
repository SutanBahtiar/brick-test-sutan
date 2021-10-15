package brick.scrape.csv;

import brick.scrape.model.Product;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface CSVService {

    ByteArrayInputStream getCsv(List<Product> products);
}
