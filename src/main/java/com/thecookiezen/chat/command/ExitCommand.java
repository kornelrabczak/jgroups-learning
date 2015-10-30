package com.thecookiezen.chat.command;

import com.thecookiezen.chat.State;
import org.jgroups.JChannel;

public class ExitCommand implements Command {

    public static final String QUIT = "quit";
    public static final String EXIT = "exit";

    private final String userName;
    private final JChannel channel;

    public ExitCommand(State state) {
        this.userName = state.getUserName();
        this.channel = state.getChannel();
    }

    @Override
    public void execute() throws Exception {
        channel.send(null, new UnregisterUserName(userName));
        throw new RuntimeException("Exiting");
    }
}
