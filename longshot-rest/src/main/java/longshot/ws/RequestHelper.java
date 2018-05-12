package longshot.ws;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by Naiara on 12/10/2015.
 */
@Stateless
public class RequestHelper {

    private static final String STAGE_ID = "stageId";
    private static final String USER_ID = "id";
    private static final String USER_USERNAME = "username";
    private static final String USER_PICTURE = "picture";

    public String getUserId(HttpServletRequest request) {
        return this.get(request, USER_ID);
    }

    public void setStageId(HttpServletRequest request, Long stageId) {
        this.set(request, STAGE_ID, stageId);
    }

    public String getUsername(HttpServletRequest request) {
        return this.get(request, USER_USERNAME);
    }

    public String getUserPicture(HttpServletRequest request) {
        return this.get(request, USER_PICTURE);
    }

    public void setUsername(HttpServletRequest request, String username) {
        this.set(request, USER_USERNAME, username);
    }

    public void setUserId(HttpServletRequest request, String id) {
        this.set(request, USER_ID, id);
    }

    private String get(HttpServletRequest request, String key){
        return (String) request.getSession().getAttribute(key);
    }

    private void set(HttpServletRequest request, String key, Object value){
        request.getSession().setAttribute(key, value);
    }

    public void setUserPicture(HttpServletRequest request, String userPicturePath) {
        this.set(request, USER_PICTURE, userPicturePath);
    }

    public Long getStageId(HttpServletRequest request) {
        return (Long) request.getSession().getAttribute(STAGE_ID);
    }
}
