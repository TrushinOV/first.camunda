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
        variables.put("amount", 1001L);
        variables.put("approved", true);
        variables.put("isInterbank", true);
        variables.put("details", "Назначение платежа: детали");
        processInstance = runtimeService.startProcessInstanceByKey(PROCESS_KEY, variables);
        assertNotNull(processInstance);
        assertThat(processInstance).hasPassed("StartEvent_1").hasNotPassed("UserTask_1").isNotEnded();
    }

    @Test
    public void happyPath() {
        assertThat(processInstance).isWaitingAt("UserTask_1");
        completeTask().hasPassed("UserTask_1");
        assertThat(processInstance).isWaitingAt("UserTask_3");
        completeTask().hasPassed("UserTask_3");
        assertThat(processInstance).isWaitingAt("ServiceTask_4").externalTask().hasTopicName("interbank");
        assertThat(processInstance).hasVariables("isInterbank");
//        complete(externalTask("ServiceTask_4", processInstance));
//        complete(externalTask());

        assertThat(processInstance)
                .hasPassedInOrder("StartEvent_1", "ExclusiveGateway_1",
                        "UserTask_1", "ExclusiveGateway_2",
                        "ServiceTask_2", "ExclusiveGateway_3",
                        "UserTask_3", "ExclusiveGateway_4", "ExclusiveGateway_5"); //,
//                        "ServiceTask_4", "EndEvent_1");  // Разобраться, как мокировать/отключать воркеры (ExternalTaskClient)
    }

    private ProcessInstanceAssert completeTask() {
        complete(task());
        return assertThat(processInstance);
    }
}