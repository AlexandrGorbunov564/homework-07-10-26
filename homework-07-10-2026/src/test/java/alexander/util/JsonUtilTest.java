package alexander.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonUtilTest {

    @Test
    void parseValidRate() {
        assertEquals(13.5, JsonUtil.parseRate("{\"rate\":13.5}").doubleValue());
    }

    @Test
    void parseNegativeRate() {
        assertEquals(-5.0, JsonUtil.parseRate("{\"rate\":-5}").doubleValue());
    }

    @Test
    void parseWrongFieldThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> JsonUtil.parseRate("{\"percent\":13}"));
    }

    @Test
    void parseStringInsteadOfNumberThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> JsonUtil.parseRate("{\"rate\":\"13\"}"));
    }

    @Test
    void convertRateToJson() {
        assertEquals("{\"rate\":15.0}", JsonUtil.convertRateToJson(15.0));
    }
}
