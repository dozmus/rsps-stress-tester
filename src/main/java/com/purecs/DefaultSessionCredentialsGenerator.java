package com.purecs;

public class DefaultSessionCredentialsGenerator implements SessionCredentialsGenerator {

    public String username(int n) {
        return "Bot" + n;
    }

    public String password(int n) {
        return username(n) + "123";
    }

    public int uid() {
        return 27738603;
    }
}
