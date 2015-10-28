package com.thecookiezen.chat;

public class HistoryProcessListener implements ProcessListener {

    private final State state;

    public HistoryProcessListener(State state) {
        this.state = state;
    }

    @Override
    public void process(String line) {
        System.out.println(state.getHistory());
    }

    private boolean shouldProcess(String message) {
        return message.startsWith("history");
    }
}
