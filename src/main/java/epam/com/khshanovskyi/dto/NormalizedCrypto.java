package epam.com.khshanovskyi.dto;

import java.math.BigDecimal;

public record NormalizedCrypto(Crypto crypto, BigDecimal index) {
}
