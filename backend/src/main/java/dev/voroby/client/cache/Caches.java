package dev.voroby.client.cache;

import dev.voroby.client.chatList.dto.ChatGroupInfo;
import dev.voroby.client.chat.common.dto.MessageId;
import org.drinkless.tdlib.TdApi;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public final class Caches {

    public final static AtomicLong requestsCount = new AtomicLong();

    public final static AtomicReference<Long> openedChat = new AtomicReference<>(null);

    public final static AtomicBoolean initialApplicationLoadDone = new AtomicBoolean(false);

    public final static Set<Long> mainListChatIds = new CopyOnWriteArraySet<>();

    public final static Map<Long, String> chatIdToPhotoCache = new ConcurrentHashMap<>();

    public final static Map<Long, TdApi.Chat> initialChatCache = new ConcurrentHashMap<>();

    public final static Map<Long, Long> chatIdToGroupIdCache = new ConcurrentHashMap<>();

    public final static Map<Long, ChatGroupInfo> groupIdToGroupInfoCache = new ConcurrentHashMap<>();

    public final static Map<Long, TdApi.User> userIdToUserCache = new ConcurrentHashMap<>();

    public final static Map<Integer, Long> photoIdToChatIdCache = new ConcurrentHashMap<>();

    public final static Map<Integer, Long> profilePhotoIdToUserIdCache = new ConcurrentHashMap<>();

    public final static Map<Long, String> userIdToProfilePhotoCache = new ConcurrentHashMap<>();

    public final static Map<Long, Integer> messageIdToPhotoPreviewIdCache = new ConcurrentHashMap<>();

    public final static Map<Integer, MessageId> photoPreviewIdToMessageIdCache = new ConcurrentHashMap<>();

    public final static Map<Long, Integer> messageIdToGifAnimationIdCache = new ConcurrentHashMap<>();

    public final static Map<Integer, MessageId> gifAnimationIdToMessageIdCache = new ConcurrentHashMap<>();

    private Caches() {}

}