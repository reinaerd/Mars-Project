package be.howest.ti.mars.logic.domain;

import be.howest.ti.mars.logic.controller.DefaultMarsController;
import be.howest.ti.mars.logic.controller.MarsController;
import be.howest.ti.mars.logic.domain.events.*;
import be.howest.ti.mars.logic.util.Config;
import io.vertx.core.json.JsonObject;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Chatroom {

    private static final Chatroom instance = new Chatroom();
    private static final Logger LOGGER = Logger.getLogger(Chatroom.class.getName());
    private static final String PRIVATE_VAPID_KEY = Config.getInstance().readSetting("push.private.key");
    private static final String PUBLIC_VAPID_KEY = Config.getInstance().readSetting("push.public.key");
    private static PushService pushService;

    public static Chatroom getInstance(){
        Security.addProvider(new BouncyCastleProvider());
        try{
            pushService = new PushService(PUBLIC_VAPID_KEY,PRIVATE_VAPID_KEY);
        }catch(GeneralSecurityException e) {
            LOGGER.log(Level.SEVERE,"Could not create pushservice",e);
        }
        return instance;
    }

    private static final MarsController controller = new DefaultMarsController();

    public OutgoingEvent handleEvent(IncomingEvent e){
        OutgoingEvent outgoingEvent = null;
        switch(e.getType()){
            case MESSAGE:
                outgoingEvent = handlePublicMessageEvent((MessageEvent) e);
                break;
            case PRIVATEMESSAGE:
                outgoingEvent = handlePrivateMessageEvent((PrivateMessageEvent) e);
                break;
            case REQUEST:
                outgoingEvent = handleChatRequest((ChatRequestEvent) e);
                break;
            case SUBSCRIPTION:
                storeUserSubscriptionInDatabase((SubscriptionEvent) e);
                break;
            default:
                break;
        }
        return outgoingEvent;
    }

    private OutgoingEvent handlePublicMessageEvent(MessageEvent e){
        String outgoingMessage = String.format("%s: %s",controller.getUser(e.getMarsid()).getName(), e.getMessage());
        return EventFactory.getInstance().createBroadcastEvent(outgoingMessage);
    }

    private OutgoingEvent handlePrivateMessageEvent(PrivateMessageEvent e){
        String outgoingMessage = String.format("%s: %s", controller.getUser(e.getMarsid()).getName(), e.getMessage());
        storeMessageInDatabase(e);
        return EventFactory.getInstance().createMulticastEvent(outgoingMessage, Integer.parseInt(e.getChatid()));
    }

    private OutgoingEvent handleChatRequest(ChatRequestEvent e){
        int sendermid = e.getMarsid();
        int receivercontactid = e.getReceivercontactid();
        int receivermid = controller.getUserByContactid(receivercontactid).getMarsid();
        int value = e.getValue();
        UnicastEvent response = null;
        //first check if they already have a chat, if so just discard it
        for(Chat chat : controller.getChatids(sendermid)){
            for(Chat chat2 : controller.getChatids(receivermid)){
                if(chat.getChatid() == chat2.getChatid()){
                    return null;
                }
            }
        }
        switch(value){
            case 0:
                sendChatRequestNotification(e);
                //send request to receiver
                break;
            case 1:
                //receiver responds with yes => send the chatid to both sender and receiver
                int chatid = controller.addChatid(sendermid,receivermid); // => retrieve the chatid from this function
                response = EventFactory.getInstance().createUnicastEvent(controller.getUser(sendermid),receivermid,value,chatid);//
                break;
            default:
                break;
        }
        return response;
    }

    public void storeMessageInDatabase(PrivateMessageEvent e){
        int chatid = Integer.parseInt(e.getChatid());
        int marsid = e.getMarsid();
        String messageContents = e.getMessage();
        controller.addChatMessage(chatid, marsid, messageContents);
    }

    public void storeUserSubscriptionInDatabase(SubscriptionEvent e){
        controller.insertUserPushSubscription(e.getMarsid(),e.getData());
    }

    public void sendChatRequestNotification(ChatRequestEvent e) {
        //get the receivers notification data, and send them a request
        int receivermid = controller.getUserByContactid(e.getReceivercontactid()).getMarsid();
        User sender = controller.getUser(e.getMarsid());
        NotificationData receiverPushData = controller.retrieveSubscriptionData(receivermid);
        JsonObject obj = new JsonObject();
        obj.put("type","chatrequest");
        obj.put("sendername", sender.getName());
        obj.put("sendermid",sender.getMarsid());
        obj.put("receivermid",receivermid);
        obj.put("answer",0);
        try{
            Notification notification = new Notification(receiverPushData.getEndpoint(), receiverPushData.getUserkey(), receiverPushData.getAuth(), obj.toString());
            pushService.send(notification);
        } catch (JoseException | GeneralSecurityException | IOException | ExecutionException | InterruptedException ex) {
            LOGGER.log(Level.SEVERE,"Something went wrong sending a message to the client:", ex);
            Thread.currentThread().interrupt();
        }
    }
}
