package com.iprody.payment.service.app.async;

// Интерфейс отправки сообщений для асинхронной обработки
public interface AsyncSender<T extends Message> {
    void send(T message);
}
