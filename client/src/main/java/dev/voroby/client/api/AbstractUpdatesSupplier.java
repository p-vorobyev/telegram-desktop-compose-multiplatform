package dev.voroby.client.api;

import dev.voroby.client.updates.UpdatesQueues;
import dev.voroby.springframework.telegram.client.TelegramClient;

import java.util.function.Supplier;

abstract public class AbstractUpdatesSupplier<T> implements Supplier<T> {

    final UpdatesQueues updatesQueues;

    final TelegramClient telegramClient;

    protected AbstractUpdatesSupplier(UpdatesQueues updatesQueues, TelegramClient telegramClient) {
        this.updatesQueues = updatesQueues;
        this.telegramClient = telegramClient;
    }

}
