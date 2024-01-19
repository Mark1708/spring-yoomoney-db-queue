package com.example.common.transformer;

import com.example.common.MessageDto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yoomoney.tech.dbqueue.api.TaskPayloadTransformer;

import java.time.LocalDateTime;

public class MessageTransformer implements TaskPayloadTransformer<MessageDto> {

    private final Gson gson;

    private static final MessageTransformer INSTANCE = new MessageTransformer();

    public static MessageTransformer getInstance() {
        return INSTANCE;
    }

    private MessageTransformer() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());

        gson = gsonBuilder.create();
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
