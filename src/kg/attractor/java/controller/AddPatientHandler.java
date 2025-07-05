package kg.attractor.java.controller;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import kg.attractor.java.model.Patient;
import kg.attractor.java.server.RouteHandler;
import kg.attractor.java.utils.Storage;
import kg.attractor.java.server.ResponseCode;
import kg.attractor.java.utils.Query;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AddPatientHandler implements RouteHandler {
    private final Configuration freemarker;

    public AddPatientHandler(Configuration freemarker) {
        this.freemarker = freemarker;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        if ("GET".equals(ex.getRequestMethod())) {
            handleGet(ex);
        } else if ("POST".equals(ex.getRequestMethod())) {
            handlePost(ex);
        } else {
            ex.sendResponseHeaders(405, -1);
        }
    }

    private void handleGet(HttpExchange ex) throws IOException {
        Map<String, Object> model = new HashMap<>();

        String rawDay = Query.getParam(ex.getRequestURI().getQuery(), "day")
                .orElseThrow(() -> new IllegalArgumentException("Day parameter is missing"));

        int day = Integer.parseInt(rawDay);
        LocalDate date = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), day);

        model.put("day", day);
        model.put("date", date.toString());

        renderTemplate(ex, "addPatient.ftlh", model);
    }

    private void handlePost(HttpExchange ex) throws IOException {
        String requestBody = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> params = parseRequestBody(requestBody);

        try {
            int day = Integer.parseInt(params.get("day"));
            LocalDate date = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), day);
            LocalTime time = LocalTime.parse(params.get("time"), DateTimeFormatter.ofPattern("HH:mm"));
            String fullName = params.get("fullName");
            String type = params.get("type");
            String symptoms = params.get("symptoms");

            Patient newPatient = new Patient(time, fullName, type, symptoms);
            Storage.addPatient(date, newPatient);

            ex.getResponseHeaders().set("Location", "/patients?day=" + day);

            ex.sendResponseHeaders(ResponseCode.SEE_OTHER.getCode(), -1);
        } catch (Exception e) {
            System.err.println("Error adding patient: " + e.getMessage());
            ex.sendResponseHeaders(500, -1);
        }
    }

    private void renderTemplate(HttpExchange ex, String templateName, Map<String, Object> model) throws IOException {
        ex.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        ex.sendResponseHeaders(200, 0);
        try (Writer w = new OutputStreamWriter(ex.getResponseBody(), StandardCharsets.UTF_8)) {
            Template tpl = freemarker.getTemplate(templateName);
            tpl.process(model, w);
        } catch (TemplateException e) {
            throw new IOException(e);
        }
    }

    private Map<String, String> parseRequestBody(String requestBody) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<>();
        String[] pairs = requestBody.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            String key = URLDecoder.decode(idx > 0 ? pair.substring(0, idx) : pair, StandardCharsets.UTF_8.name());
            String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.name()) : "";
            params.put(key, value);
        }
        return params;
    }
}