package kg.attractor.java;

import kg.attractor.java.server.PatientServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            new PatientServer("localhost", 8089).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}