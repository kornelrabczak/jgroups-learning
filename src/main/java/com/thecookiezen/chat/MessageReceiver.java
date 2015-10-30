package com.thecookiezen.chat;

import com.thecookiezen.chat.command.UserNameCommand;
import org.jgroups.Address;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MessageReceiver extends ReceiverAdapter {

    private final State state;

    public MessageReceiver(State state) {
        this.state = state;
    }

    @Override
    public void receive(Message msg) {
        Address src = msg.getSrc();
        Object obj = msg.getObject();
        if (obj instanceof UserNameCommand) {
            ((UserNameCommand) obj).apply(state.getUsers(), src);
        } else {
            String message = src + " : " + String.valueOf(obj);
            System.out.println(message);
            Address dest = msg.dest();
            if (dest == null) {
                state.addToHistory(message);
            }
        }
    }

    @Override
    public void viewAccepted(View view) {
        System.out.println("new view : " + view);
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        Util.objectToStream(state, new DataOutputStream(output));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setState(InputStream input) throws Exception {
        State replicatedState = (State) Util.objectFromStream(new DataInputStream(input));
        state.setUp(replicatedState);
    }
}
