package epam.com.khshanovskyi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import epam.com.khshanovskyi.dto.Crypto;
import epam.com.khshanovskyi.dto.NormalizedCrypto;
import epam.com.khshanovskyi.exception.CryptoNameDoesNotExistException;
import epam.com.khshanovskyi.exception.CryptoValuesNotPresentException;
import epam.com.khshanovskyi.parser.CryptoDtoParser;
import lombok.SneakyThrows;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CryptoServiceTest {

    private static final String PATH_TO_FOLDER_WITH_FOLDERS = "src/test/resources/crypto";
    private static final String PATH_TO_FOLDER_WITH_FOLDERS_FOR_EXCEPTION_CASES = "src/test/resources/exception/crypto";
    private static final String BTC = "BTC";
    private static final String BTC_LOWER_CASE = "btc";
    private static final String BTC_CAMEL_CASE = "bTc";
    private static final String MY = "MY";
    private static final String WRONG_NAME = "WRONG";
    private static final LocalDate DAY = LocalDate.parse("2022-01-11");
    private static final Long DAY_IN_MILLISECONDS = 1641873600000L;
    private static final LocalDate DAY_NOT_EXISTING = LocalDate.parse("2005-11-29");
    private static final Long DAY_IN_MILLISECONDS_NOT_EXISTING = 1133300000000L;

    private static CryptoService cryptoService;
    private static CryptoService cryptoServiceForExceptionCases;

    @BeforeAll
    @SneakyThrows
    static void beforeAll() {
        cryptoService = new CryptoService(new CryptoDtoParser());
        cryptoServiceForExceptionCases = new CryptoService(new CryptoDtoParser());

        Field pathToFolder = CryptoService.class.getDeclaredField("pathToFolder");
        pathToFolder.setAccessible(true);
        pathToFolder.set(cryptoService, PATH_TO_FOLDER_WITH_FOLDERS);
        pathToFolder.set(cryptoServiceForExceptionCases, PATH_TO_FOLDER_WITH_FOLDERS_FOR_EXCEPTION_CASES);
    }

    @Test
    @Order(1)
    @DisplayName("getOldest -> throws CryptoValuesNotPresentException when file is empty")
    void getOldest_ThrowsCryptoValuesNotPresentExceptionWhenFileIsEmpty() {
        assertThrows(CryptoValuesNotPresentException.class, () -> cryptoServiceForExceptionCases.getOldest(MY));
    }

    @Test
    @Order(2)
    @DisplayName("getOldest -> check if result is correct for all files and list sorted revers by localDateTime")
    void getOldest_ProvidesListCryptoDtoFromAllFilesByLastPresentedMonthWithReversSortByLocalDateTime() {
        List<Crypto> cryptos = cryptoService.getOldest();

        notNullAsserts(cryptos);
        assertTrue(cryptos.get(0).getLocalDateTime().isBefore(cryptos.get(cryptos.size() - 1).getLocalDateTime()));
    }

    @Order(3)
    @ParameterizedTest
    @NullSource
    @EmptySource
    @DisplayName("getOldest -> throws NullPointerException when cryptoName is null or empty")
    void getOldest_ThrowsNullPointerExceptionWhenParamNullOrEmpty(String cryptoName) {
        assertThrows(NullPointerException.class, () -> cryptoService.getOldest(cryptoName));
    }

    @Test
    @Order(4)
    @DisplayName("getOldest -> throws CryptoNameDoesNotExistException when cryptoName is wrong")
    void getOldest_ThrowsCryptoNameDoesNotExistExceptionWhenCryptoNameDoesNotExist() {
        assertThrows(CryptoNameDoesNotExistException.class, () -> cryptoService.getOldest(WRONG_NAME));
    }

    @Test
    @Order(5)
    @DisplayName("getOldest -> throws CryptoValuesNotPresentException when cryptoName is null or empty")
    void getOldest_ThrowsCryptoValuesNotPresentExceptionWhenFileIsEmptyByParameter() {
        assertThrows(CryptoValuesNotPresentException.class, () -> cryptoServiceForExceptionCases.getOldest(MY));
    }

    @Order(6)
    @ParameterizedTest
    @ValueSource(strings = {BTC, BTC_LOWER_CASE, BTC_CAMEL_CASE})
    @DisplayName("getOldest -> check if result is correct where file name contains param and list sorted revers by localDateTime")
    void getOldest_ProvidesListCryptoDtoByParamAndByLastPresentedMonthWithReversSortByLocalDateTime(String cryptoName) {
        Crypto result = cryptoService.getOldest(cryptoName);
        List<Crypto> cryptos = cryptoService.getNewest();

        assertNotNull(result);
        assertTrue(result.getLocalDateTime().isBefore(cryptos.get(0).getLocalDateTime()));
        assertEquals(BTC, result.getName());
    }

    @Test
    @Order(7)
    @DisplayName("getNewest -> throws CryptoValuesNotPresentException when file is empty")
    void getNewest_ThrowsCryptoValuesNotPresentExceptionWhenFileIsEmpty() {
        assertThrows(CryptoValuesNotPresentException.class, () -> cryptoServiceForExceptionCases.getNewest(MY));
    }

    @Test
    @Order(8)
    @DisplayName("getNewest -> check if result is correct for all files and list sorted by localDateTime")
    void getNewest_ProvidesListCryptoDtoFromAllFilesByLastPresentedMonthWithSortByLocalDateTime() {
        List<Crypto> cryptos = cryptoService.getNewest();

        notNullAsserts(cryptos);
        assertTrue(cryptos.get(0).getLocalDateTime().isAfter(cryptos.get(cryptos.size() - 1).getLocalDateTime()));
    }

    @Order(9)
    @ParameterizedTest
    @NullSource
    @EmptySource
    @DisplayName("getNewest -> throws NullPointerException when cryptoName is null or empty")
    void getNewest_ThrowsNullPointerExceptionWhenParamNullOrEmpty(String cryptoName) {
        assertThrows(NullPointerException.class, () -> cryptoService.getNewest(cryptoName));
    }

    @Test
    @Order(10)
    @DisplayName("getNewest -> throws CryptoNameDoesNotExistException when cryptoName is wrong")
    void getNewest_ThrowsCryptoNameDoesNotExistExceptionWhenCryptoNameDoesNotExist() {
        assertThrows(CryptoNameDoesNotExistException.class, () -> cryptoService.getNewest(WRONG_NAME));
    }

    @Test
    @Order(11)
    @DisplayName("getNewest -> throws CryptoValuesNotPresentException when cryptoName is null or empty")
    void getNewest_ThrowsCryptoValuesNotPresentExceptionWhenFileIsEmptyByParameter() {
        assertThrows(CryptoValuesNotPresentException.class, () -> cryptoServiceForExceptionCases.getNewest(MY));
    }

    @Order(12)
    @ParameterizedTest
    @ValueSource(strings = {BTC, BTC_LOWER_CASE, BTC_CAMEL_CASE})
    @DisplayName("getNewest -> check if result is correct where file name contains param and list sorted by max price")
    void getNewest_ProvidesListCryptoDtoByParamAndByLastPresentedMonthWithSortByMaxPrice(String cryptoName) {
        Crypto result = cryptoService.getNewest(cryptoName);
        List<Crypto> cryptos = cryptoService.getOldest();

        assertNotNull(result);
        assertTrue(result.getLocalDateTime().isAfter(cryptos.get(0).getLocalDateTime()));
        assertEquals(BTC, result.getName());
    }

    @Test
    @Order(13)
    @DisplayName("getMaxByPrice -> throws CryptoValuesNotPresentException when file is empty")
    void getMaxByPrice_ThrowsCryptoValuesNotPresentExceptionWhenFileIsEmpty() {
        assertThrows(CryptoValuesNotPresentException.class, () -> cryptoServiceForExceptionCases.getMaxByPrice(MY));
    }

    @Test
    @Order(14)
    @DisplayName("getMaxByPrice -> check if result is correct for all files and list sorted by max price")
    void getMaxByPrice_ProvidesListCryptoDtoFromAllFilesByLastPresentedMonthWithSortByLocalDateTime() {
        List<Crypto> cryptos = cryptoService.getMaxByPrice();

        notNullAsserts(cryptos);
        assertTrue(cryptos.get(0).getPrice().compareTo(cryptos.get(cryptos.size() - 1).getPrice()) > 0);
    }

    @Order(15)
    @ParameterizedTest
    @NullSource
    @EmptySource
    @DisplayName("getMaxByPrice -> throws NullPointerException when cryptoName is null or empty")
    void getMaxByPrice_ThrowsNullPointerExceptionWhenParamNullOrEmpty(String cryptoName) {
        assertThrows(NullPointerException.class, () -> cryptoService.getNewest(cryptoName));
    }

    @Test
    @Order(16)
    @DisplayName("getMaxByPrice -> throws CryptoNameDoesNotExistException when cryptoName is wrong")
    void getMaxByPrice_ThrowsCryptoNameDoesNotExistExceptionWhenCryptoNameDoesNotExist() {
        assertThrows(CryptoNameDoesNotExistException.class, () -> cryptoService.getNewest(WRONG_NAME));
    }

    @Test
    @Order(17)
    @DisplayName("getMaxByPrice -> throws CryptoValuesNotPresentException when cryptoName is null or empty")
    void getMaxByPrice_ThrowsCryptoValuesNotPresentExceptionWhenFileIsEmptyByParameter() {
        assertThrows(CryptoValuesNotPresentException.class, () -> cryptoServiceForExceptionCases.getNewest(MY));
    }

    @Order(18)
    @ParameterizedTest
    @ValueSource(strings = {BTC, BTC_LOWER_CASE, BTC_CAMEL_CASE})
    @DisplayName("getMaxByPrice -> check if result is correct where file name contains param and list sorted by max price")
    void getMaxByPrice_ProvidesListCryptoDtoByParamAndByLastPresentedMonthWithSortByMaxPrice(String cryptoName) {
        Crypto result = cryptoService.getMaxByPrice(cryptoName);
        List<Crypto> cryptos = cryptoService.getMinByPrice();

        assertNotNull(result);
        assertTrue(result.getPrice().compareTo(cryptos.get(cryptos.size() - 1).getPrice()) > 0);
        assertEquals(BTC, result.getName());
    }

    @Test
    @Order(19)
    @DisplayName("getMinByPrice -> throws CryptoValuesNotPresentException when file is empty")
    void getMinByPrice_ThrowsCryptoValuesNotPresentExceptionWhenFileIsEmpty() {
        assertThrows(CryptoValuesNotPresentException.class, () -> cryptoServiceForExceptionCases.getMinByPrice(MY));
    }

    @Test
    @Order(20)
    @DisplayName("getMinByPrice -> check if result is correct for all files and list sorted by min price")
    void getMinByPrice_ProvidesListCryptoDtoFromAllFilesByLastPresentedMonthWithSortByMinPrice() {
        List<Crypto> cryptos = cryptoService.getMinByPrice();

        notNullAsserts(cryptos);
        assertTrue(cryptos.get(0).getPrice().compareTo(cryptos.get(cryptos.size() - 1).getPrice()) < 0);
    }

    @Order(21)
    @ParameterizedTest
    @NullSource
    @EmptySource
    @DisplayName("getMinByPrice -> throws NullPointerException when cryptoName is null or empty")
    void getMinByPrice_ThrowsNullPointerExceptionWhenParamNullOrEmpty(String cryptoName) {
        assertThrows(NullPointerException.class, () -> cryptoService.getMinByPrice(cryptoName));
    }

    @Test
    @Order(22)
    @DisplayName("getMinByPrice -> throws CryptoNameDoesNotExistException when cryptoName is wrong")
    void getMinByPrice_ThrowsCryptoNameDoesNotExistExceptionWhenCryptoNameDoesNotExist() {
        assertThrows(CryptoNameDoesNotExistException.class, () -> cryptoService.getMinByPrice(WRONG_NAME));
    }

    @Test
    @Order(23)
    @DisplayName("getMinByPrice -> throws CryptoValuesNotPresentException when cryptoName is null or empty")
    void getMinByPrice_ThrowsCryptoValuesNotPresentExceptionWhenFileIsEmptyByParameter() {
        assertThrows(CryptoValuesNotPresentException.class, () -> cryptoServiceForExceptionCases.getMinByPrice(MY));
    }

    @Order(24)
    @ParameterizedTest
    @ValueSource(strings = {BTC, BTC_LOWER_CASE, BTC_CAMEL_CASE})
    @DisplayName("getMinByPrice -> check if result is correct where file name contains param and list sorted by min price")
    void getMinByPrice_ProvidesListCryptoDtoByParamAndByLastPresentedMonthWithSortByMinPrice(String cryptoName) {
        Crypto result = cryptoService.getMinByPrice(cryptoName);
        List<Crypto> cryptos = cryptoService.getMaxByPrice();

        assertNotNull(result);
        assertTrue(result.getPrice().compareTo(cryptos.get(0).getPrice()) < 0);
        assertEquals(BTC, result.getName());
    }

    @Order(25)
    @ParameterizedTest
    @MethodSource("normalize_provideParams")
    @DisplayName("normalize -> throws CryptoValuesNotPresentException when file is empty")
    void normalize_ThrowsCryptoValuesNotPresentExceptionWhenFileIsEmpty(LocalDate day, Long timestampMilliseconds) {
        assertThrows(CryptoValuesNotPresentException.class,
          () -> cryptoServiceForExceptionCases.normalize(day, timestampMilliseconds));
    }

    @Order(26)
    @ParameterizedTest
    @MethodSource("normalize_provideWrongParams")
    @DisplayName("normalize -> throws CryptoValuesNotPresentException when file is empty")
    void normalize_ThrowsCryptoValuesNotPresentExceptionWhenFileIsEmptyWithWrongParams(LocalDate day,
                                                                                       Long timestampMilliseconds) {
        assertThrows(CryptoValuesNotPresentException.class, () -> cryptoService.normalize(day, timestampMilliseconds));
    }

    @Order(27)
    @ParameterizedTest
    @MethodSource("normalize_provideParams")
    @DisplayName("normalize -> check if result is correct for all files and list sorted by index")
    void normalize_ProvidesListNormalizedCryptoDtoFromAllFilesByLastPresentedMonthWithCountedIndexDescending(
      LocalDate day, Long timestampMilliseconds) {
        List<NormalizedCrypto> cryptos = cryptoService.normalize(day, timestampMilliseconds);

        assertNotNull(cryptos);
        assertNotNull(cryptos.get(0));
        assertNotNull(cryptos.get(0).crypto());
        assertTrue(cryptos.get(0).index().compareTo(cryptos.get(cryptos.size() - 1).index()) > 0);
    }

    @Order(28)
    @ParameterizedTest
    @EmptySource
    @DisplayName("getCryptoByNameAndRangeOfDays -> throws NullPointerException when cryptoName is empty")
    void getCryptoByNameAndRangeOfDays_ThrowsNullPointerExceptionWhenParamNullOrEmpty(String cryptoName) {
        assertThrows(NullPointerException.class, () -> cryptoService.getCryptoByNameAndRangeOfDays(1, cryptoName));
    }

    @Test
    @Order(29)
    @DisplayName("getCryptoByNameAndRangeOfDays -> throws CryptoNameDoesNotExistException when cryptoName is wrong")
    void getCryptoByNameAndRangeOfDays_ThrowsCryptoNameDoesNotExistExceptionWhenCryptoNameDoesNotExist() {
        assertThrows(CryptoNameDoesNotExistException.class,
          () -> cryptoService.getCryptoByNameAndRangeOfDays(1, WRONG_NAME));
    }

    @Order(30)
    @ParameterizedTest
    @ValueSource(ints = {0, 366})
    @DisplayName("getCryptoByNameAndRangeOfDays -> throws IllegalArgumentException when amountOfDays out of bounds")
    void getCryptoByNameAndRangeOfDays_ThrowsIllegalArgumentExceptionWhenAmountOutOfBound(int amountOfDays) {
        assertThrows(IllegalArgumentException.class,
          () -> cryptoService.getCryptoByNameAndRangeOfDays(amountOfDays, WRONG_NAME));
    }

    @Order(27)
    @ParameterizedTest
    @MethodSource("getCryptoByNameAndRangeOfDays_provideParams")
    @DisplayName("getCryptoByNameAndRangeOfDays -> check if result is correct for files with bounded amount")
    void getCryptoByNameAndRangeOfDays_ProvidesListCryptoDtoFromFilesBy(int amountOfDays, String cryptoName) {
        List<Crypto> cryptos = cryptoService.getCryptoByNameAndRangeOfDays(amountOfDays, cryptoName);

        notNullAsserts(cryptos);
        assertTrue(cryptos.get(0).getLocalDateTime().isAfter(cryptos.get(cryptos.size() - 1).getLocalDateTime()));
    }

    private static Stream<Arguments> normalize_provideParams() {
        return Stream.of(Arguments.of(null, null),
          Arguments.of(DAY, null),
          Arguments.of(null, DAY_IN_MILLISECONDS),
          Arguments.of(DAY, DAY_IN_MILLISECONDS));
    }

    private static Stream<Arguments> getCryptoByNameAndRangeOfDays_provideParams() {
        return Stream.of(Arguments.of(5, null),
          Arguments.of(5, BTC));
    }

    private static Stream<Arguments> normalize_provideWrongParams() {
        return Stream.of(
          Arguments.of(DAY_NOT_EXISTING, null),
          Arguments.of(null, DAY_IN_MILLISECONDS_NOT_EXISTING),
          Arguments.of(DAY_NOT_EXISTING, DAY_IN_MILLISECONDS_NOT_EXISTING));
    }

    private void notNullAsserts(List<Crypto> cryptos) {
        assertNotNull(cryptos);
        assertNotNull(cryptos.get(0));
        assertNotNull(cryptos.get(0).getLocalDateTime());
    }

}