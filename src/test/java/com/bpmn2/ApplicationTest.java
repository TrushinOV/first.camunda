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
        variables.put("details", "Назначение платежа: детали");
    }

    /**
     * Определение стартовых параметров и шагов
     *
     * @param amount      сумма платежа
     * @param approvedS   одобрен ли платеж супервизором
     * @param approvedF   одобрен ли платеж финмониторингом
     * @param isInterbank является ли платеж внутрибанковским
     */
    public void startSteps(Long amount, boolean approvedS, boolean approvedF, boolean isInterbank) {
        variables.put("amount", amount);
        variables.put("approved_s", approvedS);
        variables.put("approved_f", approvedF);
        variables.put("isInterbank", isInterbank);
        processInstance = runtimeService.startProcessInstanceByKey(PROCESS_KEY, variables);
        assertNotNull(processInstance);
        assertThat(processInstance).hasPassed("StartEvent_1").hasNotPassed("UserTask_1").isNotEnded();
        assertThat(processInstance).isWaitingAt("UserTask_1");
        completeTask().hasPassed("UserTask_1");
    }

    @Test
    public void happyPathInterbank() {
        // Задаем внутрибанковский одобренный платеж с требующей проверки "хорошей" суммой
        startSteps(1001L, true, true, true);

        assertThat(processInstance).isWaitingAt("UserTask_3");
        completeTask().hasPassed("UserTask_3");
        assertThat(processInstance).isWaitingAtExactly("ServiceTask_4").externalTask().hasTopicName("interbank");
        assertThat(processInstance).hasVariables("isInterbank");
        // Важно! В деплойментах  по топику (interbank)  д.быть "чисто".
        // Иначе получим тут error ".. Multiple external tasks found for externalTask"
        complete(externalTask("ServiceTask_4", processInstance));

        assertThat(processInstance)
                .hasPassedInOrder("StartEvent_1", "ExclusiveGateway_1",
                        "UserTask_1", "ExclusiveGateway_2",
                        "ServiceTask_2", "ExclusiveGateway_3",
                        "UserTask_3", "ExclusiveGateway_4", "ExclusiveGateway_5",
                        "ServiceTask_4", "EndEvent_1");  // Разобраться, как мокировать/отключать воркеры (ExternalTaskClient)
    }

    @Test
    public void happyPathIntrabank() {
        // Задаем межбанковский одобренный платеж с требующей проверки суммой
        startSteps(1001L, true, true, false);

        assertThat(processInstance).isWaitingAt("UserTask_3");
        completeTask().hasPassed("UserTask_3");
        assertThat(processInstance).isWaitingAtExactly("ServiceTask_5").externalTask().hasTopicName("intrabank");
        assertThat(processInstance).hasVariables("isInterbank");
        // Важно! В деплойментах по топику (intrabank) д.быть "чисто".
        // Иначе получим тут error ".. Multiple external tasks found for externalTask"
        complete(externalTask("ServiceTask_5", processInstance));

        assertThat(processInstance)
                .hasPassedInOrder("StartEvent_1", "ExclusiveGateway_1",
                        "UserTask_1", "ExclusiveGateway_2",
                        "ServiceTask_2", "ExclusiveGateway_3",
                        "UserTask_3", "ExclusiveGateway_4", "ExclusiveGateway_5",
                        "ServiceTask_5", "EndEvent_1");
    }

    @Test
    public void sadPathIntrabank() {
        // Задаем межбанковский НЕодобренный супервизором платеж с требующей проверки суммой
        startSteps(1001L, false, true, false);

        assertThat(processInstance)
                .hasPassedInOrder("StartEvent_1", "ExclusiveGateway_1",
                        "UserTask_1", "ExclusiveGateway_2",
                        "EndEvent_2");
    }

    @Test
    public void sadPathInterbank() {
        // Задаем внутрибанковский НЕодобренный финмониторингом платеж с требующей проверки "хорошей" суммой
        startSteps(1001L, true, false, true);

        assertThat(processInstance).isWaitingAt("UserTask_3");
        completeTask().hasPassed("UserTask_3");

        assertThat(processInstance)
                .hasPassedInOrder("StartEvent_1", "ExclusiveGateway_1",
                        "UserTask_1", "ExclusiveGateway_2",
                        "ServiceTask_2", "ExclusiveGateway_3",
                        "UserTask_3", "ExclusiveGateway_4", "EndEvent_3");  // Разобраться, как мокировать/отключать воркеры (ExternalTaskClient)
    }

    private ProcessInstanceAssert completeTask() {
        complete(task());
        return assertThat(processInstance);
    }
}