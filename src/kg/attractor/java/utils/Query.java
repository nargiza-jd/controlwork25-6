package kg.attractor.java.utils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Query {

    public static Map<String, String> parseQuery(String query) {
        if (query == null || query.isEmpty()) {
            return Collections.emptyMap();
        }
        return Arrays.stream(query.split("&"))
                .map(Query::decodePair)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(
                        pair -> pair[0],
                        pair -> pair[1],
                        (oldValue, newValue) -> newValue
                ));
    }


    private static Optional<String[]> decodePair(String pair) {
        int idx = pair.indexOf("=");
        if (idx == -1) {

            return Optional.of(new String[]{decode(pair), ""});
        } else {
            String key = decode(pair.substring(0, idx));
            String value = decode(pair.substring(idx + 1));
            return Optional.of(new String[]{key, value});
        }
    }

    private static String decode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }


    public static Optional<String> getParam(String query, String paramName) {
        return Optional.ofNullable(parseQuery(query).get(paramName));
    }
}