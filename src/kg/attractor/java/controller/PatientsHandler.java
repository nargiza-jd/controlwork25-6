package kg.attractor.java.controller;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.*;
import kg.attractor.java.server.RouteHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class PatientsHandler implements RouteHandler {
    private final Configuration freemarker;

    public PatientsHandler(Configuration freemarker) {
        this.freemarker = freemarker;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        Map<String, Object> model = new HashMap<>();

        String rawDay = Optional.ofNullable(ex.getRequestURI().getQuery()).orElse("day=1");
        int day = Integer.parseInt(rawDay.split("=")[1]);
        YearMonth ym = YearMonth.now();
        LocalDate date = ym.atDay(day);

        model.put("day", day);
        model.put("date", date.toString());
        model.put("patients", List.of());

        Template tpl = freemarker.getTemplate("patients.ftlh");

        ex.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        ex.sendResponseHeaders(200, 0);
        try (Writer w = new OutputStreamWriter(ex.getResponseBody(), StandardCharsets.UTF_8)) {
            tpl.process(model, w);
        } catch (TemplateException e) {
            throw new IOException(e);
        }
    }
}