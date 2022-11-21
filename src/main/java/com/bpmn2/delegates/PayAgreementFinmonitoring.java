package com.bpmn2.delegates;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PayAgreementFinmonitoring implements JavaDelegate { // ${payAgreement} в камунде

    @Override
    public void execute(DelegateExecution delegateExecution){

        // Логирование
        Boolean approved = (Boolean)delegateExecution.getVariable("approved_f");
        String agree = approved ? "платеж одобрен" : "платеж оказан";
        log.info("Сумма {}: {} фин.мониторингом.", delegateExecution.getVariable("amount"), agree);
    }
}
