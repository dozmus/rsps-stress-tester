package com.purecs;

public interface SessionCredentialsGenerator {

    String username(int n);

    String password(int n);

    int uid();
}
