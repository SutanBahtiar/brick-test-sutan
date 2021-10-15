package brick.scrape.csv;

import org.apache.commons.csv.CSVFormat;

public interface CSVConfiguration {

    String[] HEADERS = {
            "Name of Product",
            "Description",
            "Image Link",
            "Price",
            "Rating",
            "Store Name/Merchant"
    };

    CSVFormat FORMAT = CSVFormat.DEFAULT.withHeader();
}
