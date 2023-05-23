package org.example.zeebe.controller;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class Controller {

    private final ZeebeClient zeebeClient;

    @Autowired
    public Controller(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @GetMapping("/start")
    public long startProcess() {
        ProcessInstanceEvent processInstanceEvent = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("marketing_campaign_start")
                .latestVersion()
                .variables(Map.of("personIds", List.of(1, 2, 5)))
                .send()
                .join();
        final long processInstanceKey = processInstanceEvent.getProcessInstanceKey();
        System.out.println("ProcessInstanceKey " + processInstanceKey);
        return processInstanceKey;
    }

    @GetMapping("/send")
    public void sendMessage() {
        zeebeClient.newPublishMessageCommand()
                .messageName("test-message")
                .correlationKey("123")
                .send()
                .join();
    }

}
