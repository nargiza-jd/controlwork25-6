package kg.attractor.java.utils;

import kg.attractor.java.model.Patient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Storage {
    private static final Map<LocalDate, List<Patient>> appointments = new HashMap<>();

    public static void addPatient(LocalDate date, Patient patient) {
        appointments.computeIfAbsent(date, k -> new ArrayList<>()).add(patient);

        appointments.get(date).sort(Comparator.comparing(Patient::getTime));
    }

    public static List<Patient> getPatients(LocalDate day) {
        return appointments.getOrDefault(day, Collections.emptyList());
    }
    public static int count(LocalDate day){
        return appointments.getOrDefault(day, List.of()).size();
    }

    static {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate nextWeek = today.plusWeeks(1);

        addPatient(today, new Patient(LocalTime.of(10, 0), "Иванов Иван", "первичный", "Боли в спине"));
        addPatient(today, new Patient(LocalTime.of(11, 30), "Петрова Анна", "вторичный", "Контрольный осмотр"));
        addPatient(today, new Patient(LocalTime.of(14, 0), "Сидоров Олег", "первичный", "Высокая температура"));

        addPatient(tomorrow, new Patient(LocalTime.of(9, 0), "Кузнецова Мария", "первичный", "Головные боли"));
        addPatient(tomorrow, new Patient(LocalTime.of(15, 0), "Михайлов Дмитрий", "вторичный", "Результаты анализов"));

        addPatient(nextWeek, new Patient(LocalTime.of(10, 0), "Алексеева Елена", "первичный", "Профилактический осмотр"));
        addPatient(nextWeek, new Patient(LocalTime.of(12, 0), "Николаев Сергей", "вторичный", "Реабилитация"));
    }
}