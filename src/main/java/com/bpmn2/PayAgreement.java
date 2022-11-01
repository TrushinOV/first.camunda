package com.bpmn2;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PayAgreement implements JavaDelegate { // ${payAgreement} в камунде

    @Override
    public void execute(DelegateExecution delegateExecution){

        // Логирование
        Boolean approved = (Boolean)delegateExecution.getVariable("approved");
        String agree = approved ? "платеж одобрен." : "платеж оказан.";
        log.info("Сумма {}: {}", delegateExecution.getVariable("amount"), agree);
    }
}
