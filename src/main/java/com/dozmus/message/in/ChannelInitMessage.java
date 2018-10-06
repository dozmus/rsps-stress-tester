package com.dozmus.message.in;

import com.dozmus.message.Message;

public class ChannelInitMessage implements Message {

    private final int responseCode;
    private final long serverSessionKey;

    public ChannelInitMessage(int responseCode, long serverSessionKey) {
        this.responseCode = responseCode;
        this.serverSessionKey = serverSessionKey;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public long getServerSessionKey() {
        return serverSessionKey;
    }
}
