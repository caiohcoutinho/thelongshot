package longshot.rest;

import longshot.model.CustomGson;
import longshot.model.Player;
import longshot.model.Stage;
import longshot.ws.RequestHelper;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * Created by Naiara on 12/09/2015.
 */
@Stateless
@Path("/battle")
public class BattleRestService {

    @Inject
    private RequestHelper requestHelper;

    @PersistenceContext
    private EntityManager em;

    @GET
    @Path("/game")
    @Produces("application/json")
    public Response game(@Context HttpServletRequest request){
        Long stageId = this.requestHelper.getStageId(request);
        String userId = this.requestHelper.getUserId(request);
        Stage stage = this.em.find(Stage.class, stageId);
        for(Player p : stage.getPlayers()){
            if(p.getUserId().equals(userId)){
                stage.setSelectedPlayer(p);
                return Response.ok().entity(CustomGson.GSON.toJson(stage)).build();
            }
        }
        return Response.seeOther(URI.create("../stage.html")).build();
    }


    @GET
    @Path("/{stageId}")
    @Produces("text/html")
    public Response joinStage(@PathParam("stageId") Long stageId, @Context HttpServletRequest request){
        Stage stage = this.em.find(Stage.class, stageId);
        String userId = this.requestHelper.getUserId(request);
        this.requestHelper.setStageId(request, stageId);
        for(Player p : stage.getPlayers()){
            if(p.getUserId().equals(userId)){
                return Response.seeOther(URI.create("../game.html")).build();
            }
        }
        return Response.seeOther(URI.create("../stage.html")).build();
    }

}
