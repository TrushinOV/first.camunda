package com.bpmn2.delegates;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Aml implements JavaDelegate {
    public void execute(DelegateExecution delegateExecution) {
        // Определяем признак toAML по четности суммы
        Long amount = (Long) delegateExecution.getVariable("amount");
        boolean check = amount % 2 == 1;
        delegateExecution.setVariable("toAML", check);

        // Логирование
        String toCheck = check ? "платеж отправлен на проверку." : "платеж не подозрительный.";
        log.info("Сумма {}: {}", amount, toCheck);
    }
}
