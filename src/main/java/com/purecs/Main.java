package com.purecs;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        String host = "127.0.0.1";
        int port = 43594;
        BotClient client = new BotClient();
        int n = 1;
        int chunks = 10;

        for (int i = 1; i <= n; i++) {
            client.connect(host, port, "Bot" + i);

            if (i % chunks == 0) {
                Thread.sleep(1000);
            }
        }
    }
}
