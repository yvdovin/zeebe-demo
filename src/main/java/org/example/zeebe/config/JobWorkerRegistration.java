package org.example.zeebe.config;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class JobWorkerRegistration {

    private final ZeebeClient zeebeClient;

    private List<JobWorker> workerList = new ArrayList<>();

    @Autowired
    public JobWorkerRegistration(ZeebeClient zeebeClient) {
        this.zeebeClient = zeebeClient;
    }

    @PostConstruct
    public void init() {
        final AtomicInteger count = new AtomicInteger(0);
        final JobWorker worker = zeebeClient.newWorker()
                .jobType("payment-service")
                .handler(((client, job) -> {
                    job.getBpmnProcessId();
                    String deliveryType;
                    if (count.get() % 2 == 0) {
                        deliveryType = "regular";
                        count.incrementAndGet();
                    } else {
                        deliveryType = "post";
                    }
                    System.out.println("Collecting money $$$$$");
                    client.newCompleteCommand(job.getKey())
                            .variables(Map.of("", deliveryType,
                                    "orderId", 123))
                            .send()
                            .join();
                }))
                .open();
        workerList.add(worker);

        final JobWorker deliveryWorker = zeebeClient.newWorker()
                .jobType("delivery-service")
                .handler(((client, job) -> {
                    System.out.println("Delivering order...");
                    client.newCompleteCommand(job.getKey())
                            .send()
                            .join();
                }))
                .open();
        workerList.add(deliveryWorker);

        final JobWorker postWorker = zeebeClient.newWorker()
                .jobType("post-service")
                .handler(((client, job) -> {
                    System.out.println("Posting order...");
                    client.newCompleteCommand(job.getKey())
                            .send()
                            .join();
                }))
                .open();
        workerList.add(postWorker);

        final JobWorker printIdWorker = zeebeClient.newWorker()
                .jobType("print-id")
                .handler(((client, job) -> {
                    int personIdInService = (Integer) job.getVariablesAsMap().get("personIdInService");
                    System.out.printf("Person id: %d \n", personIdInService);
                    client.newCompleteCommand(job.getKey())
                            .send()
                            .join();
                }))
                .open();
        workerList.add(printIdWorker);

        final JobWorker getPersonIdsWorker = zeebeClient.newWorker()
                .jobType("get-person-ids")
                .handler(((client, job) -> {
                    System.out.println("Create personsIds ...");
                    client.newCompleteCommand(job.getKey())
                            .variables(Map.of("personIdsFromWorker", List.of(1, 3, 4, 6, 9)))
                            .send()
                            .join();
                }))
                .open();
        workerList.add(getPersonIdsWorker);

        createMarketingCampaignSendBatchEmailWorker();
        createMarketingCampaignBatchEmailStatusWorker();
        createMarketingCampaignSendSmsWorker();
        createMarketingCampaignCallCenterWorker();
    }

    public void createMarketingCampaignSendBatchEmailWorker() {
        final JobWorker createMarketingCampaignSendBatchEmailWorker = zeebeClient.newWorker()
                .jobType("marketing-campaign-send-batch-email")
                .handler(((client, job) -> {
                    System.out.println("Start send email to 1,2,5");
                    client.newCompleteCommand(job.getKey())
                            .send()
                            .join();
                }))
                .open();
        workerList.add(createMarketingCampaignSendBatchEmailWorker);
    }

    public void createMarketingCampaignBatchEmailStatusWorker() {
        final JobWorker createMarketingCampaignSendBatchEmailWorker = zeebeClient.newWorker()
                .jobType("marketing-campaign-batch-email-statuses")
                .handler(((client, job) -> {
                    client.newCompleteCommand(job.getKey())
                            .variables( Map.of("email_delivery_statuses",
                                    Map.of("email_delivery_status_1", "OPEN_LINK",
                                    "email_delivery_status_2", "NOT_OPENED",
                                    "email_delivery_status_5", "ERROR")))
                            .send()
                            .join();
                }))
                .open();
        workerList.add(createMarketingCampaignSendBatchEmailWorker);
    }

    public void createMarketingCampaignSendSmsWorker() {
        final JobWorker createMarketingCampaignSendBatchEmailWorker = zeebeClient.newWorker()
                .jobType("send-sms")
                .handler(((client, job) -> {
                    Integer personId = (Integer) job.getVariablesAsMap().get("personId");
                    System.out.println("Send sms " + personId);
                    client.newCompleteCommand(job.getKey())
                            .send()
                            .join();
                }))
                .open();
        workerList.add(createMarketingCampaignSendBatchEmailWorker);
    }

    public void createMarketingCampaignCallCenterWorker() {
        final JobWorker createMarketingCampaignSendBatchEmailWorker = zeebeClient.newWorker()
                .jobType("call-center")
                .handler(((client, job) -> {
                    Integer personId = (Integer) job.getVariablesAsMap().get("personId");
                    System.out.println("Send to call center " + personId);
                    client.newCompleteCommand(job.getKey())
                            .send()
                            .join();
                }))
                .open();
        workerList.add(createMarketingCampaignSendBatchEmailWorker);
    }
}
