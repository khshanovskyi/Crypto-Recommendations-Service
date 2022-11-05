package epam.com.khshanovskyi.service;

import static epam.com.khshanovskyi.util.FilesUtil.getFileNamesWithPath;
import static epam.com.khshanovskyi.util.FilesUtil.getFolderPathOfTheLastPresentedMonth;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import epam.com.khshanovskyi.dto.Crypto;
import epam.com.khshanovskyi.dto.NormalizedCrypto;
import epam.com.khshanovskyi.exception.CryptoNameDoesNotExistException;
import epam.com.khshanovskyi.exception.CryptoValuesNotPresentException;
import epam.com.khshanovskyi.parser.CryptoParser;
import epam.com.khshanovskyi.util.FilesUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides APIs for extraction various information about Crypto from stored files.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CryptoService {

    private static final Comparator<Crypto> PRICE_COMPARATOR = Comparator.comparing(Crypto::getPrice);
    private static final Comparator<Crypto> TIME_COMPARATOR = Comparator.comparing(Crypto::getLocalDateTime);
    private static final int MIN_AMOUNT_OF_DAYS = 1;
    private static final int MAX_AMOUNT_OF_DAYS = 365;

    @Value("${path.to.folder.with.crypto.files}")
    private String pathToFolder;
    private final CryptoParser cryptoParser;

    /**
     * Select all {@link Crypto} from stored files and return the oldest one from each file.
     *
     * @return {@link List<Crypto>}
     */
    @Cacheable("parsed-cryptos-from-csv-oldest")
    public List<Crypto> getOldest() {
        return getStreamWithListCrypto()
          .map(cryptos -> findMin(cryptos, TIME_COMPARATOR))
          .sorted(TIME_COMPARATOR)
          .toList();
    }

    /**
     * Select {@link Crypto} by the <b>cryptoName</b> from specific stored file (file with the same name) and return the
     * oldest one.
     *
     * @param cryptoName short name of Crypto, Bitcoin -> BTC || btc
     * @return {@link List<Crypto>}
     * @throws CryptoNameDoesNotExistException in case if file for <b>cryptoName</b> is not present
     * @throws CryptoValuesNotPresentException in case if no value present in file
     */
    @Cacheable("parsed-cryptos-from-csv-oldest-by-name")
    public Crypto getOldest(String cryptoName) {
        checkIfEmpty(cryptoName);
        String cryptoNameInUpperCase = cryptoName.toUpperCase();
        validateIfPassedCryptoNameExists(cryptoNameInUpperCase);

        return getFilteredStreamWithListCrypto(cryptoNameInUpperCase)
          .map(cryptos -> findMin(cryptos, TIME_COMPARATOR))
          .findFirst()
          .orElseThrow(generateCryptoValuesNotPresentException(cryptoName));
    }

    /**
     * Select all {@link Crypto} from stored files and return the newest one from each file.
     *
     * @return {@link List<Crypto>}
     */
    @Cacheable("parsed-cryptos-from-csv-newest")
    public List<Crypto> getNewest() {
        return getStreamWithListCrypto()
          .map(cryptos -> findMax(cryptos, TIME_COMPARATOR))
          .sorted(TIME_COMPARATOR.reversed())
          .toList();
    }

    /**
     * Select {@link Crypto} by the <b>cryptoName</b> from specific stored file (file with the same name) and return the
     * newest one.
     *
     * @param cryptoName short name of Crypto, Bitcoin -> BTC || btc
     * @return {@link List<Crypto>}
     * @throws CryptoNameDoesNotExistException in case if file for <b>cryptoName</b> is not present
     * @throws CryptoValuesNotPresentException in case if no value present in file
     */
    @Cacheable("parsed-cryptos-from-csv-newest-by-name")
    public Crypto getNewest(String cryptoName) {
        checkIfEmpty(cryptoName);
        String cryptoNameInUpperCase = cryptoName.toUpperCase();
        validateIfPassedCryptoNameExists(cryptoNameInUpperCase);

        return getFilteredStreamWithListCrypto(cryptoNameInUpperCase)
          .map(cryptos -> findMax(cryptos, TIME_COMPARATOR))
          .findFirst()
          .orElseThrow(generateCryptoValuesNotPresentException(cryptoName));
    }

    /**
     * Select all {@link Crypto} from stored files and return one with the highest price from each file.
     *
     * @return {@link List<Crypto>}
     */
    @Cacheable("parsed-cryptos-from-csv-max-price")
    public List<Crypto> getMaxByPrice() {
        return getStreamWithListCrypto()
          .map(cryptos -> findMax(cryptos, PRICE_COMPARATOR))
          .sorted(PRICE_COMPARATOR.reversed())
          .toList();
    }

    /**
     * Select {@link Crypto} by the <b>cryptoName</b> from specific stored file (file with the same name) and return one
     * with the highest price.
     *
     * @param cryptoName short name of Crypto, Bitcoin -> BTC || btc
     * @return {@link List<Crypto>}
     * @throws CryptoNameDoesNotExistException in case if file for <b>cryptoName</b> is not present
     * @throws CryptoValuesNotPresentException in case if no value present in file
     */
    @Cacheable("parsed-cryptos-from-csv-max-price-by-name")
    public Crypto getMaxByPrice(String cryptoName) {
        checkIfEmpty(cryptoName);
        String cryptoNameInUpperCase = cryptoName.toUpperCase();
        validateIfPassedCryptoNameExists(cryptoNameInUpperCase);

        return getFilteredStreamWithListCrypto(cryptoNameInUpperCase)
          .map(cryptos -> findMax(cryptos, PRICE_COMPARATOR))
          .findFirst()
          .orElseThrow(generateCryptoValuesNotPresentException(cryptoName));
    }

    /**
     * Select all {@link Crypto} from stored files and return one with the lowest price from each file.
     *
     * @return {@link List<Crypto>}
     */
    @Cacheable("parsed-cryptos-from-csv-min-price")
    public List<Crypto> getMinByPrice() {
        return getStreamWithListCrypto()
          .map(cryptos -> findMin(cryptos, PRICE_COMPARATOR))
          .sorted(PRICE_COMPARATOR)
          .toList();
    }

    /**
     * Select {@link Crypto} by the <b>cryptoName</b> from specific stored file (file with the same name) and return one
     * with the lowest price.
     *
     * @param cryptoName short name of Crypto, Bitcoin -> BTC || btc
     * @return {@link List<Crypto>}
     * @throws CryptoNameDoesNotExistException in case if file for <b>cryptoName</b> is not present
     * @throws CryptoValuesNotPresentException in case if no value present in file
     */
    @Cacheable("parsed-cryptos-from-csv-min-price-by-name")
    public Crypto getMinByPrice(String cryptoName) {
        checkIfEmpty(cryptoName);
        String cryptoNameInUpperCase = cryptoName.toUpperCase();
        validateIfPassedCryptoNameExists(cryptoNameInUpperCase);

        return getFilteredStreamWithListCrypto(cryptoNameInUpperCase)
          .map(cryptos -> findMin(cryptos, PRICE_COMPARATOR))
          .findFirst()
          .orElseThrow(generateCryptoValuesNotPresentException(cryptoName));
    }

    /**
     * Select {@link Crypto} by specified day, then normalize by price it and convert it to the {@link NormalizedCrypto}
     * where {@link NormalizedCrypto#index()} will be range of price (from 0 to 1).
     * In case if neither <b>day</b> or <b>dayInMilliseconds</b> present -> do the same but for all period of time.
     *
     * @param day               {@link LocalDate} required day
     * @param dayInMilliseconds {@link Long} required day in milliseconds
     * @return {@link List<NormalizedCrypto>}
     * @throws CryptoValuesNotPresentException in case if no value present in file
     */
    @Cacheable("parsed-cryptos-from-csv-normalized")
    public List<NormalizedCrypto> normalize(LocalDate day, Long dayInMilliseconds) {
        List<Crypto> cryptos = getCryptosByDay(day, dayInMilliseconds);

        BigDecimal min = getMin(cryptos);
        BigDecimal max = getMax(cryptos);
        return cryptos.stream()
          .map(crypto -> new NormalizedCrypto(crypto,
            crypto.getPrice().subtract(min).divide(max.subtract(min), 6, RoundingMode.UP)))
          .sorted(Comparator.comparing(NormalizedCrypto::index).reversed())
          .toList();
    }

    /**
     * Select {@link Crypto} with specified range of days and specified name in desciding order by {@link Crypto#getLocalDateTime()}
     * 'amountOfDays' is required and cannot be (x < 1 || x > 365), bound for 'x > 365' need to avoid high loading of
     * processor.
     * 'cryptoName' is not required field, if not present, will do it for all presented Crypto info files.
     *
     * @param amountOfDays required amount of days
     * @param cryptoName   short name of Crypto, Bitcoin -> BTC || btc
     * @return {@link List<Crypto>} sorted revers by {@link Crypto#getLocalDateTime()}
     * @throws IllegalArgumentException        in case if 'amountOfDays' is out of bound (x < 1 || x > 365)
     * @throws CryptoValuesNotPresentException in case if no 'cryptoName' present in file
     */
    @Cacheable("parsed-cryptos-from-csv-bound-on-amount")
    public List<Crypto> getCryptoByNameAndRangeOfDays(int amountOfDays, String cryptoName) {
        validateIfAmountOfDaysHasAppropriateValue(amountOfDays);
        List<String> foldersPath = FilesUtil.getFoldersPath(pathToFolder);

        return Objects.nonNull(cryptoName) ?
          getCryptoListByNameAndRangeIfDays(amountOfDays, foldersPath, cryptoName.toUpperCase()) :
          getCryptosInRangeOfDays(amountOfDays, foldersPath);
    }

    private void validateIfAmountOfDaysHasAppropriateValue(int amountOfDays) {
        if (amountOfDays < MIN_AMOUNT_OF_DAYS) {
            String message = String.format("[amountOfDays] cannot be less than 1, current value is [%s]", amountOfDays);
            log.warn(message);
            throw new IllegalArgumentException(message);
        } else if (amountOfDays > MAX_AMOUNT_OF_DAYS) {
            String message =
              String.format("[amountOfDays] cannot be more than 365, current value is [%s]", amountOfDays);
            log.warn(message);
            throw new IllegalArgumentException(message);
        }
    }

    //TODO define a way to do it via one stream
    private List<Crypto> getCryptoListByNameAndRangeIfDays(int amountOfDays, List<String> foldersPath,
                                                           String cryptoName) {
        checkIfEmpty(cryptoName);
        String cryptoNameInUpperCase = cryptoName.toUpperCase();
        validateIfPassedCryptoNameExists(cryptoNameInUpperCase);

        Map<LocalDate, List<Crypto>> dayCryptoMap = defineTypeOfStreamAndProvide(amountOfDays, foldersPath)
          .limit(calculateLimitForAmountOfFoldersWithFiles(amountOfDays))
          .map(FilesUtil::getFileNamesWithPath)
          .flatMap(Collection::stream)
          .filter(fileName -> fileName.contains(cryptoNameInUpperCase))
          .map(cryptoParser::parseFromFile)
          .flatMap(Collection::stream)
          .collect(Collectors.groupingBy(crypto -> crypto.getLocalDateTime().toLocalDate()));

        return collectToListWithBoundOfAmountOfDays(amountOfDays, dayCryptoMap);
    }

    //TODO define a way to do it via one stream
    private List<Crypto> getCryptosInRangeOfDays(int amountOfDays, List<String> foldersPath) {
        Map<LocalDate, List<Crypto>> dayCryptoMap = defineTypeOfStreamAndProvide(amountOfDays, foldersPath)
          .limit(calculateLimitForAmountOfFoldersWithFiles(amountOfDays))
          .map(FilesUtil::getFileNamesWithPath)
          .flatMap(Collection::stream)
          .map(cryptoParser::parseFromFile)
          .flatMap(Collection::stream)
          .collect(Collectors.groupingBy(crypto -> crypto.getLocalDateTime().toLocalDate()));

        return collectToListWithBoundOfAmountOfDays(amountOfDays, dayCryptoMap);
    }

    //TODO performance tests for defining amount of days when we need parallel stream
    private Stream<String> defineTypeOfStreamAndProvide(int amountOfDays, List<String> foldersPath) {
        return amountOfDays < 90 ? foldersPath.stream() : foldersPath.parallelStream();
    }

    /**
     * Calculates limit for folders amount with files of Crypto info.
     * Works like:
     * if amountOfDays < 31
     * -> then we provide 2 folders (can be case when one folder contains files Crypto info for not full month)
     * else
     * -> amountOfDays / 30 (cannot be less than 1) and + 1 for case if Crypto info file contains info not for full month
     */
    private int calculateLimitForAmountOfFoldersWithFiles(int amountOfDays) {
        return amountOfDays < 31 ? 2 : amountOfDays / 30 + 1;
    }

    private List<Crypto> collectToListWithBoundOfAmountOfDays(int amountOfDays,
                                                              Map<LocalDate, List<Crypto>> dayCryptoMap) {
        return dayCryptoMap.entrySet().stream()
          .limit(amountOfDays)
          .map(Map.Entry::getValue)
          .flatMap(Collection::stream)
          .sorted(TIME_COMPARATOR.reversed())
          .toList();
    }

    private List<Crypto> getCryptosByDay(LocalDate day, Long dayInMilliseconds) {
        List<Crypto> cryptos;

        if (Objects.nonNull(day)) {
            cryptos = getCryptosByDay(day);
        } else if (Objects.nonNull(dayInMilliseconds) && dayInMilliseconds != 0L) {
            cryptos = getCryptosByDay(Instant.ofEpochMilli(dayInMilliseconds)
              .atZone(ZoneId.systemDefault())
              .toLocalDate());
        } else {
            cryptos = getCryptoStream().toList();
        }

        return cryptos;
    }

    private Stream<Crypto> getCryptoStream() {
        return getStreamWithListCrypto()
          .flatMap(Collection::stream);
    }

    private List<Crypto> getCryptosByDay(LocalDate day) {
        return getCryptoStream()
          .filter(crypto -> crypto.getLocalDateTime().toLocalDate().equals(day))
          .toList();
    }

    private Stream<List<Crypto>> getStreamWithListCrypto() {
        String folderPathWithFiles = getFolderPathOfTheLastPresentedMonth(pathToFolder);
        return getFileNamesWithPath(folderPathWithFiles).stream()
          .map(cryptoParser::parseFromFile);
    }

    private Stream<List<Crypto>> getFilteredStreamWithListCrypto(String cryptoName) {
        String folderPathWithFiles = getFolderPathOfTheLastPresentedMonth(pathToFolder);
        return getFileNamesWithPath(folderPathWithFiles).stream()
          .filter(fileName -> fileName.contains(cryptoName))
          .map(cryptoParser::parseFromFile);
    }

    private void validateIfPassedCryptoNameExists(String cryptoName) {
        String folderPathWithFiles = getFolderPathOfTheLastPresentedMonth(pathToFolder);
        List<String> cryptoNames = FilesUtil.getCryptoNamesFromFileNames(folderPathWithFiles);
        if (cryptoNames.stream().noneMatch(splitFileName -> splitFileName.equals(cryptoName))) {
            String message = String.format("Unable to find a file with crypto name [%s]. " +
              "Please take a look at existing names and choose one that appropriate for you. " +
              "Exists: [%s]", cryptoName, String.join(", ", cryptoNames));
            log.warn(message);
            throw new CryptoNameDoesNotExistException(message);
        }
    }

    private BigDecimal getMin(List<Crypto> cryptos) {
        return cryptos.stream()
          .map(Crypto::getPrice)
          .min(BigDecimal::compareTo)
          .orElseThrow(CryptoValuesNotPresentException::new);
    }

    private BigDecimal getMax(List<Crypto> cryptos) {
        return cryptos.stream()
          .map(Crypto::getPrice)
          .max(BigDecimal::compareTo)
          .orElseThrow(CryptoValuesNotPresentException::new);
    }

    private Crypto findMin(List<Crypto> cryptos, Comparator<Crypto> comparator) {
        return cryptos.stream().min(comparator).orElseThrow(CryptoValuesNotPresentException::new);
    }

    private Crypto findMax(List<Crypto> cryptos, Comparator<Crypto> comparator) {
        return cryptos.stream().max(comparator).orElseThrow(CryptoValuesNotPresentException::new);
    }

    private void checkIfEmpty(String cryptoName) {
        if (StringUtils.isEmpty(cryptoName)) {
            log.warn("Provided name of Crypto is null or empty");
            throw new NullPointerException("Provided name of Crypto cannot be null or empty");
        }
    }

    private Supplier<CryptoValuesNotPresentException> generateCryptoValuesNotPresentException(String cryptoName) {
        String message = String.format("File does not contain any records for Crypto with the name [%s]", cryptoName);
        log.warn(message);
        return () -> new CryptoValuesNotPresentException(message);
    }

}
