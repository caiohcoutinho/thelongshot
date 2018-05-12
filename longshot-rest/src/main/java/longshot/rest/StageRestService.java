package longshot.rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import longshot.model.CustomGson;
import longshot.model.Player;
import longshot.model.Position;
import longshot.model.Stage;
import longshot.model.stagetype.PlainStage;
import longshot.ws.RequestHelper;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.security.SecureRandom;
import java.util.List;

/**
 * Created by Naiara on 12/09/2015.
 */
@Stateless
@Path("/stage")
public class StageRestService {

    @Inject
    private RequestHelper requestHelper;

    @PersistenceContext
    private EntityManager em;

    @GET
    @Produces("application/json")
    public Response findRooms(@Context HttpServletRequest request) {
        Query namedQueryUserStages = this.em.createNamedQuery(Stage.FIND_BY_CREATOR_ID);
        Object userId = this.requestHelper.getUserId(request);
        namedQueryUserStages.setParameter("creatorId", userId);
        List userStages = namedQueryUserStages.getResultList();

        Query namedQueryOpenStages = this.em.createNamedQuery(Stage.FIND_OPEN_STAGES);
        namedQueryOpenStages.setParameter("creatorId", userId);
        List openStages = namedQueryOpenStages.getResultList();

        JsonObject json = new JsonObject();
        json.addProperty("userStages", CustomGson.GSON.toJson(userStages));
        json.addProperty("openStages", CustomGson.GSON.toJson(openStages));
        return Response.status(200).entity(CustomGson.GSON.toJson(json)).build();
    }

    @POST
    @Path("/play/{stageId}")
    @Produces("application/json")
    @Consumes("application/json")
    public Response play(@PathParam("stageId") Long stageId, @Context HttpServletRequest request) {
        // Recover Stage from database
        Stage stage = this.em.find(Stage.class, stageId);

        String userId = this.requestHelper.getUserId(request);
        String username = this.requestHelper.getUsername(request);

        // Check if player is already on this stage
        Player player = null;
        for(Player p : stage.getPlayers()){
            if(p.getUserId().equals(userId)){
                // player has already signed to this stage
                player = p;
                break;
            }
        }

        Long maxPlayers = stage.getPlayersNumber();
        if(player == null){
            // Player has not signed to this stage yet. Check if there is a free spot
            int index = stage.getPlayers().size();
            if(maxPlayers > index){
                // There is a spot. Join the stage.
                Player newPlayer = new Player(stage, new Long(index), userId, username, stage.indexPosition(index), stage.indexRotation(index), "blue");
                stage.addPlayer(newPlayer);
                this.em.merge(stage);
            }
        }

        JsonObject json = new JsonObject();
        boolean ready = maxPlayers > stage.getPlayers().size();

        if(ready && stage.getActivePlayerId() == null){
            // let's choose who goes first.
            Player activePlayer = stage.getPlayers().get(new SecureRandom().nextInt(stage.getPlayers().size()));
            // TODO: Starting fuel depends on vehicle
            activePlayer.setFuel(80L);
            stage.setActivePlayerId(activePlayer.getId());
            stage.setActivePlayerShot(false);
            stage.setActivePlayerMoved(false);
        }

        json.addProperty("response", ready ? "notReady" : "ready");
        return Response.ok(CustomGson.GSON.toJson(json)).build();
    }

    @POST
    @Path("/")
    @Produces("application/json")
    @Consumes("application/json")
    public Response saveRoom(String stageJson, @Context HttpServletRequest request) {
        PlainStage stage = new Gson().fromJson(stageJson, PlainStage.class);
        String username = this.requestHelper.getUsername(request);
        Player player = new Player(stage, 0L, this.requestHelper.getUserId(request), username, stage.indexPosition(0), stage.indexRotation(0), "red");
        stage.addPlayer(player);
        stage.setCreator(player.getUserId());
        stage.setCreatorName(username);
        stage.setActivePlayerId(player.getId());
        stage.setPlayersNumber(stage.maxPlayers());
        this.em.persist(stage);
        return Response.ok().entity(CustomGson.GSON.toJson(stage)).build();
    }

    @POST
    @Path("/{stageId}")
    @Produces("application/json")
    @Consumes("application/json")
    public Response mergeRoom(Stage newStage, @Context HttpServletRequest request) {
        if (newStage.getCreator().equals(this.requestHelper.getUsername(request))) {
            Stage stage = this.em.find(Stage.class, newStage.getId());
            stage.setName(newStage.getName());
            this.em.merge(stage);
            return Response.ok().entity(CustomGson.GSON.toJson(stage)).build();
        }
        throw new RuntimeException("Trying to merge another user's stage.");
    }

    @DELETE
    @Path("/{stageId}")
    public void deleteRoom(@PathParam("stageId") Long id, @Context HttpServletRequest request) {
        Stage stage = this.em.find(Stage.class, id);
        if (stage.getCreator().equals(this.requestHelper.getUserId(request))) {
            this.em.remove(stage);
            return;
        }
        throw new RuntimeException("Trying to remove another user's stage.");
    }

}
