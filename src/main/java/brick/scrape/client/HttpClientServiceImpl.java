package brick.scrape.client;

import brick.scrape.mapper.ProductMapper;
import brick.scrape.model.Product;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static brick.scrape.client.HttpClientConstants.ACCEPT;
import static brick.scrape.client.HttpClientConstants.ACCEPT_ENCODING;
import static brick.scrape.client.HttpClientConstants.ACCEPT_LANGUAGE;
import static brick.scrape.client.HttpClientConstants.REFERER;
import static brick.scrape.client.HttpClientConstants.USER_AGENT;

@Slf4j
@Service
public class HttpClientServiceImpl implements HttpClientService {

    private final CloseableHttpClient httpClient;
    private final ProductMapper productMapper;

    @Autowired
    public HttpClientServiceImpl(CloseableHttpClient httpClient,
                                 ProductMapper productMapper) {
        this.httpClient = httpClient;
        this.productMapper = productMapper;
    }

    @Override
    public List<Product> getProducts(List<Product> productPageOne,
                                     List<Product> productPageTwo) {
        return productMapper.getProducts(productPageOne, productPageTwo);
    }

    @Async
    public CompletableFuture<List<Product>> getProductsFuture(String url) throws Exception {
        log.info("Parse products of:{}", url);
        final String pageContent = requestGet(url);
        if (null == pageContent)
            throw new Exception("Error when request " + url);

        try {
            final List<Product> products = productMapper.getProducts(pageContent);
            return CompletableFuture.completedFuture(products);
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
        }
        throw new Exception("Error when get content of product " + url);
    }

    public String requestGet(String url) {
        log.info("Request get:{}", url);
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
        } catch (IOException | URISyntaxException e) {
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
