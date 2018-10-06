package com.dozmus.message.in;

import com.dozmus.message.Message;

public class LoginResponseMessage implements Message {

    private final int responseCode;
    private final int privilege;
    private final boolean flagged;

    public LoginResponseMessage(int responseCode, int privilege, boolean flagged) {
        this.responseCode = responseCode;
        this.privilege = privilege;
        this.flagged = flagged;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getPrivilege() {
        return privilege;
    }

    public boolean isFlagged() {
        return flagged;
    }
}
