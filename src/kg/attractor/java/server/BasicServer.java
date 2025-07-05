package kg.attractor.java.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BasicServer {

    private final HttpServer server;
    private final Map<String, RouteHandler> routes = new HashMap<>();
    private final String dataDir = "data";

    protected BasicServer(String host, int port) throws IOException {


        server = HttpServer.create(new InetSocketAddress(host, port), 50);
        System.out.printf("Server started: http://%s:%d/%n", host, port);

        server.createContext("/", this::dispatch);

//        registerFileHandler(".css",  ContentType.TEXT_CSS);
//        registerFileHandler(".html", ContentType.TEXT_HTML);
//        registerFileHandler(".jpg",  ContentType.IMAGE_JPEG);
//        registerFileHandler(".png",  ContentType.IMAGE_PNG);

        registerGet("",  ex -> redirect303(ex, "/schedule"));
        registerGet("/", ex -> redirect303(ex, "/schedule"));
    }

    public void start() { server.start(); }

    protected void registerGet (String route, RouteHandler h){ routes.put("GET "  + route, h); }
    protected void registerPost(String route, RouteHandler h){ routes.put("POST " + route, h); }



    private void dispatch(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        String method = ex.getRequestMethod().toUpperCase();

        if ("GET".equals(method)) {
            String fileExtension = getFileExtension(path);
            ContentType contentType = getContentType(fileExtension);

            if (contentType != null) {

                Path filePath = makePath(path.substring(1));
                if (Files.exists(filePath)) {
                    sendFile(ex, filePath, contentType);
                    return;
                }
            }
        }

        String key  = method + " " + path;
        RouteHandler handler = routes.getOrDefault(key, this::respond404);
        handler.handle(ex);
    }


    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return null;
        }
        return fileName.substring(dotIndex);
    }

    private ContentType getContentType(String fileExtension) {
        if (fileExtension == null) return null;
        return switch (fileExtension.toLowerCase()) {
            case ".css" -> ContentType.TEXT_CSS;
            case ".html" -> ContentType.TEXT_HTML;
            case ".jpg", ".jpeg" -> ContentType.IMAGE_JPEG;
            case ".png" -> ContentType.IMAGE_PNG;
            default -> null;
        };
    }

    protected Path makePath(String... parts){ return Path.of(dataDir, parts); }
    private   Path makePath(HttpExchange ex){ return makePath(ex.getRequestURI().getPath()); }

    protected void sendFile(HttpExchange ex, Path file, ContentType ct) throws IOException {
        if (Files.notExists(file)){ respond404(ex); return; }
        byte[] bytes = Files.readAllBytes(file);
        sendBytes(ex, ResponseCode.OK, ct, bytes);
    }

    protected void sendBytes(HttpExchange ex, ResponseCode code,
                             ContentType ct, byte[] data) throws IOException {
        ex.getResponseHeaders().set("Content-Type", ct.toString());
        ex.sendResponseHeaders(code.getCode(), data.length);
        try (var out = ex.getResponseBody()){ out.write(data); }
    }

    protected void redirect303(HttpExchange ex, String path) throws IOException {
        Headers headers = ex.getResponseHeaders();
        headers.set("Location", path);

        ex.sendResponseHeaders(ResponseCode.SEE_OTHER.getCode(), -1);
    }

    protected String body(HttpExchange ex){
        try (var r = new BufferedReader(
                new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8))){
            return r.lines().collect(Collectors.joining());
        }catch (IOException e){ return ""; }
    }

    private void respond404(HttpExchange ex){
        try { sendBytes(ex, ResponseCode.NOT_FOUND,
                ContentType.TEXT_PLAIN, "404 Not Found".getBytes()); }
        catch (IOException ignored){}
    }
}