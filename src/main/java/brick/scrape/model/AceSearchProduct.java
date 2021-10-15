package brick.scrape.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AceSearchProduct {

    @JsonProperty("image_url")
    private String imageUrl;
    private String name;
    private String price;
    private String rating;
    private Shop shop;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Shop {
        private String id;
        @JsonProperty("typename")
        private String typeName;
    }

}
