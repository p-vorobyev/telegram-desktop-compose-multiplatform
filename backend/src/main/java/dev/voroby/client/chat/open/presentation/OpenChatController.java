package dev.voroby.client.chat.open.presentation;

import dev.voroby.client.chat.common.application.ConvertToChatMessagesService;
import dev.voroby.client.chat.common.presentation.CommonChatController;
import dev.voroby.client.chat.open.application.GetChatHistoryService;
import dev.voroby.client.chat.open.application.OpenChatService;
import dev.voroby.client.chat.open.application.api.IsUserAdminInChannel;
import dev.voroby.client.chat.open.dto.ChatHistoryRequest;
import dev.voroby.client.chat.open.dto.ChatMessage;
import dev.voroby.springframework.telegram.client.TdApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @Slf4j
public class OpenChatController extends CommonChatController {

    private final OpenChatService openChatService;

    private final GetChatHistoryService getChatHistoryService;

    private final ConvertToChatMessagesService convertToChatMessagesService;

    private final IsUserAdminInChannel isUserAdminInChannel;

    public OpenChatController(OpenChatService openChatService,
                              GetChatHistoryService getChatHistoryService,
                              ConvertToChatMessagesService convertToChatMessagesService,
                              IsUserAdminInChannel isUserAdminInChannel) {
        this.openChatService = openChatService;
        this.getChatHistoryService = getChatHistoryService;
        this.convertToChatMessagesService = convertToChatMessagesService;
        this.isUserAdminInChannel = isUserAdminInChannel;
    }

    @PostMapping(value = "/open/{chatId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ChatMessage> openChat(@PathVariable("chatId") long chatId) {
        openChatService.accept(chatId);
        return loadChatHistory(new ChatHistoryRequest(chatId, 0, 0));
    }

    @PostMapping(value = "/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ChatMessage> loadChatHistory(@RequestBody ChatHistoryRequest chatHistoryRequest) {
        return getChatHistoryService
                .andThen(convertToChatMessagesService)
                .apply(chatHistoryRequest);
    }

    @GetMapping(value = "/incoming", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ChatMessage> getIncomingMessages() {
        log.debug("Poll incoming messages for chat");
        List<TdApi.Message> messages = openChatService.getIncomingMessages();
        return convertToChatMessagesService.apply(messages);
    }

    @GetMapping(value = "/edited", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ChatMessage> getUpdatedMessages() {
        log.debug("Poll edited messages for chat");
        List<TdApi.Message> messages = openChatService.getEditedMessages();
        return convertToChatMessagesService.apply(messages);
    }

    @GetMapping(value = "/deleted")
    public List<Long> getDeletedMsgIds() {
        log.debug("Poll deleted messages for chat");
        return openChatService.getDeletedMsgIds();
    }

    @GetMapping("/channel/isAdmin/{chatId}")
    public boolean isChannelAdmin(@PathVariable("chatId") long chatId) {
        return isUserAdminInChannel.apply(chatId);
    }
}
