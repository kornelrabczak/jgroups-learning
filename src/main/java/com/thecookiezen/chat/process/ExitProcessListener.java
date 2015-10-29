package com.thecookiezen.chat.process;

public class ExitProcessListener implements ProcessListener {
    @Override
    public void process(String line) {
        if (shouldProcess(line))
            throw new RuntimeException("Exiting");
    }

    private boolean shouldProcess(String message) {
        return message.startsWith("quit") || message.startsWith("exit");
    }
}
