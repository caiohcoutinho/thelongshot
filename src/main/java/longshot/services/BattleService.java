package longshot.services;

import fi.iki.elonen.NanoHTTPD;
import longshot.model.entity.Player;
import longshot.model.entity.Stage;

import javax.persistence.Query;
import java.util.List;
import java.util.Map;

public class BattleService extends RestService {

    private SessionService sessionService = new SessionService();

    @Override
    public String getDomainName() {
        return "battle";
    }

    @Override
    public Stage get(NanoHTTPD.CookieHandler cookieHandler, Map<String, List<String>> queryStringParams) {
        Long battleId = getBattleIdFromCookies(cookieHandler, queryStringParams);
        Query query = this.getEntityManager().createQuery("From Stage where id = :battleId");
        query.setParameter("battleId", battleId);
        Stage stage = (Stage) query.getSingleResult();
        List<Player> players = stage.getPlayers();
        Player selectedPlayer = players.get(0);
        stage.setSelectedPlayer(selectedPlayer);
        stage.setActivePlayerId(selectedPlayer.getId());
        return stage;
    }

    private Long getBattleIdFromCookies(NanoHTTPD.CookieHandler cookieHandler, Map<String, List<String>> queryStringParams) {
        String battleId = cookieHandler.read("battleId");
        return Long.parseLong(battleId);
    }

    @Override
    public Object getById(NanoHTTPD.CookieHandler cookieHandler, Map<String, List<String>> queryStringParams, String id) {
        return null;
    }

    @Override
    public Object post(NanoHTTPD.CookieHandler cookieHandler, Map<String, List<String>> queryStringParams, String json) {
        return null;
    }
}
