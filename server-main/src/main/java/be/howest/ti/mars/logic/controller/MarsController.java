package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.domain.Chat;
import be.howest.ti.mars.logic.domain.ChatMessage;
import be.howest.ti.mars.logic.domain.NotificationData;
import be.howest.ti.mars.logic.domain.User;

import java.util.List;

public interface MarsController {
    User createUser(int marsid);

    User getUser(int marsid);

    User getUserByContactid(int contactid);

    List<User> getContacts(int marsid);

    boolean addContact(int marsid, int contactid);

    boolean deleteContact(int marsid, int contactid);

    List<Chat> getChatids(int marsid);

    List<ChatMessage> getMessages(int marsid, int chatid);

    int addChatid(int marsid1, int marsid2);

    boolean addChatMessage(int chatid, int marsid, String content);

    void insertUserPushSubscription(int marsid, NotificationData subscription);

    NotificationData retrieveSubscriptionData(int marsid);
}
