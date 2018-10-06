package com.dozmus.session;

public enum LoginState {

    PENDING_CONNECTION,
    WAITING_FOR_JUNK,
    WAITING_FOR_RESPONSE_CODE,
    WAITING_FOR_LOGIN_RESPONSE,
    CONNECTED,
    DISCONNECTED
}
