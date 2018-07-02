package longshot.services;

import com.google.common.collect.Maps;
import fi.iki.elonen.NanoHTTPD;
import longshot.model.dto.LongshotSession;

import java.util.List;
import java.util.Map;

import static longshot.model.CustomGson.GSON;

public class SessionService extends RestService {

    private static Map<String, LongshotSession> map = Maps.newConcurrentMap();

    @Override
    public String getDomainName() {
        return "session";
    }

    @Override
    public LongshotSession get(NanoHTTPD.CookieHandler cookieHandler, Map<String, List<String>> queryStringParams) {
        return getById(cookieHandler, queryStringParams, cookieHandler.read("userTokenId"));
    }

    @Override
    public LongshotSession getById(NanoHTTPD.CookieHandler cookieHandler, Map<String, List<String>> queryStringParams, String id) {
        if(id == null){
            return null;
        }
        if(!map.containsKey(id)) {
            // No session for this user.
            return null;
        }
        return map.get(id);
    }

    @Override
    public LongshotSession post(NanoHTTPD.CookieHandler cookieHandler, Map<String, List<String>> queryStringParams, String json) {
        LongshotSession receivedSession = GSON.fromJson(json, LongshotSession.class);
        map.put(receivedSession.getUserTokenId(), receivedSession);
        return receivedSession;
    }
}
