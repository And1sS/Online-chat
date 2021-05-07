package com.and1ss.onlinechat.repositories.projections;

import java.sql.Timestamp;

public interface GroupChatWithLastMessageProjection {
    String getChatId();

    String getChatTitle();

    String getChatAbout();

    String getChatCreatorId();

    String getChatCreatorName();

    String getChatCreatorSurname();

    String getChatCreatorLogin();

    String getLastMessageId();

    Timestamp getLastMessageCreationTime();

    String getLastMessageAuthorId();

    String getLastMessageContents();

    String getLastMessageAuthorName();

    String getLastMessageAuthorSurname();

    String getLastMessageAuthorLogin();
}
