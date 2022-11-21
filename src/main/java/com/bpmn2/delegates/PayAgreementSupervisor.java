package com.bpmn2.delegates;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PayAgreementSupervisor implements JavaDelegate { // ${payAgreement} в камунде

    @Override
    public void execute(DelegateExecution delegateExecution){

        // Логирование
        Boolean approved = (Boolean)delegateExecution.getVariable("approved_s");
        String agree = approved ? "платеж одобрен" : "платеж оказан";
        log.info("Сумма {}: {} супервизором.", delegateExecution.getVariable("amount"), agree);
    }
}
