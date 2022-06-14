package be.howest.ti.mars.logic.domain.events;

import be.howest.ti.mars.logic.domain.User;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class EventsTest {

    private EventFactory getEventFactory(){
        return new EventFactory();
    }

    private JsonObject createbasicjsonobject(){
        JsonObject obj = new JsonObject();
        obj.put("marsid", 1);
        obj.put("message","test");
        return obj;
    }

    @Test
    void testCreateMessageEvent(){

        JsonObject obj = createbasicjsonobject();
        obj.put("type","message");
        IncomingEvent event = getEventFactory().createIncomingEvent(obj);
        assertEquals( MessageEvent.class,event.getClass());
        assertEquals(EventType.MESSAGE,event.getType());
        assertEquals(1,event.getMarsid());
    }

    @Test
    void testDiscardEvent(){
        JsonObject obj = createbasicjsonobject();
        obj.put("type", "");
        IncomingEvent event = getEventFactory().createIncomingEvent(obj);
        assertEquals(DiscardEvent.class, event.getClass());
        assertEquals(EventType.DISCARD,event.getType());
        assertEquals(1,event.getMarsid());
    }

    @Test
    void testDiscardEvent2(){
        JsonObject obj = new JsonObject();
        obj.put("sendermid",1);
        IncomingEvent event = getEventFactory().createIncomingEvent(obj);
        assertEquals(DiscardEvent.class, event.getClass());
        assertEquals(EventType.DISCARD,event.getType());
        assertEquals(1,event.getMarsid());
    }

    @Test
    void testChatRequestEvent(){
        JsonObject obj = createbasicjsonobject();
        obj.put("type","chatrequest");
        obj.put("sendermid", 1);
        obj.put("receivercontactid", 2);
        obj.put("answer", 0);
        ChatRequestEvent event = (ChatRequestEvent) getEventFactory().createIncomingEvent(obj);
        assertEquals(ChatRequestEvent.class,event.getClass());
        assertEquals(EventType.REQUEST,event.getType());
        assertEquals(1,event.getMarsid());
        assertEquals(2,event.getReceivercontactid());
        assertEquals(0,event.getValue());
    }

    @Test
    void testPrivateMessageEvent(){
        JsonObject obj = createbasicjsonobject();
        obj.put("type", "privatemessage");
        obj.put("chatid", 1);
        PrivateMessageEvent event = (PrivateMessageEvent) getEventFactory().createIncomingEvent(obj);
        assertEquals(PrivateMessageEvent.class,event.getClass());
        assertEquals(EventType.PRIVATEMESSAGE,event.getType());
        assertEquals(1,event.getMarsid());
        assertEquals("test",event.getMessage());
        assertEquals("1",event.getChatid());
    }

    @Test
    void testOutgoingEvent(){
        BroadcastEvent event = getEventFactory().createBroadcastEvent("hello");
        assertEquals(BroadcastEvent.class, event.getClass());
        assertEquals(EventType.BROADCAST,event.getType());
        assertEquals("hello",event.getMessage());
    }

    @Test
    void testMulticastEvent(){
        MulticastEvent event = getEventFactory().createMulticastEvent("hello there", 2);
        assertEquals(MulticastEvent.class, event.getClass());
        assertEquals(EventType.MULTICAST,event.getType());
        assertEquals(2, event.getChatid());
        assertEquals("hello there", event.getMessage());
    }

    @Test
    void testUnicastEvent(){
        UnicastEvent event = getEventFactory().createUnicastEvent(new User(1,"Michael"), 2,1,1);
        assertEquals(UnicastEvent.class,event.getClass());
        assertEquals("Michael",event.getSendername());
        assertEquals(1,event.getSendermid());
        assertEquals(2, event.getReceivermid());
        assertEquals(1,event.getValue());
        assertEquals(1,event.getChatid());
    }

    @Test
    void testSubscriptionEvent(){
        JsonObject obj = new JsonObject();
        JsonObject keys = new JsonObject();
        keys.put("p256dh","a");
        keys.put("auth","b");
        obj.put("endpoint","test");
        obj.put("keys",keys);
        JsonObject finalObj = createbasicjsonobject();
        finalObj.put("type","subscription");
        finalObj.put("subscription",obj);
        SubscriptionEvent event = (SubscriptionEvent)getEventFactory().createIncomingEvent(finalObj);
        assertEquals(EventType.SUBSCRIPTION,event.getType());
        assertEquals(1, event.getMarsid());
        assertEquals("a", event.getData().getUserkey());
        assertEquals("b",event.getData().getAuth());
        assertEquals("test",event.getData().getEndpoint());
    }


}
