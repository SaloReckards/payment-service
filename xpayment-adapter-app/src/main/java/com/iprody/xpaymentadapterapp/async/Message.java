package com.iprody.xpaymentadapterapp.async;

import java.time.OffsetDateTime;
import java.util.UUID;

// Интерфейс, представляющий сообщение с уникальным идентификатором и временем возникновения
public interface Message {
    UUID getMessageId();

    OffsetDateTime getOccurredAt();
}
