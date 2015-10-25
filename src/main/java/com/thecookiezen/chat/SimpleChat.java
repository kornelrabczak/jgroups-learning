package com.thecookiezen.chat;

import lombok.extern.log4j.Log4j;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

@Log4j
public class SimpleChat extends ReceiverAdapter {

    final private JChannel channel;
    final private int nodeId;

    public SimpleChat(JChannel channel, int nodeId, String clusterName) throws ChannelException {
        this.channel = channel;
        this.nodeId = nodeId;
        this.channel.connect(clusterName);
    }

    public static void main(String[] args) throws ChannelException {
        new SimpleChat(new JChannel(), Integer.valueOf(args[0]), args[1]);
    }

    public void run() {
        this.channel.close();
    }

    @Override
    public void receive(Message msg) {
        log.info(msg.getSrc() + " : " + msg.getObject());
    }

    @Override
    public void viewAccepted(View view) {
        log.info("new view : " + view);
    }
}
