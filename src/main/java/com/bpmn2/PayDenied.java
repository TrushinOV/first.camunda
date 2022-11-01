package com.bpmn2;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PayDenied implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution){

        // Логирование
        log.info("Платеж (сумма {}) отказан как подозрительный", delegateExecution.getVariable("amount"));
    }
}
