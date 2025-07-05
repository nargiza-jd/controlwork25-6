package kg.attractor.java.utils;

import java.time.LocalDate;
import java.util.*;

public class Storage {
    private static final Map<LocalDate, List<Object>> appointments = new HashMap<>();

    public static int count(LocalDate day){
        return appointments.getOrDefault(day, List.of()).size();
    }
}