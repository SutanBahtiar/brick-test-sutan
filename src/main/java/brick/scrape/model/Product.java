package brick.scrape.model;

import lombok.Data;

@Data
public class Product {

    private String productName;
    private String description;
    private String imageLink;
    private String price;
    private String rating;
    private String storeName;

}
