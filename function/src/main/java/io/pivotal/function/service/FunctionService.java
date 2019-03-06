package io.pivotal.function.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RefreshScope
public class FunctionService {

    @Autowired
    RestTemplate restTemplate;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public ResponseEntity<String> callFunction(String transactionId,
                              String transactionDescription) {
        logger.info("Submitting Transaction {} to Function", transactionId);
        ResponseEntity<String> result = null;
        try {
            result = restTemplate.postForEntity(
                    "https://czjbyefvra.execute-api.ap-southeast-1.amazonaws.com/test?txnId="
                            + transactionId
                            + "&txnDesc=" +
                            transactionDescription,
                    transactionDescription, String.class);
            logger.info("Function call Response code: {}", result.getStatusCode());
        } catch (Exception e) {
            logger.error("Exception posting to function.", e);
        }
        return result;
    }
}
