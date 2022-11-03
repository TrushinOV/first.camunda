package com.bpmn2.delegates;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Логгер параметров блока процесса
 */
@Component
@Slf4j
public class LoggerDelegate implements JavaDelegate {

    public void execute(DelegateExecution execution) {
        log.info("""
                        Отработка блока activityName=[{}]:
                         activityId={},
                         processDefinitionId={},
                         processInstanceId={},
                         businessKey={},
                         executionId={}
                        """,
                execution.getCurrentActivityName(), execution.getCurrentActivityId(), execution.getProcessDefinitionId(),
                execution.getProcessInstanceId(), execution.getProcessBusinessKey(), execution.getId());
    }

}
