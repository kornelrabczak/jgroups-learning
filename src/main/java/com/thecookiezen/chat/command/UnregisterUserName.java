package com.thecookiezen.chat.command;

import lombok.Getter;
import org.jgroups.Address;

import java.io.Serializable;
import java.util.Map;

@Getter
public class UnregisterUserName implements UserNameCommand, Serializable {
    private final String userName;

    public UnregisterUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public void apply(Map<String, Address> users, Address src) {
        users.remove(userName, src);
    }
}
