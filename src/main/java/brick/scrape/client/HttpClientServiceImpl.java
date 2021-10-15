package brick.scrape.client;

import brick.scrape.mapper.ProductMapper;
import brick.scrape.model.AceSearchProduct;
import brick.scrape.model.AceShop;
import brick.scrape.model.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static brick.scrape.client.HttpClientConstants.ACCEPT;
import static brick.scrape.client.HttpClientConstants.ACCEPT_ENCODING;
import static brick.scrape.client.HttpClientConstants.ACCEPT_LANGUAGE;
import static brick.scrape.client.HttpClientConstants.PAGE_ONE_URL;
import static brick.scrape.client.HttpClientConstants.PAGE_TWO_URL;
import static brick.scrape.client.HttpClientConstants.REFERER;
import static brick.scrape.client.HttpClientConstants.USER_AGENT;

@Slf4j
@Service
public class HttpClientServiceImpl implements HttpClientService {

    private final CloseableHttpClient httpClient;
    private final ProductMapper productMapper;

    @Autowired
    public HttpClientServiceImpl(ProductMapper productMapper,
                                 CloseableHttpClient httpClient) {
        this.productMapper = productMapper;
        this.httpClient = httpClient;
    }

    @Override
    public List<Product> getProducts() {
        final List<Product> productsPageOne = getProducts(getPageOne());
        final List<Product> productsPageTwo = getProducts(getPageTwo());
        return productMapper.getProducts(productsPageOne, productsPageTwo);
    }

    public List<Product> getProducts(String pageContent) {
        try {
            // get content page one
            final String content = productMapper.getContent(pageContent);

            // get product
            final List<AceSearchProduct> productsContent = productMapper.getProductsContent(content);

            // get shop
            final Map<String, AceShop> shopsContent = productMapper.getShopsContent(content);

            // mapping the product
            return productMapper.getProducts(productsContent, shopsContent);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    public String getPageOne() {
        return requestGet(PAGE_ONE_URL);
    }

    public String getPageTwo() {
        return requestGet(PAGE_TWO_URL);
    }

    public String requestGet(String url) {
        try {
            final ClassicHttpRequest httpRequest = ClassicRequestBuilder.get()
                    .setUri(new URI(url))
                    .build();

            httpRequest.addHeader(HttpHeaders.USER_AGENT, USER_AGENT);
            httpRequest.addHeader(HttpHeaders.ACCEPT, ACCEPT);
            httpRequest.addHeader(HttpHeaders.ACCEPT_ENCODING, ACCEPT_ENCODING);
            httpRequest.addHeader(HttpHeaders.ACCEPT_LANGUAGE, ACCEPT_LANGUAGE);
            httpRequest.addHeader(HttpHeaders.REFERER, REFERER);

            return httpClient.execute(httpRequest, responseHandler());
        } catch (URISyntaxException | IOException e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    public HttpClientResponseHandler<String> responseHandler() {
        return response -> {
            final int status = response.getCode();
            if (status >= HttpStatus.SC_SUCCESS && status < HttpStatus.SC_REDIRECTION) {
                final HttpEntity entity = response.getEntity();
                try {
                    return entity != null ? EntityUtils.toString(entity) : null;
                } catch (final ParseException ex) {
                    throw new ClientProtocolException(ex);
                }
            } else {
                throw new ClientProtocolException("Unexpected response status: " + status);
            }
        };
    }
}
