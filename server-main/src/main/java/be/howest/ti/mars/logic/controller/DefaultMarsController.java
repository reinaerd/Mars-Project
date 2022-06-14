package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.domain.Chat;
import be.howest.ti.mars.logic.domain.ChatMessage;
import be.howest.ti.mars.logic.domain.NotificationData;
import be.howest.ti.mars.logic.domain.User;
import io.vertx.core.json.JsonObject;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * DefaultMarsController is the default implementation for the MarsController interface.
 * It should NOT be aware that it is used in the context of a webserver:
 * <p>
 * This class and all other classes in the logic-package (or future sub-packages)
 * should use 100% plain old Java Objects (POJOs). The use of Json, JsonObject or
 * Strings that contain encoded/json data should be avoided here.
 * Do not be afraid to create your own Java classes if needed.
 * <p>
 * Note: Json and JsonObject can (and should) be used in the web-package however.
 * <p>
 * (please update these comments in the final version)
 */
public class DefaultMarsController implements MarsController {
    private final Random rand = new SecureRandom();

    //create a user and put them in the database
    @Override
    public User createUser(int marsid){
        //create user without contactid, pass to h2repo and return user with contactid in database
        return Repositories.getH2Repo().createUser(new User(marsid, getRandomName()));
    }

    @Override
    public User getUser(int marsid){
        return Repositories.getH2Repo().getUser(marsid);
    }

    @Override
    public User getUserByContactid(int contactid) {
        return Repositories.getH2Repo().getUserByContactid(contactid);
    }

    private String getRandomName(){
        //since your marsid has your real name linked to it, we
        List<String> nameFaker = new ArrayList<>();
        nameFaker.add("Stijn");
        nameFaker.add("Bilal");
        nameFaker.add("Nicolas");
        nameFaker.add("Reinaerd");
        nameFaker.add("Eduardo");
        nameFaker.add("Billy");
        nameFaker.add("Charlie");
        nameFaker.add("Marc");
        nameFaker.add("Jill");
        nameFaker.add("Mathias");
        nameFaker.add("Zorro");
        nameFaker.add("Thomas");
        nameFaker.add("Jonas");
        nameFaker.add("Damiano");
        nameFaker.add("Vicky");
        nameFaker.add("Patrick");
        nameFaker.add("Ezekiel");
        nameFaker.add("Bobby");
        nameFaker.add("James");
        nameFaker.add("Emma");
        nameFaker.add("Emily");
        nameFaker.add("Rob");
        nameFaker.add("Ben");
        nameFaker.add("Thor");
        nameFaker.add("Lando");
        nameFaker.add("Alvin");
        int randnr = rand.nextInt(26);
        return nameFaker.get(randnr);
    }

    @Override
    public List<User> getContacts(int marsid){
        return Repositories.getH2Repo().getContacts(marsid);
    }

    @Override
    public boolean addContact(int marsid, int contactid){
        User user1 = getUser(marsid);
        User user2 = getUserByContactid(contactid);
        return Repositories.getH2Repo().addContact(marsid, contactid) && Repositories.getH2Repo().addContact(user2.getMarsid(), user1.getContactid());
    }

    @Override
    public boolean deleteContact(int marsid, int contactid){
        User user1 = getUser(marsid);
        User user2 = getUserByContactid(contactid);
        return Repositories.getH2Repo().deleteContact(marsid, contactid) && Repositories.getH2Repo().deleteContact(user2.getMarsid(), user1.getContactid());
    }

    @Override
    public List<Chat> getChatids(int marsid){
        return Repositories.getH2Repo().getChatids(marsid);
    }

    @Override
    public List<ChatMessage> getMessages(int marsid, int chatid) {
        return Repositories.getH2Repo().getMessages(marsid, chatid);
    }

    @Override
    public int addChatid(int marsid1, int marsid2) {
        return Repositories.getH2Repo().createChat(marsid1, marsid2);
    }

    @Override
    public boolean addChatMessage(int chatid, int marsid, String content) {
        return Repositories.getH2Repo().insertChatMessage(chatid, marsid, content);
    }

    @Override
    public void insertUserPushSubscription(int marsid, NotificationData subscription) {
        Repositories.getH2Repo().insertUserPushSubscription(marsid, subscription);
    }

    @Override
    public NotificationData retrieveSubscriptionData(int marsid) {
        return Repositories.getH2Repo().retrieveSubscriptionDataWithMarsID(marsid);
    }
}