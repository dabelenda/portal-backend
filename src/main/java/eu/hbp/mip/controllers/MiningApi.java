package eu.hbp.mip.controllers;

import com.google.gson.Gson;
import eu.hbp.mip.akka.WokenClientController;
import eu.hbp.mip.model.Mining;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.Date;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created by mirco on 06.01.17.
 */
@RestController
@RequestMapping(value = "/mining", produces = {APPLICATION_JSON_VALUE})
@Api(value = "/mining", description = "the mining API")
public class MiningApi extends WokenClientController {

    private static final Logger LOGGER = Logger.getLogger(MiningApi.class);
    private static final Gson gson = new Gson();

    @ApiOperation(value = "Run an algorithm", response = String.class)
    @Cacheable(value = "mining", condition = "#query.getAlgorithm().getCode() == 'histograms'", key = "#query.toString()", unless = "#result.getStatusCode().value()!=200")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity runAlgorithm(@RequestBody eu.hbp.mip.model.MiningQuery query) throws IOException {
        LOGGER.info("Run an algorithm");

        return askWokenQuery(query.prepareQuery(), 120,
                result -> {
                    if (result.error().nonEmpty()) {
                        LOGGER.error(result.error().get());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\":\"" + result.error().get() + "\"}");
                    } else {
                        Mining mining = new Mining(
                                result.jobId(),
                                result.node(),
                                result.algorithm(),
                                result.shape(),
                                Date.from(result.timestamp().toInstant()),
                                result.data().get().compactPrint()
                        );
                        return ResponseEntity.ok(gson.toJson(mining.jsonify()));
                    }
                });
    }

}
