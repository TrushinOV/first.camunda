package com.bpmn2;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.assertions.bpmn.ProcessInstanceAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ApplicationTest {

    @Autowired
    private RuntimeService runtimeService;

    ProcessInstance processInstance;
    Map<String, Object> variables;
    final String PROCESS_KEY = "payments";

    @BeforeEach
    public void beforeEach() {
        variables = new HashMap<>();
        variables.put("amount", 1001);
        variables.put("approved", true);
        variables.put("isInterbank", true);
        variables.put("details", "Назначение платежа: детали");
        processInstance = runtimeService.startProcessInstanceByKey(PROCESS_KEY, variables);
        assertNotNull(processInstance);
        assertThat(processInstance).hasPassed("StartEvent_1").hasNotPassed("Task_2").isNotEnded();
    }

    @Test
    public void happyPath() {
        completeTask().hasPassed("Task_2");
        completeTask().hasPassed("Task_4");
//        assertThat(processInstance).isWaitingAt("Task_5").externalTask().hasTopicName("interbank");
//        complete(externalTask());
//        complete(externalTask("Task_5"));
        assertThat(processInstance)
                .hasPassedInOrder("StartEvent_1", "ExclusiveGateway_1",
                        "Task_2", "ExclusiveGateway_2",
                        "Task_3", "ExclusiveGateway_3",
                        "Task_4", "ExclusiveGateway_4", "ExclusiveGateway_5"); //,
//                        "Task_5", "EndEvent_1");  // Разобраться, как мокировать/отключать воркеры (ExternalTaskClient)
    }

    private ProcessInstanceAssert completeTask() {
        complete(task());
        return assertThat(processInstance);
    }
}