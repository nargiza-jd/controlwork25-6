package kg.attractor.java.controller;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.*;
import kg.attractor.java.model.Patient;
import kg.attractor.java.server.BasicServer;
import kg.attractor.java.server.ContentType;
import kg.attractor.java.server.HttpStatusCode;
import kg.attractor.java.utils.Query;
import kg.attractor.java.utils.Storage;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class AddPatientHandler extends BasicServer.BaseRouteHandler {

    private final Configuration freemarker;

    public AddPatientHandler(Configuration freemarker) {
        this.freemarker = freemarker;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        switch (ex.getRequestMethod()) {
            case "GET"  -> handleGet(ex);
            case "POST" -> handlePost(ex);
            default     -> ex.sendResponseHeaders(HttpStatusCode.METHOD_NOT_ALLOWED.getCode(), -1);
        }
    }

    private void handleGet(HttpExchange ex) throws IOException {
        Map<String, Object> model = new HashMap<>();

        int day = Integer.parseInt(
                Query.getParam(ex.getRequestURI().getQuery(), "day")
                        .orElseThrow(() -> new IllegalArgumentException("Day parameter missing"))
        );

        LocalDate date = LocalDate.of(LocalDate.now().getYear(),
                LocalDate.now().getMonth(), day);

        model.put("day",  day);
        model.put("date", date.toString());

        renderTemplate(ex, "addPatient.ftlh", model);
    }

    private void handlePost(HttpExchange ex) throws IOException {
        Map<String, String> p = parseBody(new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));

        try {
            int day = Integer.parseInt(p.get("day"));
            LocalDate date   = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), day);
            if (date.isBefore(LocalDate.now())) {
                sendErrorResponse(ex, HttpStatusCode.BAD_REQUEST, "Нельзя записывать на прошедшую дату.");
                return;
            }

            String timeStr   = p.get("time");
            String fullName  = p.get("fullName");
            String type      = p.get("type");
            String symptoms  = p.get("symptoms");

            if (timeStr == null || timeStr.isBlank()
                    || fullName == null || fullName.isBlank()
                    || type == null || type.isBlank()
                    || symptoms == null || symptoms.isBlank()) {
                sendErrorResponse(ex, HttpStatusCode.BAD_REQUEST, "Все поля обязательны для заполнения.");
                return;
            }

            LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
            Storage.addPatient(date, new Patient(time, fullName, type, symptoms));


            redirect303(ex, "/patients?day=" + day);

        } catch (NumberFormatException e) {
            sendErrorResponse(ex, HttpStatusCode.BAD_REQUEST, "Некорректный день.");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(ex, HttpStatusCode.INTERNAL_SERVER_ERROR, "Ошибка сервера.");
        }
    }

    private void renderTemplate(HttpExchange ex, String tplName, Map<String, Object> model) throws IOException {
        ex.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        ex.sendResponseHeaders(HttpStatusCode.OK.getCode(), 0);
        try (Writer w = new OutputStreamWriter(ex.getResponseBody(), StandardCharsets.UTF_8)) {
            freemarker.getTemplate(tplName).process(model, w);
        } catch (TemplateException e) {
            throw new IOException(e);
        }
    }

    private Map<String, String> parseBody(String body) {
        Map<String, String> params = new HashMap<>();
        for (String pair : body.split("&")) {
            int idx = pair.indexOf('=');
            String key = URLDecoder.decode(idx > 0 ? pair.substring(0, idx) : pair, StandardCharsets.UTF_8);
            String val = idx > 0 && pair.length() > idx + 1
                    ? URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8)
                    : "";
            params.put(key, val);
        }
        return params;
    }
}