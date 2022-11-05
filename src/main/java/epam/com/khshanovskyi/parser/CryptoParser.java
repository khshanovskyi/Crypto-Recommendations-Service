package epam.com.khshanovskyi.parser;

import java.util.List;

import epam.com.khshanovskyi.dto.Crypto;

/**
 * Contract for parsing Crypto data from files to {@link Crypto}.
 */
public interface CryptoParser {

    List<Crypto> parseFromFile(String fileName);
}
