/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package longshot;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import fi.iki.elonen.NanoHTTPD;
import longshot.model.CustomGson;
import longshot.model.dto.LongshotSession;
import longshot.services.BattleService;
import longshot.services.RestService;
import longshot.services.SessionService;
import longshot.services.StageService;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fi.iki.elonen.NanoHTTPD.Method.GET;
import static fi.iki.elonen.NanoHTTPD.Method.POST;
import static fi.iki.elonen.NanoHTTPD.Response.Status.OK;
import static fi.iki.elonen.NanoHTTPD.Response.Status.UNAUTHORIZED;
import static longshot.model.CustomGson.GSON;

/**
 * import org.springframework.boot.SpringApplication;
 * import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
 * import org.springframework.boot.autoconfigure.SpringBootApplication;
 * import org.springframework.context.annotation.ComponentScan;
 * import org.springframework.context.annotation.Configuration;
 * import org.springframework.context.annotation.Import;
 * import org.springframework.stereotype.Controller;
 *
 * @SpringBootApplication
 * @Configuration
 * @Import({PersistenceConfiguration.class})
 **/
public class Main extends NanoHTTPD {

    public static final int PORT = 5000;
    private static SessionService sessionService = new SessionService();
    private static final Map<String, String> STATIC_MIME_TYPE_MAP = Maps.newHashMap();
    {
        STATIC_MIME_TYPE_MAP.put("css", "text/css");
        STATIC_MIME_TYPE_MAP.put("html", "text/html");
        STATIC_MIME_TYPE_MAP.put("js", "text/javascript");
        STATIC_MIME_TYPE_MAP.put("jpg", "image/jpeg");
        STATIC_MIME_TYPE_MAP.put("png", "image/png");
        STATIC_MIME_TYPE_MAP.put("woff", "application/font-woff");
        STATIC_MIME_TYPE_MAP.put("woff2", "application/font-woff2");
        STATIC_MIME_TYPE_MAP.put("ico", "image/x-icon");
    }
    private static final List<RestService> PROTECTED_REST_SERVICES = Lists.newArrayList();
    private static final List<RestService> REST_SERVICES = Lists.newArrayList();
    {
        StageService stage = new StageService();
        SessionService session = new SessionService();
        BattleService battle = new BattleService();

        REST_SERVICES.add(session);
        REST_SERVICES.add(stage);
        REST_SERVICES.add(battle);
        PROTECTED_REST_SERVICES.add(stage);
        PROTECTED_REST_SERVICES.add(battle);
    }

    public Main() throws IOException {
        super(PORT);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:" + PORT + "/ \n");
    }

    public static void main(String[] args) throws Exception {
        try {
            new Main();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        CookieHandler cookieHandler = new CookieHandler(session.getHeaders());
        String userTokenId = cookieHandler.read("userTokenId");
        Response response = null;

        Map<String, List<String>> queryStringParams = session.getParameters();
        LongshotSession longshotSession = sessionService.get(cookieHandler, queryStringParams);

        for (Entry<String, String> entry : STATIC_MIME_TYPE_MAP.entrySet()) {
            if (uri.endsWith(entry.getKey())) {
                if(entry.getKey().startsWith("text")) {
                    String body = readFileAsText(uri);
                    response = newFixedLengthResponse(body);
                } else{
                    byte[] data = readFileAsBytes(uri);
                    response = newFixedLengthResponse(OK, entry.getValue(),
                            new ByteArrayInputStream(data), data.length);
                }
                response.setMimeType(entry.getValue());
                return response;
            }
        }

        String body = "";
        String restPrefix = "/rest";

        Method method = session.getMethod();
        System.out.println(method +" "+uri);
        String domain = uri.substring(restPrefix.length());
        RestService matchedRestService = null;
        for (RestService restService : REST_SERVICES) {
            if (domain.startsWith("/" + restService.getDomainName())) {
                matchedRestService = restService;
                break;
            }
        }

        if(matchedRestService == null){
            return response;
        }

        if(uri.startsWith(restPrefix) && PROTECTED_REST_SERVICES.contains(matchedRestService)) {
            if(userTokenId == null || longshotSession == null){
                response = newFixedLengthResponse(UNAUTHORIZED, "application/json", null);
                return response;
            }
        }

        String serviceDomainName = "/"+matchedRestService.getDomainName();
        if(method.equals(GET)) {
            if (domain.equals(serviceDomainName)) {
                body = GSON.toJson(matchedRestService.get(cookieHandler, queryStringParams));
            } else{
                body = GSON.toJson(matchedRestService.getById(cookieHandler, queryStringParams,
                        domain.substring(serviceDomainName.length()+1)));
            }
        } else if(method.equals(POST)){
            Map<String, String> files = Maps.newHashMap();
            try {
                session.parseBody(files);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            String postData = files.get("postData");
            Object postReturn = matchedRestService.post(cookieHandler, queryStringParams, postData);
            body = GSON.toJson(postReturn);
        }
        response = newFixedLengthResponse(body);
        response.setMimeType("application/json");
        response.setStatus(OK);
        return response;
    }

    public String readFileAsText(String stringPath){
        StringBuilder sb = new StringBuilder();
        try {
            String finalPath = stringPath.endsWith("html") ?
                    "public/html/" + stringPath:
                    stringPath.endsWith("ico")?
                            "public/ico/"+stringPath:
                            "public/" + stringPath;
            URL resource = this.getClass().getClassLoader()
                    .getResource(finalPath);
            if(resource == null){
                System.out.println("Problem reading file: "+stringPath+" "
                        +finalPath+" resource: "+resource);
                return "error reading file";
            }
            URI uri = resource.toURI();
            Path path = Paths.get(uri);
            Files.lines(path).forEach(line -> sb.append(line+"\n"));
            return sb.toString();
        } catch (Exception e) {
            System.out.println("Problem reading file: "+stringPath);
            e.printStackTrace();
            return "error reading file";
        }
    }

    public byte[] readFileAsBytes(String stringPath){
        StringBuilder sb = new StringBuilder();
        try {
            String finalPath = stringPath.endsWith("html") ?
                    "public/html/" + stringPath:
                    stringPath.endsWith("ico")?
                            "public/ico/"+stringPath:
                            "public/" + stringPath;
            URL resource = this.getClass().getClassLoader()
                    .getResource(finalPath);
            if(resource == null){
                System.out.println("Problem reading file: "+stringPath+" "
                        +finalPath+" resource: "+resource);
                return new byte[]{};
            }
            URI uri = resource.toURI();
            Path path = Paths.get(uri);
            byte[] data = Files.readAllBytes(path);
            return data;
        } catch (Exception e) {
            System.out.println("Problem reading file: "+stringPath);
            e.printStackTrace();
            return new byte[]{};
        }
    }

}
