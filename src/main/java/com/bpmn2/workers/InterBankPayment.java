package com.bpmn2.workers;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;

import java.awt.*;
import java.net.URI;

/*
Заглушка обработки межбанковского платежа.
Стартовать после Application.
 */

@Slf4j
public class InterBankPayment {

    public static void main(String[] args) {
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl("http://localhost:8080/engine-rest")
                .asyncResponseTimeout(10000) // long polling timeout
                .build();

        // subscribe to an external task topic as specified in the process
        client.subscribe("interbank")
                .lockDuration(1000) // the default lock duration is 20 seconds, but you can override this
                .handler((externalTask, externalTaskService) -> {
                    // Business logic
                    try {
                        Desktop.getDesktop().browse(new URI("https://docs.camunda.org/get-started/quick-start/complete"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Get process variables
                    String details = externalTask.getVariable("details");
                    Long amount = externalTask.getVariable("amount");
                    log.info("Детали транзакции межбанковского платежа {}: '{}'", amount, details);

                    // Complete the task
                    externalTaskService.complete(externalTask);
                })
                .open();
    }
}