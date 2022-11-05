package epam.com.khshanovskyi.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;

import epam.com.khshanovskyi.dto.Crypto;
import epam.com.khshanovskyi.exception.UnableToFindFileByPathException;

class CryptoDtoParserTest {

    private static final String WRONG_PATH = "wrong.path";
    private static CryptoParser cryptoParser;

    @BeforeAll
    static void beforeAll() {
        cryptoParser = new CryptoDtoParser();
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @DisplayName("parseFromFile -> throws NullPointerException when param is null or empty")
    void parseFromFile_ThrowsNullPointerExceptionWhenParamNullOrEmpty(String fileName) {
        assertThrows(NullPointerException.class, () -> cryptoParser.parseFromFile(fileName));
    }

    @Test
    @DisplayName("parseFromFile -> throws UnableToFindFileByPathException when param is not correct")
    void parseFromFile_ThrowsUnableToFindFileByPathExceptionWhenPathIsWrong() {
        assertThrows(UnableToFindFileByPathException.class, () -> cryptoParser.parseFromFile(WRONG_PATH));
    }

    @Test
    @DisplayName("parseFromFile -> check if result is correct")
    void parseFromFile_ProvidesListWithCryptoDto() {
        List<Crypto> cryptos = cryptoParser.parseFromFile("src\\test\\resources\\crypto\\2021-11\\ETH_values.csv");

        assertNotNull(cryptos);
        assertNotNull(cryptos.get(0));
        assertEquals("ETH", cryptos.get(0).getName());
        assertNotNull(cryptos.get(0).getLocalDateTime());
    }

}