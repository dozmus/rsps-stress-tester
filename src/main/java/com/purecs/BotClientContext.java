package com.purecs;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public final class BotClientContext {

    @Parameter(names = {"--host", "-h"}, description = "Server hostname")
    private String host = "127.0.0.1";
    @Parameter(names = {"--port", "-p"}, description = "Server port")
    private int port = 43594;
    @Parameter(names = {"--number", "-n"}, description = "Number of bot clients")
    private int number = 1;
    @Parameter(names = {"--threads", "-t"}, description = "Number of threads to use")
    private int threads = 1;
    @Parameter(names = {"--messages", "-m"}, description = "Messages")
    private List<String> messages = new ArrayList<>();

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getNumber() {
        return number;
    }

    public int getThreads() {
        return threads;
    }

    public List<String> getMessages() {
        return messages;
    }
}
