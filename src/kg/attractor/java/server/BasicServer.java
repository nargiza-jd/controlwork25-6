package kg.attractor.java.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import kg.attractor.java.controller.AddPatientHandler;
import kg.attractor.java.controller.DeletePatientHandler;
import kg.attractor.java.controller.PatientsHandler;
import kg.attractor.java.controller.ScheduleHandler;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class BasicServer {

    private static final Logger logger = Logger.getLogger(BasicServer.class.getName());

    private final HttpServer server;
    private final Map<String, RouteHandler> routes = new HashMap<>();
    private final String dataDir = "data";

    protected BasicServer(String host, int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(host, port), 50);
        System.out.printf("Server started: http://%s:%d/%n", host, port);

        server.createContext("/", this::dispatch);

        registerGet("", ex -> redirect303(ex, "/schedule"));
        registerGet("/", ex -> redirect303(ex, "/schedule"));
    }

    public void start() {
        server.start();

    }

    protected void registerGet(String route, RouteHandler h) {
        routes.put("GET " + route, h);
    }

    protected void registerPost(String route, RouteHandler h) {
        routes.put("POST " + route, h);
    }

    private void dispatch(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        String method = ex.getRequestMethod().toUpperCase();

        if ("GET".equals(method)) {
            String fileExtension = getFileExtension(path);
            ContentType contentType = getContentType(fileExtension);

            if (contentType != null) {
                Path filePath = Path.of(dataDir, path.substring(1));
                if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                    sendFile(ex, filePath, contentType);
                    return;
                }
            }
        }

        String key = method + " " + path;
        RouteHandler handler = routes.getOrDefault(key, this::respond404);
        try {
            handler.handle(ex);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error handling request for " + key, e);
            sendErrorResponse(ex, HttpStatusCode.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
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

    protected Path makePath(String... parts) {
        return Path.of(dataDir, String.join(File.separator, parts));
    }

    protected void sendFile(HttpExchange ex, Path file, ContentType ct) throws IOException {
        if (Files.notExists(file) || !Files.isReadable(file)) {
            respond404(ex);
            return;
        }
        byte[] bytes = Files.readAllBytes(file);
        sendBytes(ex, HttpStatusCode.OK, ct, bytes);
    }

    protected void sendBytes(HttpExchange ex, HttpStatusCode code,
                             ContentType ct, byte[] data) throws IOException {
        ex.getResponseHeaders().set("Content-Type", ct.toString());
        ex.sendResponseHeaders(code.getCode(), data.length);
        try (var out = ex.getResponseBody()) {
            out.write(data);
        }
    }

    protected void redirect303(HttpExchange ex, String path) throws IOException {
        Headers headers = ex.getResponseHeaders();
        headers.set("Location", path);
        ex.sendResponseHeaders(HttpStatusCode.SEE_OTHER.getCode(), -1);
    }

    protected String body(HttpExchange ex) {
        try (var r = new BufferedReader(
                new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8))) {
            return r.lines().collect(Collectors.joining());
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error reading request body", e);
            return "";
        }
    }

    private void respond404(HttpExchange ex) {
        try {
            sendBytes(ex, HttpStatusCode.NOT_FOUND,
                    ContentType.TEXT_PLAIN, "404 Not Found".getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignored) {
            logger.log(Level.SEVERE, "Failed to send 404 response.", ignored);
        }
    }

    protected void sendErrorResponse(HttpExchange ex, HttpStatusCode code, String message) throws IOException {
        sendBytes(ex, code,
                ContentType.TEXT_PLAIN,
                message.getBytes(StandardCharsets.UTF_8));
    }


    public static abstract class BaseRouteHandler implements RouteHandler {

        protected void redirect303(HttpExchange ex, String path) throws IOException {
            Headers headers = ex.getResponseHeaders();
            headers.set("Location", path);
            ex.sendResponseHeaders(HttpStatusCode.SEE_OTHER.getCode(), -1);
        }

        protected String body(HttpExchange ex) {
            try (var r = new BufferedReader(
                    new InputStreamReader(ex.getRequestBody(), StandardCharsets.UTF_8))) {
                return r.lines().collect(Collectors.joining());
            } catch (IOException e) {
                Logger.getLogger(BaseRouteHandler.class.getName())
                        .log(Level.WARNING, "Error reading request body in BaseRouteHandler", e);
                return "";
            }
        }

        protected void sendBytes(HttpExchange ex, HttpStatusCode code,
                                 ContentType ct, byte[] data) throws IOException {
            ex.getResponseHeaders().set("Content-Type", ct.toString());
            ex.sendResponseHeaders(code.getCode(), data.length);
            try (var out = ex.getResponseBody()) {
                out.write(data);
            }
        }

        protected void sendErrorResponse(HttpExchange ex, HttpStatusCode code, String message) throws IOException {
            sendBytes(ex, code,
                    ContentType.TEXT_PLAIN,
                    message.getBytes(StandardCharsets.UTF_8));
        }
    }
}