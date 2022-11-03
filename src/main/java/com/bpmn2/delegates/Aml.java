package com.bpmn2.delegates;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Slf4j
public class Aml implements JavaDelegate {
    public void execute(DelegateExecution delegateExecution){
        // Определяем признак toAML
        Random rand = new Random();
        boolean check = rand.nextBoolean();
        delegateExecution.setVariable("toAML", check);

        // Логирование
        String toCheck = check ? "платеж отправлен на проверку." : "платеж не подозрительный.";
        log.info("Сумма {}: {}", delegateExecution.getVariable("amount"), toCheck);
    }
}
