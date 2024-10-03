package dev.voroby.client.chat.convertChatMessage.application.api;

import dev.voroby.client.chat.common.dto.ChatMessage;
import dev.voroby.client.chat.common.dto.MessageGifAnimationInfo;
import dev.voroby.client.chat.common.dto.UrlContent.GifFile;
import dev.voroby.client.chat.convertChatMessage.dto.ConvertChatMessageContext;
import dev.voroby.springframework.telegram.client.TdApi;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class FillUrlContent implements Function<ConvertChatMessageContext, ConvertChatMessageContext> {

    private final GetGifAnimationUrl getGifAnimationUrl;

    public FillUrlContent(GetGifAnimationUrl getGifAnimationUrl) {
        this.getGifAnimationUrl = getGifAnimationUrl;
    }

    @Override
    public ConvertChatMessageContext apply(ConvertChatMessageContext convertChatMessageContext) {
        TdApi.Message message = convertChatMessageContext.message();
        if (message.content instanceof TdApi.MessageAnimation msgAnimation) {
            ChatMessage chatMessage = chatMessageWithGif(convertChatMessageContext, msgAnimation, message);
            return new ConvertChatMessageContext(message, chatMessage);
        } else return convertChatMessageContext;
    }

    private ChatMessage chatMessageWithGif(ConvertChatMessageContext convertChatMessageContext,
                                           TdApi.MessageAnimation messageAnimation,
                                           TdApi.Message message) {
        var messageGifAnimationInfo = new MessageGifAnimationInfo(message.id, message.chatId, messageAnimation);
        GifFile gifFile = getGifAnimationUrl.apply(messageGifAnimationInfo);
        return convertChatMessageContext.chatMessage().withUrlContent(gifFile);
    }
}
