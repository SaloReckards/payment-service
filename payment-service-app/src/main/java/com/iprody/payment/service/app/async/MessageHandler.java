package com.iprody.payment.service.app.async;

// Интерфейс обработчика входящих сообщений
public interface MessageHandler<T extends Message> {
    void handle(T message);
}
