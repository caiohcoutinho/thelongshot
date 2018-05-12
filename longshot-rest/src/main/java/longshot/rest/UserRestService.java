package longshot.rest;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.gson.Gson;
import longshot.model.GoogleJWT;
import longshot.model.GoogleToken;
import longshot.model.Token;
import longshot.model.User;
import longshot.ws.RequestHelper;
import org.apache.commons.codec.binary.Base64;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.*;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;

/**
 * Created by Naiara on 22/09/2015.
 */
@Stateless
@Path("/user")
public class UserRestService {

    private static final String STATE = "state";
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final String CODE = "code";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String REDIRECT_URI = "redirect_uri";
    private static final String GRANT_TYPE = "grant_type";
    private static final String HOST = "Host";
    private static final String GOOGLE_APIS = "www.googleapis.com";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String URLENCODED = "application/x-www-form-urlencoded";

    @Inject
    private RequestHelper requestHelper;

    @POST
    @Path("/token")
    @Produces("application/json")
    public Response token(@Context HttpServletRequest request){
        String state = new BigInteger(130, new SecureRandom()).toString(32);
        request.getSession().setAttribute(STATE, state);
        Token token = new Token(state);
        return Response.ok().entity(token).build();
    }

    @GET
    @Path("/logged")
    @Produces("application/json")
    public Response logged(@Context HttpServletRequest request){
        String id = this.requestHelper.getUserId(request);
        if(id == null){
            Response.status(200).entity(new User()).build();
        }
        String username = this.requestHelper.getUsername(request);
        String picture = this.requestHelper.getUserPicture(request);
        return Response.status(200).entity(new User(null, username, null, picture)).build();
    }

    @POST
    @Path("/logoff")
    @Produces("text/html")
    public Response logoff(@Context HttpServletRequest request) throws URISyntaxException {
        this.requestHelper.setUsername(request, null);
        this.requestHelper.setUserId(request, null);
        this.requestHelper.setUserPicture(request, null);
        return Response.ok().build();
    }

    @GET
    @Path("/code")
    @Produces("text/html")
    public Response getCode(@Context HttpServletRequest request) throws Exception {
        // Protection agains request forgery
        String sessionToken = (String) request.getSession().getAttribute(STATE);
        String requestToken = request.getParameter(STATE);
        if(!sessionToken.equals(requestToken)){
            // TODO: Must redirect 401 to login page;
            return Response.status(401).build();
        }

        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest httpRequest) throws IOException {
                httpRequest.addParser(new JsonHttpParser());
            }
        });
        GenericUrl url = new GenericUrl("https://www.googleapis.com/oauth2/v4/token");

        StringBuilder content = new StringBuilder();
        this.append(content, CODE, request.getParameter(CODE));
        this.append(content, CLIENT_ID, "405402394589-f20c532e75fn33c541jfbu5ta179bko4.apps.googleusercontent.com");
        this.append(content, CLIENT_SECRET, "9Tx02j2XpCi_e8M_q0Bnuuq3");
        String requestURL = request.getRequestURL().toString();
        if(requestURL.contains("localhost")) {
            this.append(content, REDIRECT_URI, "http://localhost:8080/rest/user/code");
        } else{
            this.append(content, REDIRECT_URI, "http://longshot-pcmaker.rhcloud.com/rest/user/code");
        }
        this.append(content, GRANT_TYPE, "authorization_code");

        ByteArrayContent byteArrayContent = new ByteArrayContent(content.toString());
        HttpRequest httpRequest = requestFactory.buildPostRequest(url, byteArrayContent);
        httpRequest.headers.put(HOST, GOOGLE_APIS);
        httpRequest.headers.put(CONTENT_TYPE, URLENCODED);
        try {
            HttpResponse response = httpRequest.execute();
            InputStream in = response.getContent();

            Reader decoder = new InputStreamReader(in);
            BufferedReader buffered = new BufferedReader(decoder);
            String json = "";
            String read = buffered.readLine();
            while(read != null){
                json += read+"\n";
                read = buffered.readLine();
            }
            Gson gson = new Gson();
            GoogleToken token = gson.fromJson(json, GoogleToken.class);
            String[] split = token.getId_token().split("\\.");
            String payloadString = new String(Base64.decodeBase64(split[1].getBytes()));
            String correctSuffix = "\"}";
            if(!payloadString.endsWith(correctSuffix)){
                String suffix = new String(new char[]{'\u0000', '\u0000'});
                if(payloadString.endsWith(suffix)){
                    payloadString = payloadString.replace(suffix, "}");
                } else {
                    payloadString = payloadString + correctSuffix;
                }
            }
            GoogleJWT jwt = gson.fromJson(payloadString, GoogleJWT.class);

            this.requestHelper.setUsername(request, jwt.getGiven_name());
            this.requestHelper.setUserId(request, jwt.getSub());
            this.requestHelper.setUserPicture(request, jwt.getPicture());
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        }
        return Response.seeOther(new URI("../")).build();
    }

    private void append(StringBuilder sb, String a, String b) {
        this.append(sb, a, b, false);
    }

    private void append(StringBuilder sb, String a, String b, boolean last) {
        sb.append(a+"="+b);
        if(!last){
            sb.append("&");
        }
    }

}
