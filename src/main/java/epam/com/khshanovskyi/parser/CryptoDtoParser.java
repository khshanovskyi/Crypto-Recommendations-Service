package epam.com.khshanovskyi.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.opencsv.bean.CsvToBeanBuilder;

import epam.com.khshanovskyi.dto.Crypto;
import epam.com.khshanovskyi.exception.UnableToFindFileByPathException;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides basic implementation of the {@link CryptoParser} for the {@link Crypto} DTO.
 */
@Component
@Slf4j
public class CryptoDtoParser implements CryptoParser {

    /**
     * Read rows from the file by <b>fileName</b>, convert it into {@link List<Crypto>}, and convert provided
     * {@link Long} <b>timestamp</b> in additional field {@link Crypto#setLocalDateTime(LocalDateTime)}.s
     *
     * @param fileName
     * @return {@link List<Crypto>}
     * @throws UnableToFindFileByPathException in case if file is not present
     */
    @Override
    public List<Crypto> parseFromFile(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            log.warn("Provided file name is null or empty");
            throw new NullPointerException("Provided file name cannot be null or empty");
        }

        FileReader fileReader = generateFileReader(fileName);
        List<Crypto> parsed = new CsvToBeanBuilder(fileReader)
          .withType(Crypto.class)
          .build()
          .parse();

        parsed.forEach(crypto -> crypto.setLocalDateTime(
          Instant.ofEpochMilli(crypto.getTimestampMilliseconds())
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime())
        );

        return parsed;
    }

    private FileReader generateFileReader(String fileName) {
        FileReader fileReader;
        try {
            fileReader = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            String message = String.format("Unable to find a file by path [%s]", fileName);
            log.warn(message);
            throw new UnableToFindFileByPathException(message, e);
        }
        return fileReader;
    }

}
