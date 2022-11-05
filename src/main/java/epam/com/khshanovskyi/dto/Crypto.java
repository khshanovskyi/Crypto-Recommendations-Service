package epam.com.khshanovskyi.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
public class Crypto {

    @CsvBindByName(column = "symbol")
    private String name;

    @CsvBindByName
    private BigDecimal price;

    private LocalDateTime localDateTime;

    @CsvBindByName(column = "timestamp")
    private Long timestampMilliseconds;
}
