package longshot.ws;


import longshot.model.*;
import longshot.model.ammo.ALCA50;
import longshot.model.condition.*;
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
@AtmosphereService(path = "/ws/battle",
        broadcasterCache = UUIDBroadcasterCache.class,
        interceptors = {AtmosphereResourceLifecycleInterceptor.class,
                TrackMessageSizeInterceptor.class,
                HeartbeatInterceptor.class
        },
        servlet = "org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher")
@Path("/ws/battle")
@Stateless
public class BattleWebSocketService {

    public static final String WS_BATTLE = "/ws/battle/";
    public static final Long OUT_OF_TURN = 2L;
    public static final Long EQUATION_EVALUATION_PROBLEM = 3L;
    public static final Long PLAYER_OUT_OF_ROTATION = 4L;
    public static final Long MOVE_FLAG_IS_TRUE = 5L;
    public static final Long FUEL_IS_EMPTY = 6L;
    public static final Long FUEL_IS_NULL = 7L;
    public static final Long PLAYER_OUT_OF_POSITION = 8L;
    public static final Long MOVEMENT_INVALID_ON_STAGE = 9L;
    public static final Long SHOOT_FLAG_IS_TRUE = 10L;
    public static final Long MAX_FUEL = 80L; // TODO: Today, is vehicle independent.

    @Inject
    private BroadcastFinder finder;

    @Inject
    private RequestHelper requestHelper;

    @PersistenceContext
    private EntityManager em;

    private static final ConditionVerifier MOVE_CONDITION_VERIFIER = new ConditionVerifier(new Condition[]{
            new CorrectTurnCondition(),
            new PlayerHaveNotMovedCondition(),
            new FuelIsNotNullCondition(),
            new FuelIsNotEmptyCondition(),
            new CorrectPlayerPositionMoveCondition(),
            new CorrectPlayerRotationMoveCondition(),
            new StageValidatedMovementCondition()
    });

    private static final ConditionVerifier SHOT_CONDITION_VERIFIER = new ConditionVerifier(new Condition[]{
            new CorrectTurnCondition(),
            new PlayerHaveNotShotCondition(),
            new CorrectPlayerPositionShotCondition()
    });

    @GET
    @Get
    @Path("/{stageId}")
    public Response doGet(@PathParam("stageId") Long stageId, @Context HttpServletRequest req) {
        this.finder.findAndAddBroadcaster(req, stageId, WS_BATTLE);
        return Response.ok().build();
    }

    @POST
    @Post
    @Path("/{stageId}")
    public void broadcast(Turn message, @PathParam("stageId") Long stageId, @Context HttpServletRequest req) {
        Broadcaster broadcaster = this.finder.findAndAddBroadcaster(req, stageId, WS_BATTLE);
        String userId = this.requestHelper.getUserId(req);
        Stage stage = this.em.find(Stage.class, stageId);
        Player player = stage.getPlayer(userId);
        Move move = message.getMove();
        if (move != null) {
                    // This is not how cheating checks should be done.
                    // ErrorMessage errorMessage = MOVE_CONDITION_VERIFIER.verify(stage, player, message);

            Long fuel = player.getFuel();
            player.setPosition(player.getPosition().plus(move.getNewPosition().minus(move.getPosition())));
            player.setRotation(player.getRotation().plus(move.getNewRotation().minus(move.getRotation())));
            // TODO: Fuel spend depending on vehicle
            fuel--;
            player.setFuel(fuel);
            stage.setActivePlayerMoved(fuel <= 0);
            move.setUserId(player.getId());
            this.em.merge(player);
            this.em.merge(stage);

            this.switchTurns(stage, player);

            System.out.println("[Broadcast] about to broadcast move.");
            broadcaster.broadcast(CustomGson.GSON.toJson(stage));
            return;

        }
        Shot shot = message.getShot();
        if (shot != null) {
            try {
                shot.setUserId(userId);
                // TODO: choose weapon
                ShotResult shotResult = new ALCA50().calculateFire(shot, stage);

                stage.addShotResult(shotResult);
                stage.setActivePlayerShot(true);
                this.em.merge(stage);
                this.em.flush();

                this.switchTurns(stage, player);

                System.out.println("[Broadcast] about to broadcast shot.");
                broadcaster.broadcast(CustomGson.GSON.toJson(stage));
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("[Broadcast] about to broadcast shot stacktrace.");
                broadcaster.broadcast(CustomGson.GSON.toJson(new ErrorMessage(EQUATION_EVALUATION_PROBLEM, e.getMessage())));
            }
            return;

        }

    }

    private void switchTurns(Stage stage, Player player) {
        if (stage.isActivePlayerMoved() && stage.isActivePlayerShot()) {
            int i = stage.getPlayers().indexOf(player);
            if (i == stage.getPlayersNumber() - 1) {
                i = -1;
            }
            Player newActivePlayer = stage.getPlayers().get(i + 1);
            Long newActivePlayerId = newActivePlayer.getId();
            stage.setActivePlayerId(newActivePlayerId);
            stage.setActivePlayerShot(false);
            stage.setActivePlayerMoved(false);
            newActivePlayer.setFuel(MAX_FUEL);
            this.em.merge(newActivePlayer);
            this.em.merge(stage);
        }
    }

}
