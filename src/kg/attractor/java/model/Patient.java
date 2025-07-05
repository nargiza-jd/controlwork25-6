package kg.attractor.java.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Patient {
    private LocalTime time;
    private String fullName;
    private String type;
    private String symptoms;

    public Patient(LocalTime time, String fullName, String type, String symptoms) {
        this.time = time;
        this.fullName = fullName;
        this.type = type;
        this.symptoms = symptoms;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getFormattedTime() {
        return time.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "time=" + time +
                ", fullName='" + fullName + '\'' +
                ", type='" + type + '\'' +
                ", symptoms='" + symptoms + '\'' +
                '}';
    }
}