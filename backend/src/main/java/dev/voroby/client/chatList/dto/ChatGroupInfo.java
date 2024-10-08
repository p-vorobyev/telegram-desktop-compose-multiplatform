package dev.voroby.client.chatList.dto;

import org.drinkless.tdlib.TdApi;
import lombok.Data;

@Data
public class ChatGroupInfo {

    private volatile long chatId;

    private volatile TdApi.BasicGroup basicGroup;

    private volatile TdApi.BasicGroupFullInfo basicGroupFullInfo;

    private volatile TdApi.Supergroup supergroup;

    private volatile TdApi.SupergroupFullInfo supergroupFullInfo;

}
