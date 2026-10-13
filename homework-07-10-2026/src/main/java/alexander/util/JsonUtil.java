package alexander.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtil {
    private static final Pattern PATTERN = Pattern.compile(
            "\\s*\\{\\s*\"rate\"\\s*:\\s*(-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?)\\s*\\}\\s*");

    public static Double parseRate(String json) {
        if (json == null) {
            throw new IllegalArgumentException("Пустой JSON");
        }
        Matcher matcher = PATTERN.matcher(json);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Некорректный формат JSON");
        }

        Double rate = Double.parseDouble(matcher.group(1));
        if (!Double.isFinite(rate)) {
            throw new IllegalArgumentException("Некорректное число");
        }
        return rate;
    }

    public static String convertRateToJson(Double rate) {
        if (rate == null || !Double.isFinite(rate)) {
            throw new IllegalArgumentException("Некорректное число");
        }
        return "{\"rate\":" + rate + "}";
    }

    public static String errorToJson() {
        return "{\"error\":\"Некорректное число\"}";
    }
}