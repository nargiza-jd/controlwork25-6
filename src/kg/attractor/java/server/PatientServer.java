package kg.attractor.java.server;

import kg.attractor.java.controller.AddPatientHandler;
import kg.attractor.java.controller.PatientsHandler;
import kg.attractor.java.controller.ScheduleHandler;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.IOException;

public class PatientServer extends BasicServer {

    public PatientServer(String host, int port) throws IOException {
        super(host, port);

        Configuration freemarker = configureFreemarker();

        registerGet("/schedule", new ScheduleHandler(freemarker));
        registerGet("/patients", new PatientsHandler(freemarker));
        registerGet("/patients/add", new AddPatientHandler(freemarker));
        registerPost("/patients/add", new AddPatientHandler(freemarker));
    }

    private Configuration configureFreemarker() throws IOException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_29);
        configuration.setDirectoryForTemplateLoading(new File("data/templates"));
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);
        return configuration;
    }
}