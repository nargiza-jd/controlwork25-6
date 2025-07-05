package kg.attractor.java.server;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Version;
import kg.attractor.java.controller.PatientsHandler;
import kg.attractor.java.controller.ScheduleHandler;

import java.io.File;
import java.io.IOException;

public class PatientServer extends BasicServer {

    public PatientServer(String host, int port) throws IOException {
        super(host, port);

        Configuration cfg = new Configuration(new Version("2.3.29"));
        cfg.setDirectoryForTemplateLoading(new File("data/templates"));
        cfg.setDefaultEncoding("UTF-8");

        cfg.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_29).build());

        registerGet("/schedule", new ScheduleHandler(cfg));


        registerGet("/patients", new PatientsHandler(cfg));
    }
}