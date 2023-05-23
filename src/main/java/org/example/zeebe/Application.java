package org.example.zeebe;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.worker.JobWorker;
//import io.zeebe.client.ZeebeClient;
//import io.zeebe.client.api.response.DeploymentEvent;
//import io.zeebe.client.api.response.ProcessInstanceEvent;
//import io.zeebe.client.api.worker.JobWorker;

public class Application {

    public static void main(String[] args) {
        final ZeebeClient zeebeClient = ZeebeClient.newClientBuilder()
                .gatewayAddress("127.0.0.1:26500")
                //Без этой настройки падал SSL
                .usePlaintext()
                .build();

        System.out.println("Connected...");

        //deployProcess(zeebeClient);

        //startProcess(zeebeClient);

        //createWorker(zeebeClient);
        //worker.close();
        //zeebeClient.close();
        //System.out.println("Disconnected");

    }


    private static void deployProcess(ZeebeClient zeebeClient) {
        final DeploymentEvent deploymentEvent = zeebeClient.newDeployResourceCommand()
                .addResourceFromClasspath("test.bpmn")
                .send().join();

        final int version = deploymentEvent.getProcesses().get(0).getVersion();
        System.out.println("Deployment version " + version);
    }

    private static void startProcess(ZeebeClient zeebeClient) {
        ProcessInstanceEvent processInstanceEvent = zeebeClient.newCreateInstanceCommand()
                .bpmnProcessId("order-process")
                .latestVersion()
                .send()
                .join();

        final long processInstanceKey = processInstanceEvent.getProcessInstanceKey();

        System.out.println("ProcessInstanceKey " + processInstanceKey);
    }

    private static void createWorker(ZeebeClient zeebeClient) {
        final JobWorker worker = zeebeClient.newWorker()
                .jobType("payment-service")
                .handler(((client, job) -> {
                    System.out.println("Collecting money $$$$$");
                    client.newCompleteCommand(job.getKey())
                            .send()
                            .join();
                }))
                .open();
    }
}
