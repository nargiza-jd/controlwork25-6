package kg.attractor.java.controller;

import com.sun.net.httpserver.HttpExchange;
import kg.attractor.java.server.BasicServer;
import kg.attractor.java.server.HttpStatusCode;
import kg.attractor.java.utils.Query;
import kg.attractor.java.utils.Storage;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

public class DeletePatientHandler extends BasicServer.BaseRouteHandler {

    public DeletePatientHandler() { }

    @Override
    public void handle(HttpExchange ex) throws IOException {

        if (!ex.getRequestMethod().equalsIgnoreCase("GET")) {
            sendErrorResponse(ex, HttpStatusCode.METHOD_NOT_ALLOWED, "Method not allowed");
            return;
        }

        Map<String, String> qp = Query.parseQuery(ex.getRequestURI().getQuery());

        String dayStr  = qp.get("day");
        String timeStr = qp.get("time");
        String name    = qp.get("fullName");

        if (dayStr == null || timeStr == null || name == null ||
                dayStr.isBlank() || timeStr.isBlank() || name.isBlank()) {
            sendErrorResponse(ex, HttpStatusCode.BAD_REQUEST, "Missing parameters");
            return;
        }

        try {
            int day        = Integer.parseInt(dayStr);
            LocalTime time = LocalTime.parse(timeStr);

            if (Storage.deletePatient(day, time, name)) {
                redirect303(ex, "/patients?day=" + day);
            } else {
                sendErrorResponse(ex, HttpStatusCode.NOT_FOUND, "Patient not found");
            }

        } catch (NumberFormatException e) {
            sendErrorResponse(ex, HttpStatusCode.BAD_REQUEST, "Invalid day");
        } catch (DateTimeParseException e) {
            sendErrorResponse(ex, HttpStatusCode.BAD_REQUEST, "Invalid time");
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(ex, HttpStatusCode.INTERNAL_SERVER_ERROR, "Server error");
        }
    }
}