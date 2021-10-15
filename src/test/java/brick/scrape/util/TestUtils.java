package brick.scrape.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestUtils {

    public static String getContent(String fileName) throws URISyntaxException, IOException {
        final Path path = Paths.get(Objects.requireNonNull(TestUtils.class.getClassLoader()
                .getResource(fileName)).toURI());

        final Stream<String> lines = Files.lines(path);
        final String content = lines.collect(Collectors.joining("\n"));
        lines.close();
        return content;
    }
}
