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
import longshot.services.RestService;
import longshot.services.StageService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
    private static final List<RestService> REST_SERVICES = Lists.newArrayList();
    {
        REST_SERVICES.add(new StageService());
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
        Response response = null;
        for (Entry<String, String> entry : STATIC_MIME_TYPE_MAP.entrySet()) {
            if (uri.endsWith(entry.getKey())) {
                if(entry.getKey().startsWith("text")) {
                    String body = readFileAsText(uri);
                    response = newFixedLengthResponse(body);
                } else{
                    byte[] data = readFileAsBytes(uri);
                    response = newFixedLengthResponse(Response.Status.OK, entry.getValue(),
                            new ByteArrayInputStream(data), data.length);
                }
                response.setMimeType(entry.getValue());
                return response;
            }
        }
        String restPrefix = "/rest";
        if(uri.startsWith(restPrefix)){
            Method method = session.getMethod();
            System.out.println(method +" "+uri);
            String domain = uri.substring(restPrefix.length());

            String body = "";

            if(method.equals(Method.GET)) {
                for (RestService restService : REST_SERVICES) {
                    String serviceDomainName = "/" + restService.getDomainName();
                    if (domain.startsWith(serviceDomainName)) {
                        if (domain.equals(serviceDomainName)) {
                            body = CustomGson.GSON.toJson(restService.get());
                        } else{
                            body = CustomGson.GSON.toJson(restService.getById(
                                    Long.parseLong(domain.substring(serviceDomainName.length()+1))));
                        }
                    }
                }
            }
            response = newFixedLengthResponse(body);
            response.setMimeType("application/json");
            return response;
        }
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
