package io.pivotal.function.controller;

import io.pivotal.function.service.FunctionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FunctionController {

    @Autowired
    FunctionService functionService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Proxies a call to Lambda function to notify.
     * @param transactionId request param
     * @param transactionDesc request param
     * @return The portfolio with HTTP OK.
     */
    @RequestMapping(value = "/function/notification", method = RequestMethod.POST)
    public ResponseEntity<String> sendToFunction(
            @RequestParam("txnid") final String transactionId,
            @RequestParam("txnDesc") final String transactionDesc) {
        logger.debug("FunctionController: Processing Request with txnId:{}", transactionId);
        ResponseEntity<String> response = functionService.callFunction(transactionId,transactionDesc);
        logger.debug("FunctionController: completed Function call with txnId:{}" + transactionId);

        if (null == response) {
            return new ResponseEntity<String>(transactionId, getNoCacheHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(transactionId, getNoCacheHeaders(), HttpStatus.OK);
    }

    private HttpHeaders getNoCacheHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Cache-Control", "no-cache");
        return responseHeaders;
    }
}
