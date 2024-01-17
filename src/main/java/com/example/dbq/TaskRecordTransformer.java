package com.example.dbq;

import com.google.gson.Gson;
import ru.yoomoney.tech.dbqueue.api.TaskPayloadTransformer;
import ru.yoomoney.tech.dbqueue.api.TaskRecord;

public class TaskRecordTransformer implements TaskPayloadTransformer<MessageDto> {

    private final Gson gson = new Gson();

    private static final TaskRecordTransformer INSTANCE = new TaskRecordTransformer();

    public static TaskRecordTransformer getInstance() {
        return INSTANCE;
    }

    private TaskRecordTransformer() {
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
