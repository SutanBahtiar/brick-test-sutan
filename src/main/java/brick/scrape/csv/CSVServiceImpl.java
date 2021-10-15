package brick.scrape.csv;

import brick.scrape.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class CSVServiceImpl implements CSVService {

    @Override
    public ByteArrayInputStream getCsv(List<Product> products) {
        log.info("Writing data to csv printer");

        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             final CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(outputStream), CSVConfiguration.FORMAT)) {
            for (Product product : products) {
                final List<String> data = Arrays.asList(product.getProductName(),
                        product.getDescription(),
                        product.getImageLink(),
                        product.getPrice(),
                        product.getRating(),
                        product.getStoreName());
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}
