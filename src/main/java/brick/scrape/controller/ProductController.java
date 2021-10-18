package brick.scrape.controller;

import brick.scrape.client.HttpClientService;
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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static brick.scrape.client.HttpClientConstants.PAGE_ONE_URL;
import static brick.scrape.client.HttpClientConstants.PAGE_TWO_URL;

@Slf4j
@RestController
public class ProductController {

    private final HttpClientService clientService;
    private final CSVService csvService;

    @Autowired
    public ProductController(HttpClientService clientService,
                             CSVService csvService) {
        this.clientService = clientService;
        this.csvService = csvService;
    }

    @GetMapping("/download-product-csv")
    public ResponseEntity<Resource> getCsv() {
        log.info("Downloading the csv");
        final List<Product> products = getProducts();
        final InputStreamResource inputStreamResource = new InputStreamResource(csvService.getCsv(products));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; top-products.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(inputStreamResource);
    }

    private List<Product> getProducts() {
        try {
            final CompletableFuture<List<Product>> productPageOne = clientService.getProductsFuture(PAGE_ONE_URL);
            final CompletableFuture<List<Product>> productPageTwo = clientService.getProductsFuture(PAGE_TWO_URL);
            CompletableFuture.allOf(productPageOne, productPageTwo).join();

            return clientService.getProducts(productPageOne.get(), productPageTwo.get());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Collections.emptyList();
    }
}
