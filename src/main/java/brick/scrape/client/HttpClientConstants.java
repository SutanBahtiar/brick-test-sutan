package brick.scrape.client;

public interface HttpClientConstants {

    // max connections
    int MAX_TOTAL = 100;

    String PAGE_ONE_URL = "https://www.tokopedia.com/p/handphone-tablet/handphone?page=1";
    String PAGE_TWO_URL = "https://www.tokopedia.com/p/handphone-tablet/handphone?page=2";
    String REFERER = "https://www.tokopedia.com/p/handphone-tablet";
    String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:93.0) Gecko/20100101 Firefox/93.0";
    String ACCEPT = "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8";
    String ACCEPT_ENCODING = "gzip, deflate, br";
    String ACCEPT_LANGUAGE = "Accept-Language: en-US,en;q=0.5";

}
