package kg.attractor.java.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BasicServer {

    private final HttpServer server;
    private final Map<String, RouteHandler> routes = new HashMap<>();
    private final String dataDir = "data";



    protected BasicServer(String host, int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(host, port), 50);
        System.out.printf("Starting server on http://%s:%s/%n", host, port);

        server.createContext("/", this::dispatch);


        registerFileHandler(".css",  ContentType.TEXT_CSS);
        registerFileHandler(".html", ContentType.TEXT_HTML);
        registerFileHandler(".jpg",  ContentType.IMAGE_JPEG);
        registerFileHandler(".png",  ContentType.IMAGE_PNG);


        registerGet("", exchange -> redirect303(exchange, "/auth/login"));
        registerGet("/", exchange -> redirect303(exchange, "/auth/login"));
    }

    public final void start() { server.start(); }



    protected final void registerGet (String route, RouteHandler h){ routes.put("GET "  + route, h); }
    protected final void registerPost(String route, RouteHandler h){ routes.put("POST " + route, h); }

    private void registerFileHandler(String ext, ContentType type){
        registerGet(ext, ex -> sendFile(ex, makePath(ex), type));
    }



    private void dispatch(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        if (path.equals("")) path = "/";
        String key = ex.getRequestMethod().toUpperCase() + " " + path;
        routes.getOrDefault(key, this::respond404).handle(ex);
    }


    protected Path makePath(String... parts){ return Path.of(dataDir, parts); }

    private Path makePath(HttpExchange ex){ return makePath(ex.getRequestURI().getPath()); }

    protected void sendFile(HttpExchange ex, Path file, ContentType ct) throws IOException {
        if (Files.notExists(file)){ respond404(ex); return; }
        sendBytes(ex, ResponseCodes.OK, ct, Files.readAllBytes(file));
    }

    protected void sendBytes(HttpExchange ex, ResponseCodes code,
                             ContentType ct, byte[] data) throws IOException {
        ex.getResponseHeaders().set("Content-Type", ct.toString());
        ex.sendResponseHeaders(code.getCode(), data.length);
        try (OutputStream os = ex.getResponseBody()){ os.write(data); }
    }

    private void respond404(HttpExchange ex){
        try { sendBytes(ex, ResponseCodes.NOT_FOUND,
                ContentType.TEXT_PLAIN, "404 Not found".getBytes()); }
        catch (IOException ignored){}
    }

    protected void redirect303(HttpExchange exchange, String to){
        try{
            exchange.getResponseHeaders().add("Location", to);
            exchange.sendResponseHeaders(ResponseCodes.REDIRECT_303.getCode(), -1);
        }catch (IOException ignored){}
    }


    protected String body(HttpExchange ex){
        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8))){
            return r.lines().collect(Collectors.joining());
        }catch (IOException e){ return ""; }
    }

    protected final void sendByteData(HttpExchange exchange, ResponseCodes responseCode,
                                      ContentType contentType, byte[] data) throws IOException {
        try (var output = exchange.getResponseBody()) {
            exchange.getResponseHeaders().set("Content-Type", contentType.toString());
            exchange.sendResponseHeaders(responseCode.getCode(), data.length);
            output.write(data);
            output.flush();
        }
    }

    protected void setCookie(HttpExchange exchange, Cookie cookie) {
        exchange.getResponseHeaders().add("Set-Cookie", cookie.toString());
    }

    protected String getCookies(HttpExchange exchange) {
        return exchange.getRequestHeaders()
                .getOrDefault("Cookie", List.of(""))
                .get(0);
    }


}