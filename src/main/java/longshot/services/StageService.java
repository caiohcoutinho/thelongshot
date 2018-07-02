package longshot.services;

import com.google.common.collect.Lists;
import fi.iki.elonen.NanoHTTPD;
import longshot.model.dto.LongshotSession;
import longshot.model.dto.Position;
import longshot.model.dto.StageResult;
import longshot.model.entity.Player;
import longshot.model.entity.Stage;
import longshot.model.entity.stagetype.PlainStage;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

import static longshot.model.CustomGson.GSON;

public class StageService extends RestService {

    private SessionService sessionService = new SessionService();

    @Override
    public String getDomainName() {
        return "stage";
    }

    @Override
    public StageResult get(NanoHTTPD.CookieHandler cookieHandler, Map<String, List<String>> queryStringParams) {
        Query query = this.getEntityManager().createQuery("From Stage where creator = :creator");
        LongshotSession longshotSession = getLongshotSession(cookieHandler, queryStringParams);
        query.setParameter("creator", longshotSession.getUserId());
        List<Stage> resultList = query.getResultList();
        return new StageResult(resultList, resultList);
    }

    private LongshotSession getLongshotSession(NanoHTTPD.CookieHandler cookieHandler, Map<String, List<String>> queryStringParams) {
        String userTokenId = cookieHandler.read("userTokenId");
        return sessionService.getById(cookieHandler, queryStringParams, userTokenId);
    }

    @Override
    public Stage getById(NanoHTTPD.CookieHandler cookieHandler, Map<String, List<String>> queryStringParams, String id) {
        Stage stage = this.getEntityManager().find(Stage.class, Long.parseLong(id));
        return stage;
    }

    @Override
    public Stage post(NanoHTTPD.CookieHandler cookieHandler, Map<String, List<String>> queryStringParams, String json) {

        // TODO: This method today creates a very basic stage with the current player in it.
        // In the future, this will be changed to receive more data from the form.

        Stage receivedStage = GSON.fromJson(json, PlainStage.class);
        LongshotSession longshotSession = getLongshotSession(cookieHandler, queryStringParams);
        String userId = longshotSession.getUserId();
        receivedStage.setCreator(userId);
        String username = longshotSession.getUsername();
        receivedStage.setCreatorName(username);
        List<Player> players = Lists.newArrayList();
        Position pos = new Position(0.0, 0.0, 0.0);
        Position rot = new Position(0.0, 0.0, 0.0);
        players.add(new Player(receivedStage, 0L, userId, username, pos, rot, "red"));
        receivedStage.setPlayers(players);
        EntityManager em = this.getEntityManager();
        em.getTransaction().begin();
        em.persist(receivedStage);
        em.getTransaction().commit();
        return receivedStage;
    }
}
