package com.iprody.xpaymentadapterapp.async;

// Интерфейс слушателя входящих сообщений
public interface AsyncListener <T extends Message> {
    void onMessage(T message);
}
