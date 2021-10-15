package brick.scrape.controller;

import brick.scrape.client.HttpClientService;
import brick.scrape.client.HttpClientServiceImpl;
import brick.scrape.csv.CSVService;
import brick.scrape.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class ProductController {

    private final HttpClientService clientService;
    private final CSVService csvService;

    @Autowired
    public ProductController(HttpClientServiceImpl clientService,
                             CSVService csvService) {
        this.clientService = clientService;
        this.csvService = csvService;
    }

    @GetMapping("/download-product-csv")
    public ResponseEntity<Resource> getCsv() {
        log.info("Downloading the csv");
        final List<Product> products = getProducts();
        log.info("Product size:{}", products.size());
        final InputStreamResource inputStreamResource = new InputStreamResource(csvService.getCsv(products));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; top-products.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(inputStreamResource);
    }

    private List<Product> getProducts() {
        return clientService.getProducts();
    }
}
