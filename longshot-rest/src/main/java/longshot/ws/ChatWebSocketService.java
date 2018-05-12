package longshot.ws;

import org.atmosphere.cache.UUIDBroadcasterCache;
import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.config.service.AtmosphereService;
import org.atmosphere.config.service.Get;
import org.atmosphere.config.service.Post;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * Created by Naiara on 12/09/2015.
 */
@AtmosphereService(path="/ws/chat",
        broadcasterCache = UUIDBroadcasterCache.class,
        interceptors = { AtmosphereResourceLifecycleInterceptor.class,
                TrackMessageSizeInterceptor.class,
                HeartbeatInterceptor.class
        },
        servlet = "org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher")
@Path("/ws/chat")
@Stateless
public class ChatWebSocketService{

    public static final String WS_CHAT = "/ws/chat/";

    @Inject
    private BroadcastFinder finder;

    @GET
    @Get
    @Path("/{stageId}")
    public Response doGet(@PathParam("stageId") Long stageId, @Context HttpServletRequest req){
        this.finder.findAndAddBroadcaster(req, stageId, WS_CHAT);
        return Response.ok().build();
    }

    @POST
    @Post
    @Path("/{stageId}")
    public void broadcast(String message, @PathParam("stageId") Long stageId, @Context HttpServletRequest req) {
        Broadcaster broadcaster = this.finder.findAndAddBroadcaster(req, stageId, WS_CHAT);
        broadcaster.broadcast(message);
    }


}
