package com.thecookiezen.chat.command;

import com.thecookiezen.chat.State;

public class HistoryCommand implements Command {

    public static final String HISTORY = "history";
    private final State state;

    public HistoryCommand(State state) {
        this.state = state;
    }

    @Override
    public void execute() {
        System.out.println(state.getHistory());
    }
}
