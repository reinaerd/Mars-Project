package be.howest.ti.mars.web.bridge;

import be.howest.ti.mars.web.exceptions.MalformedRequestException;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.validation.RequestParameters;
import io.vertx.ext.web.validation.ValidationHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Request class is responsible for translating information that is part of the
 * request into Java.
 */
public class Request {
    private static final Logger LOGGER = Logger.getLogger(Request.class.getName());
    public static final String SPEC_MARS_ID = "mid";
    public static final String SPEC_CONTACT_ID = "contactid";
    public static final String SPEC_CHAT_ID = "chatid";
    public static final String SPEC_QUOTE = "quote";
    private final RequestParameters params;

    public static Request from(RoutingContext ctx) {
        return new Request(ctx);
    }

    private Request(RoutingContext ctx) {
        this.params = ctx.get(ValidationHandler.REQUEST_CONTEXT_KEY);
    }


    public int getMarsId(){
        return params.pathParameter(SPEC_MARS_ID).getInteger();
    }

    public int getContactId(){
        return params.pathParameter(SPEC_CONTACT_ID).getInteger();
    }

    public int getChatid(){
        return params.pathParameter(SPEC_CHAT_ID).getInteger();
    }

    public String getQuote() {
        try {
            if (params.body().isJsonObject())
                return params.body().getJsonObject().getString(SPEC_QUOTE);
            return params.body().get().toString();
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.INFO, "Unable to decipher the data in the body", ex);
            throw new MalformedRequestException("Unable to decipher the data in the request body. See logs for details.");
        }
    }
}
