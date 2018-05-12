package longshot.ws;

import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by Naiara on 12/09/2015.
 */
@Stateless
public class BroadcastFinder {

    public Broadcaster findAndAddBroadcaster(HttpServletRequest req, Long stageId, String braodcasterBaseUrl){
        AtmosphereResource r = (AtmosphereResource)
                req.getAttribute("org.atmosphere.cpr.AtmosphereResource");
        BroadcasterFactory broadcasterFactory = r.getAtmosphereConfig().getBroadcasterFactory();
        String broadcastId = braodcasterBaseUrl + stageId;
        Broadcaster broadcaster = broadcasterFactory.lookup(broadcastId);
        if(broadcaster == null){
            broadcaster = broadcasterFactory.get(broadcastId);
        }
        broadcaster.addAtmosphereResource(r);
        return broadcaster;
    }
}
