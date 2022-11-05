package epam.com.khshanovskyi.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import epam.com.khshanovskyi.dto.Crypto;
import epam.com.khshanovskyi.dto.NormalizedCrypto;
import epam.com.khshanovskyi.service.CryptoService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("advice")
@RequiredArgsConstructor
public class CryptoAdviceController {

    private final CryptoService cryptoService;

    @GetMapping(value = "/oldest", produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get oldest Crypto info",
      notes = "Get oldest Crypto info from the last presented month by passed Crypto name. \n" +
        "If name is not present then will take the oldest one from each existing file in the system. \n" +
        "Returns array with Crypto information")
    @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Successfully retrieved"),
      @ApiResponse(code = 400, message = "Inner exception related to validation of passed parameter or related to the" +
        " files handling with Crypto info")
    })
    public List<Crypto> getOldest(@RequestParam(name = "name", required = false)
                                  @ApiParam(name = "name", example = "BTC")
                                  String cryptoName) {
        return Objects.isNull(cryptoName) ? cryptoService.getOldest() : List.of(cryptoService.getOldest(cryptoName));
    }


    @GetMapping(value = "/newest", produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get newest Crypto info",
      notes = "Get newest Crypto info from the last presented month by passed Crypto name. \n " +
        "If name is not present then will take the newest one from each existing file in the system. \n" +
        "Returns array with Crypto information")
    @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Successfully retrieved"),
      @ApiResponse(code = 400, message = "Inner exception related to validation of passed parameter or related to the" +
        " files handling with Crypto info")
    })
    public List<Crypto> getNewest(@RequestParam(name = "name", required = false)
                                  @ApiParam(name = "name", example = "BTC")
                                  String cryptoName) {
        return Objects.isNull(cryptoName) ? cryptoService.getNewest() : List.of(cryptoService.getNewest(cryptoName));
    }

    @GetMapping(value = "/price/max", produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get Crypto info with max price.",
      notes = "Get Crypto info with max price from the last presented month by passed Crypto name. \n" +
        "If name is not present then will take one with max price from each existing file in the system. \n" +
        "Returns array with Crypto information")
    @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Successfully retrieved"),
      @ApiResponse(code = 400, message = "Inner exception related to validation of passed parameter or related to the" +
        " files handling with Crypto info")
    })
    public List<Crypto> getWithMaxPrice(@RequestParam(name = "name", required = false)
                                        @ApiParam(name = "name", example = "BTC")
                                        String cryptoName) {
        return Objects.isNull(cryptoName) ? cryptoService.getMaxByPrice() :
          List.of(cryptoService.getMaxByPrice(cryptoName));
    }

    @GetMapping(value = "/price/min", produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get Crypto info with min price.",
      notes = "Get Crypto info with min price from the last presented month by passed Crypto name. \n" +
        "If name is not present then will take one with min price from each existing file in the system. \n" +
        "Returns array with Crypto information")
    @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Successfully retrieved"),
      @ApiResponse(code = 400, message = "Inner exception related to validation of passed parameter or related to the" +
        " files handling with Crypto info")
    })
    public List<Crypto> getWithMinPrice(@RequestParam(name = "name", required = false)
                                        @ApiParam(name = "name", example = "BTC")
                                        String cryptoName) {
        return Objects.isNull(cryptoName) ? cryptoService.getMinByPrice() :
          List.of(cryptoService.getMinByPrice(cryptoName));
    }

    @GetMapping(value = "/price/normalize", produces = APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get Crypto info with descend normalized price.",
      notes = "Get Crypto info with descend normalized price by provided day from each existing file with " +
        "Crypto information from the last presented month. \nAlso it represent the index rate. \n" +
        "In case if day is not present then will do the same for last presented month in the system.\n  " +
        "Returns array with Crypto information and descending normalized (1 -> 0) index based on price for each Crypto")
    @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Successfully retrieved"),
      @ApiResponse(code = 400, message = "Inner exception related to validation of passed parameter or related to the" +
        " files handling with Crypto info")
    })
    public List<NormalizedCrypto> normalize(@RequestParam(required = false)
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                            @ApiParam(name = "day", example = "2022-01-31")
                                            LocalDate day,
                                            @RequestParam(required = false)
                                            @ApiParam(name = "dayInMilliseconds", example = "1643655600000")
                                            Long dayInMilliseconds) {
        return cryptoService.normalize(day, dayInMilliseconds);
    }

    @GetMapping(value = "/period")
    @ApiOperation(value = "Get Crypto info from last presented day up to days amount",
      notes = "Provides Crypto info from newest day up to amount of days (request param = 'days') \n" +
        "If param 'name' is passed then will collect data to required Crypto for requested amount of days, otherwise" +
        "will provide Crypto info of all presented Cryptos for required period of time. \n" +
        "Pay attention that 'days' parameter is required parameter. Also, there are some boundaries for the 'days'" +
        "parameter, it cannot be less than 1 and more than 365")
    @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Successfully retrieved"),
      @ApiResponse(code = 400, message = "Inner exception related to validation of passed parameter or related to the" +
        " files handling with Crypto info")
    })
    public List<Crypto> getInfoUpToDayAmount(@RequestParam(name = "name", required = false)
                                             @ApiParam(name = "name", example = "BTC")
                                             String cryptoName,
                                             @RequestParam
                                             @ApiParam(name = "days", example = "10", required = true)
                                             int amountOfDays) {
        return cryptoService.getCryptoByNameAndRangeOfDays(amountOfDays, cryptoName);
    }

}
