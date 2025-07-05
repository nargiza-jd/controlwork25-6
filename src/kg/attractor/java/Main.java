package kg.attractor.java;


import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            new Lesson46Server("localhost", 8089
            ).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
