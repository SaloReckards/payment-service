package com.iprody.payment.service.app.async;

// Интерфейс слушателя входящих сообщений
public interface AsyncListener <T extends Message> {
    void onMessage(T message);
}
