package com.thecookiezen.chat.command;

import com.thecookiezen.chat.State;

public class UsersListCommand implements Command{

    public static final String USERS_LIST = "users";

    private final State state;

    public UsersListCommand(State state) {
        this.state = state;
    }

    @Override
    public void execute() {
        System.out.println(state.getUsers());
    }
}
