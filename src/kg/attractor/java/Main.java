package kg.attractor.java;

import kg.attractor.java.server.PatientServer;
import kg.attractor.java.utils.Generator;
import kg.attractor.java.utils.Storage;

import java.io.IOException;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        try {
            for (int i = 1; i <= 5; i++) {
                for (int j = 0; j < 3; j++) {
                    var patient = Generator.generatePatient();
                    Storage.addPatient(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), i), patient);
                }
            }

            new PatientServer("localhost", 8089).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}