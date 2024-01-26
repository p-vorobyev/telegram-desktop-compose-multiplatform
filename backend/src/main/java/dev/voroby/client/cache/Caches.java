package dev.voroby.client.cache;

import dev.voroby.client.dto.ChatGroupInfo;
import dev.voroby.springframework.telegram.client.TdApi;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public final class Caches {

    public final static Set<Long> mainListChatIds = new CopyOnWriteArraySet<>();

    public final static Map<Long, String> chatIdToPhotoCache = new ConcurrentHashMap<>();

    public final static Map<Long, TdApi.Chat> initialChatCache = new ConcurrentHashMap<>();

    public final static Map<Long, Long> chatIdToGroupIdCache = new ConcurrentHashMap<>();

    public final static Map<Long, ChatGroupInfo> groupIdToGroupInfoCache = new ConcurrentHashMap<>();

    public final static Map<Long, TdApi.User> userIdToUserCache = new ConcurrentHashMap<>();

    public final static Map<Integer, Long> photoIdToChatIdCache = new ConcurrentHashMap<>();

    public final static Map<Integer, Long> profilePhotoIdToUserIdCache = new ConcurrentHashMap<>();

    public final static Map<Long, String> userIdToProfilePhotoCache = new ConcurrentHashMap<>();

    private Caches() {}

}