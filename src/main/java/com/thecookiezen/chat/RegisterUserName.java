package com.thecookiezen.chat;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class RegisterUserName implements Serializable {
    private final String userName;

    public RegisterUserName(String userName) {
        this.userName = userName;
    }
}
