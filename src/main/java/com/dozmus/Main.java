package com.dozmus;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        // Parse command-line arguments
        BotClientContext ctx = new BotClientContext();
        JCommander commander = JCommander.newBuilder()
                .addObject(ctx)
                .programName("java -jar rsps-stress-tester.jar")
                .build();

        try {
            commander.parse(args);
        } catch (ParameterException ex) {
            LOGGER.error("Unable to parse command-line arguments: {}", ex.getMessage());
            commander.usage();
            System.exit(1);
        }

        // Create hive manager
        Injector injector = Guice.createInjector(new BotModule(ctx));
        BotClientHiveManager hive = injector.getInstance(BotClientHiveManager.class);

        // Connect clients
        final int chunk = 10;

        for (int i = 1; i <= ctx.getNumber(); i++) {
            String username = "Bot" + i;
            String password = "Password" + i;
            int uid = 27738603;

            hive.connect(username, password, uid);

            if (i % chunk == 0) {
                Thread.sleep(1000);
            }
        }
    }
}
