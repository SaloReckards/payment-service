package com.iprody.xpaymentadapterapp.async;

// Интерфейс обработчика входящих сообщений
public interface MessageHandler<T extends Message> {
    void handle(T message);
}
