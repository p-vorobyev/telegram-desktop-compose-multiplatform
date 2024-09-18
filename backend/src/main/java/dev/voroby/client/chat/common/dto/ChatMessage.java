package dev.voroby.client.chat.common.dto;

public record ChatMessage(
        long id,
        long chatId,
        boolean privateChat,
        TextContent textContent,
        EncodedContent encodedContent,
        UrlContent urlContent,
        String date,
        String editDate,
        String senderInfo,
        String senderPhoto,
        boolean isCurrentUser,
        boolean canBeDeletedForAllUsers,
        boolean canBeDeletedOnlyForSelf
) {

    public ChatMessage() {
        this(
                -1,
                -1,
                false,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                false,
                false
        );
    }

    public ChatMessage withId(long id) {
        return new ChatMessage(
                id,
                this.chatId,
                this.privateChat,
                this.textContent,
                this.encodedContent,
                this.urlContent,
                this.date,
                this.editDate,
                this.senderInfo,
                this.senderPhoto,
                this.isCurrentUser,
                this.canBeDeletedForAllUsers,
                this.canBeDeletedOnlyForSelf
        );
    }

    public ChatMessage withChatId(long chatId) {
        return new ChatMessage(
                this.id,
                chatId,
                this.privateChat,
                this.textContent,
                this.encodedContent,
                this.urlContent,
                this.date,
                this.editDate,
                this.senderInfo,
                this.senderPhoto,
                this.isCurrentUser,
                this.canBeDeletedForAllUsers,
                this.canBeDeletedOnlyForSelf
        );
    }

    public ChatMessage withPrivateChat(boolean privateChat) {
        return new ChatMessage(
                this.id,
                this.chatId,
                privateChat,
                this.textContent,
                this.encodedContent,
                this.urlContent,
                this.date,
                this.editDate,
                this.senderInfo,
                this.senderPhoto,
                this.isCurrentUser,
                this.canBeDeletedForAllUsers,
                this.canBeDeletedOnlyForSelf
        );
    }

    public ChatMessage withTextContent(TextContent textContent) {
        return new ChatMessage(
                this.id,
                this.chatId,
                this.privateChat,
                textContent,
                this.encodedContent,
                this.urlContent,
                this.date,
                this.editDate,
                this.senderInfo,
                this.senderPhoto,
                this.isCurrentUser,
                this.canBeDeletedForAllUsers,
                this.canBeDeletedOnlyForSelf
        );
    }

    public ChatMessage withEncodedContent(EncodedContent encodedContent) {
        return new ChatMessage(
                this.id,
                this.chatId,
                this.privateChat,
                this.textContent,
                encodedContent,
                this.urlContent,
                this.date,
                this.editDate,
                this.senderInfo,
                this.senderPhoto,
                this.isCurrentUser,
                this.canBeDeletedForAllUsers,
                this.canBeDeletedOnlyForSelf
        );
    }

    public ChatMessage withUrlContent(UrlContent urlContent) {
        return new ChatMessage(
                this.id,
                this.chatId,
                this.privateChat,
                this.textContent,
                this.encodedContent,
                urlContent,
                this.date,
                this.editDate,
                this.senderInfo,
                this.senderPhoto,
                this.isCurrentUser,
                this.canBeDeletedForAllUsers,
                this.canBeDeletedOnlyForSelf
        );
    }

    public ChatMessage withDate(String date) {
        return new ChatMessage(
                this.id,
                this.chatId,
                this.privateChat,
                this.textContent,
                this.encodedContent,
                this.urlContent,
                date,
                this.editDate,
                this.senderInfo,
                this.senderPhoto,
                this.isCurrentUser,
                this.canBeDeletedForAllUsers,
                this.canBeDeletedOnlyForSelf
        );
    }

    public ChatMessage withEditDate(String editDate) {
        return new ChatMessage(
                this.id,
                this.chatId,
                this.privateChat,
                this.textContent,
                this.encodedContent,
                this.urlContent,
                this.date,
                editDate,
                this.senderInfo,
                this.senderPhoto,
                this.isCurrentUser,
                this.canBeDeletedForAllUsers,
                this.canBeDeletedOnlyForSelf
        );
    }

    public ChatMessage withSenderInfo(String senderInfo) {
        return new ChatMessage(
                this.id,
                this.chatId,
                this.privateChat,
                this.textContent,
                this.encodedContent,
                this.urlContent,
                this.date,
                this.editDate,
                senderInfo,
                this.senderPhoto,
                this.isCurrentUser,
                this.canBeDeletedForAllUsers,
                this.canBeDeletedOnlyForSelf
        );
    }

    public ChatMessage withSenderPhoto(String senderPhoto) {
        return new ChatMessage(
                this.id,
                this.chatId,
                this.privateChat,
                this.textContent,
                this.encodedContent,
                this.urlContent,
                this.date,
                this.editDate,
                this.senderInfo,
                senderPhoto,
                this.isCurrentUser,
                this.canBeDeletedForAllUsers,
                this.canBeDeletedOnlyForSelf
        );
    }

    public ChatMessage withIsCurrentUser(boolean isCurrentUser) {
        return new ChatMessage(
                this.id,
                this.chatId,
                this.privateChat,
                this.textContent,
                this.encodedContent,
                this.urlContent,
                this.date,
                this.editDate,
                this.senderInfo,
                this.senderPhoto,
                isCurrentUser,
                this.canBeDeletedForAllUsers,
                this.canBeDeletedOnlyForSelf
        );
    }

    public ChatMessage withCanBeDeletedForAllUsers(boolean canBeDeletedForAllUsers) {
        return new ChatMessage(
                this.id,
                this.chatId,
                this.privateChat,
                this.textContent,
                this.encodedContent,
                this.urlContent,
                this.date,
                this.editDate,
                this.senderInfo,
                this.senderPhoto,
                this.isCurrentUser,
                canBeDeletedForAllUsers,
                this.canBeDeletedOnlyForSelf
        );
    }

    public ChatMessage withCanBeDeletedOnlyForSelf(boolean canBeDeletedOnlyForSelf) {
        return new ChatMessage(
                this.id,
                this.chatId,
                this.privateChat,
                this.textContent,
                this.encodedContent,
                this.urlContent,
                this.date,
                this.editDate,
                this.senderInfo,
                this.senderPhoto,
                this.isCurrentUser,
                this.canBeDeletedForAllUsers,
                canBeDeletedOnlyForSelf
        );
    }
}
