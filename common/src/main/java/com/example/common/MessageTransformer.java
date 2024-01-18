package com.example.common;

import com.google.gson.Gson;
import ru.yoomoney.tech.dbqueue.api.TaskPayloadTransformer;

public class MessageTransformer implements TaskPayloadTransformer<MessageDto> {

    private final Gson gson = new Gson();

    private static final MessageTransformer INSTANCE = new MessageTransformer();

    public static MessageTransformer getInstance() {
        return INSTANCE;
    }

    private MessageTransformer() {
    }

    @Override
    public MessageDto toObject(String payload) {
        return gson.fromJson(payload, MessageDto.class);
    }

    @Override
    public String fromObject(MessageDto payload) {
        return gson.toJson(payload);
    }
}
