package com.dozmus.session;

import com.runescape.ISAACCipher;
import io.netty.bootstrap.Bootstrap;

import java.util.List;

public class Session {

    private final String username;
    private final String password;
    private final int uid;
    private final Bootstrap parent;
    private final boolean lowMem = true;
    private final List<String> messages;
    private boolean reconnecting = false;
    private LoginState state = LoginState.PENDING_CONNECTION;
    private ISAACCipher encrypter;
    private ISAACCipher decrypter;

    public Session(String username, String password, int uid, Bootstrap parent, List<String> messages) {
        this.username = username;
        this.password = password;
        this.uid = uid;
        this.parent = parent;
        this.messages = messages;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getUid() {
        return uid;
    }

    public Bootstrap getParent() {
        return parent;
    }

    public boolean isLowMem() {
        return lowMem;
    }

    public boolean isReconnecting() {
        return reconnecting;
    }

    public void setReconnecting(boolean reconnecting) {
        this.reconnecting = reconnecting;
    }

    public LoginState getState() {
        return state;
    }

    public void setState(LoginState state) {
        this.state = state;
    }

    public ISAACCipher getEncrypter() {
        return encrypter;
    }

    public void setEncrypter(ISAACCipher encrypter) {
        this.encrypter = encrypter;
    }

    public ISAACCipher getDecrypter() {
        return decrypter;
    }

    public void setDecrypter(ISAACCipher decrypter) {
        this.decrypter = decrypter;
    }

    public List<String> getMessages() {
        return messages;
    }
}
