package dev.sleepy_evelyn.create_configured.trains;

import com.google.gson.JsonObject;
import com.simibubi.create.content.trains.schedule.ScheduleEntry;
import com.simibubi.create.content.trains.schedule.destination.ChangeThrottleInstruction;
import com.simibubi.create.content.trains.schedule.destination.DestinationInstruction;
import dev.sleepy_evelyn.create_configured.CreateConfigured;
import dev.sleepy_evelyn.create_configured.config.CCConfigs;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public record DiscordSchedule(@NotNull ServerPlayer requester, List<ScheduleEntry> scheduleEntries) {

    public boolean send() {
        Optional<URI> webhookUri = getWebhookUri();
        if (webhookUri.isEmpty()) return false;

        JsonObject testObject = new JsonObject();
        testObject.addProperty("content", "Testing 123 :3");

        try (var httpClient = HttpClient.newHttpClient()) {
            String scheduleMessage = newScheduleMessage();
            var httpRequest = HttpRequest.newBuilder(webhookUri.get())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(testObject.toString()))
                    .build();

            httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
                    .whenCompleteAsync((response, exception) -> {
                        if (response.statusCode() == 200 || response.statusCode() == 204)
                            CreateConfigured.logger.info("Successfully sent payload and got response {}", response.body());
                    });
        }
        return true;
    }

    private Optional<URI> getWebhookUri() {
        if (!CreateConfigured.isDedicatedServer() || CCConfigs.secrets().isEmpty()) return Optional.empty();

        var webhookConfigValue = CCConfigs.secrets().get().scheduleRequestsDiscordWebhookURL;
        if (webhookConfigValue.get().equals(webhookConfigValue.getDefault())) return Optional.empty();

        return Optional.of(URI.create(webhookConfigValue.get()));
    }

    private String newScheduleMessage() {
        String requesterUsername = requester.getName().getString();
        Queue<String> scheduleEntriesMessage = new ArrayDeque<>();

        for (var scheduleEntry : scheduleEntries) {
            var instruction = scheduleEntry.instruction;

            if (instruction instanceof DestinationInstruction di) {
                String filter = di.getFilter();
            } else if (instruction instanceof ChangeThrottleInstruction cti) {
                float throttle = cti.getThrottle();
            }
        }
        return "Bing Bong!";
    }

}
