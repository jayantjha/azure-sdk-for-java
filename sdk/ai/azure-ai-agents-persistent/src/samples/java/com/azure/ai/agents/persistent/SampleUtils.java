package com.azure.ai.agents.persistent;

import com.azure.ai.agents.persistent.models.MessageContent;
import com.azure.ai.agents.persistent.models.MessageDeltaImageFileContent;
import com.azure.ai.agents.persistent.models.MessageDeltaTextContent;
import com.azure.ai.agents.persistent.models.MessageImageFileContent;
import com.azure.ai.agents.persistent.models.MessageTextContent;
import com.azure.ai.agents.persistent.models.OpenAIPageableListOfThreadMessage;
import com.azure.ai.agents.persistent.models.RunStatus;
import com.azure.ai.agents.persistent.models.ThreadMessage;
import com.azure.ai.agents.persistent.models.ThreadRun;
import com.azure.ai.agents.persistent.models.streaming.StreamMessageUpdate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class SampleUtils {

    public static void printRunMessages(MessagesClient messagesClient, String threadId) {

        // BEGIN: com.azure.ai.agents.persistent.SampleUtils.printRunMessages

        OpenAIPageableListOfThreadMessage runMessages = messagesClient.listMessages(threadId);
        for (ThreadMessage message : runMessages.getData()) {
            System.out.print(String.format("%1$s - %2$s : ", message.getCreatedAt(), message.getRole()));
            for (MessageContent contentItem : message.getContent()) {
                if (contentItem instanceof MessageTextContent) {
                    System.out.print((((MessageTextContent) contentItem).getText().getValue()));
                } else if (contentItem instanceof MessageImageFileContent) {
                    String imageFileId = (((MessageImageFileContent) contentItem).getImageFile().getFileId());
                    System.out.print("Image from ID: " + imageFileId);
                }
                System.out.println();
            }
        }

        // END: com.azure.ai.agents.persistent.SampleUtils.printRunMessages
    }

    public static Mono<Void> printRunMessagesAsync(MessagesAsyncClient messagesAsyncClient, String threadId) {
        // BEGIN: com.azure.ai.agents.persistent.SampleUtils.printRunMessagesAsync

        return messagesAsyncClient.listMessages(threadId)
            .flatMapMany(response -> Flux.fromIterable(response.getData()))
            .doOnNext(message -> {
                System.out.print(String.format("%1$s - %2$s : ", message.getCreatedAt(), message.getRole()));
                message.getContent().forEach(contentItem -> {
                    if (contentItem instanceof MessageTextContent) {
                        System.out.print((((MessageTextContent) contentItem).getText().getValue()));
                    } else if (contentItem instanceof MessageImageFileContent) {
                        String imageFileId = (((MessageImageFileContent) contentItem).getImageFile().getFileId());
                        System.out.print("Image from ID: " + imageFileId);
                    }
                    System.out.println();
                });
            })
            .then();

        // END: com.azure.ai.agents.persistent.SampleUtils.printRunMessagesAsync
    }


    public static void printStreamUpdate(StreamMessageUpdate messageUpdate) {

        // BEGIN: com.azure.ai.agents.persistent.SampleUtils.printStreamUpdate

        messageUpdate.getMessage().getDelta().getContent().stream().forEach(delta -> {
            if (delta instanceof MessageDeltaImageFileContent) {
                MessageDeltaImageFileContent imgContent = (MessageDeltaImageFileContent) delta;
                System.out.println("Image fileId: " + imgContent.getImageFile().getFileId());
            } else if (delta instanceof MessageDeltaTextContent) {
                MessageDeltaTextContent textContent = (MessageDeltaTextContent) delta;
                System.out.print(textContent.getText().getValue());
            }
        });

        // END: com.azure.ai.agents.persistent.SampleUtils.printStreamUpdate
    }

    public static Mono<Void> printStreamUpdateAsync(StreamMessageUpdate messageUpdate) {
        // BEGIN: com.azure.ai.agents.persistent.SampleUtils.printStreamUpdateAsync

        return Mono.fromRunnable(() -> {
            messageUpdate.getMessage().getDelta().getContent().stream().forEach(delta -> {
                if (delta instanceof MessageDeltaImageFileContent) {
                    MessageDeltaImageFileContent imgContent = (MessageDeltaImageFileContent) delta;
                    System.out.println("Image fileId: " + imgContent.getImageFile().getFileId());
                } else if (delta instanceof MessageDeltaTextContent) {
                    MessageDeltaTextContent textContent = (MessageDeltaTextContent) delta;
                    System.out.print(textContent.getText().getValue());
                }
            });
        });

        // END: com.azure.ai.agents.persistent.SampleUtils.printStreamUpdateAsync
    }

    public static void waitForRunCompletion(String threadId, ThreadRun threadRun, RunsClient runsClient)
        throws InterruptedException {

        // BEGIN: com.azure.ai.agents.persistent.SampleUtils.waitForRunCompletion

        do {
            Thread.sleep(500);
            threadRun = runsClient.getRun(threadId, threadRun.getId());
        }
        while (
            threadRun.getStatus() == RunStatus.QUEUED
                || threadRun.getStatus() == RunStatus.IN_PROGRESS
                || threadRun.getStatus() == RunStatus.REQUIRES_ACTION);

        if (threadRun.getStatus() == RunStatus.FAILED) {
            System.out.println(threadRun.getLastError().getMessage());
        }

        // END: com.azure.ai.agents.persistent.SampleUtils.waitForRunCompletion
    }

    public static Mono<ThreadRun> waitForRunCompletionAsync(String threadId, ThreadRun threadRun, RunsAsyncClient runsAsyncClient) {
        // BEGIN: com.azure.ai.agents.persistent.SampleUtils.waitForRunCompletionAsync

        return Mono.defer(() -> runsAsyncClient.getRun(threadId, threadRun.getId()))
            .flatMap(run -> {
                if (run.getStatus() == RunStatus.QUEUED
                    || run.getStatus() == RunStatus.IN_PROGRESS
                    || run.getStatus() == RunStatus.REQUIRES_ACTION) {
                    return Mono.delay(java.time.Duration.ofMillis(500))
                        .then(waitForRunCompletionAsync(threadId, run, runsAsyncClient));
                } else {
                    if (run.getStatus() == RunStatus.FAILED && run.getLastError() != null) {
                        System.out.println(run.getLastError().getMessage());
                    }
                    return Mono.just(run);
                }
            });

        // END: com.azure.ai.agents.persistent.SampleUtils.waitForRunCompletionAsync
    }
}
