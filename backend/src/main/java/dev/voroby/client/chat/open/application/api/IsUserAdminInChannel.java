package dev.voroby.client.chat.open.application.api;

import dev.voroby.springframework.telegram.client.TelegramClient;
import dev.voroby.springframework.telegram.client.templates.response.Response;
import org.drinkless.tdlib.TdApi;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

@Component
public class IsUserAdminInChannel implements Function<Long, Boolean> {

    private final TelegramClient telegramClient;

    public IsUserAdminInChannel(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @Override
    public Boolean apply(Long chatId) {
        try {
            return telegramClient.sendAsync(new TdApi.GetChat(chatId))
                    .thenCompose(this::getAsSupergroup)
                    .thenApply(this::hasAdminRights)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException(e);
        }
    }

    private CompletableFuture<Response<TdApi.Supergroup>> getAsSupergroup(Response<TdApi.Chat> chatResponse) {
        if (chatResponse.getObject().isPresent() &&
                chatResponse.getObject().get().type instanceof TdApi.ChatTypeSupergroup supergroup) {
            return telegramClient.sendAsync(new TdApi.GetSupergroup(supergroup.supergroupId));
        } else {
            return CompletableFuture.completedFuture(new Response<>(null, new TdApi.Error()));
        }
    }

    private Boolean hasAdminRights(Response<TdApi.Supergroup> supergroupResponse) {
        return supergroupResponse.getObject()
                .map(supergroup -> switch (supergroup.status) {
                    case TdApi.ChatMemberStatusCreator creator -> true;
                    case TdApi.ChatMemberStatusAdministrator admin -> {
                        TdApi.ChatAdministratorRights rights = admin.rights;
                        yield rights.canPostMessages;
                    }
                    default -> false;
                }).orElse(false);
    }
}
