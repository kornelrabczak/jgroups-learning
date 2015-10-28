package com.thecookiezen.chat;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class UnregisterUserName implements Serializable {
    private final String userName;

    public UnregisterUserName(String userName) {
        this.userName = userName;
    }
}
