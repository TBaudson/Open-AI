package main.java.de;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private final MySimpleClient simpleClient;

    public Application(MySimpleClient simpleClient) {
        this.simpleClient = simpleClient;
    }

    public static void main(String[] args) {
        MySimpleClient simpleClient = new MySimpleClient();
        Application application = new Application(simpleClient);
        application.run();
    }

    public void run() {
        String prompt = "Wie geht es dir?";
        String resultString = simpleClient.startRequest(prompt);

        System.out.println("> Prompt: " + prompt);
        System.out.println("Result -> " + resultString);
    }
}