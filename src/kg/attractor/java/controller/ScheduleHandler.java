package kg.attractor.java.controller;

import com.sun.net.httpserver.HttpExchange;
import freemarker.template.*;
import kg.attractor.java.server.HttpStatusCode;
import kg.attractor.java.server.RouteHandler;
import kg.attractor.java.utils.Storage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;

public class ScheduleHandler implements RouteHandler {

    private final Configuration freemarker;

    public ScheduleHandler(Configuration freemarker) {
        this.freemarker = freemarker;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {

        Map<String, Object> model = new HashMap<>();

        LocalDate  today = LocalDate.now();
        YearMonth  ym    = YearMonth.from(today);

        List<Map<String, Object>> days = new ArrayList<>();
        for (int d = 1; d <= ym.lengthOfMonth(); d++) {
            LocalDate date = ym.atDay(d);

            Map<String, Object> day = new HashMap<>();
            day.put("number",       d);
            day.put("isToday",      date.equals(today));
            day.put("dayOfWeek",    date.getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
            day.put("patientCount", Storage.count(date));
            days.add(day);
        }

        model.put("days", days);
        model.put("currentMonth", today.getMonth()
                .getDisplayName(TextStyle.FULL_STANDALONE, Locale.ENGLISH));
        model.put("year",          today.getYear());

        Template tpl = freemarker.getTemplate("schedule.ftlh");

        ex.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        ex.sendResponseHeaders(HttpStatusCode.OK.getCode(), 0);

        try (Writer w = new OutputStreamWriter(ex.getResponseBody(), StandardCharsets.UTF_8)) {
            tpl.process(model, w);
        } catch (TemplateException e) {
            throw new IOException(e);
        }
    }
}