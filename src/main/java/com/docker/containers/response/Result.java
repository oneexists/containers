package com.docker.containers.response;

import java.util.ArrayList;
import java.util.List;

public class Result<T> {
    private ActionStatus status = ActionStatus.SUCCESS;
    private List<String> messages = new ArrayList<>();
    private T payload;

    public void addMessage(ActionStatus status, String message) {
        this.status = status;
        messages.add(message);
    }

    public boolean isSuccess() {
        return messages.isEmpty();
    }

    public ActionStatus getStatus() {
        return status;
    }

    public List<String> getMessages() {
        return new ArrayList<>(messages);
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
