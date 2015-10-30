package com.thecookiezen.chat.command;

import com.thecookiezen.chat.State;
import org.jgroups.Address;
import org.jgroups.Message;

public class SendMessageCommand implements Command {
    private final State state;
    private final String message;

    public SendMessageCommand(State state, String message) {
        this.message = message;
        this.state = state;
    }

    @Override
    public void execute() throws Exception {
        Address dest = null;
        String messageToSend = "";
        if (message.contains(":")) {
            String[] split = message.split(":");
            dest = state.getAddress(split[0]);
            messageToSend = split[1];
        }

        messageToSend = "[" + state.getUserName() + "] " + messageToSend;
        Message msg = new Message(dest, messageToSend);
        state.getChannel().send(msg);
    }
}
