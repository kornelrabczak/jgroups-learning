package com.thecookiezen.chat;

import com.thecookiezen.chat.command.RegisterUserName;
import lombok.Data;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class State implements Serializable, Closeable {
    private final List<String> history = new LinkedList<>();
    private final ConcurrentHashMap<String, Address> users = new ConcurrentHashMap<>();
    private final String userName;
    private final String clusterName;
    private final JChannel channel;

    public State(String userName, String clusterName, URL resource) throws Exception {
        this.userName = userName;
        this.clusterName = clusterName;
        this.channel = new JChannel(resource);
        this.channel.receiver(new MessageReceiver(this));
        this.channel.connect(clusterName);
        this.channel.getState(null, 10000);

        try {
            channel.send(new Message(null, null, new RegisterUserName(userName)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addToHistory(String message) {
        synchronized (history) {
            history.add(message);
        }
    }

    public void setUp(State replicatedState) {
        users.putAll(replicatedState.getUsers());
        synchronized (history) {
            history.clear();
            history.addAll(replicatedState.getHistory());
        }
    }

    public void addUser(String userName, Address src) {
        users.put(userName, src);
    }

    public void removeUser(String userName) {
        users.remove(userName);
    }

    public Address getAddress(String userName) {
        return users.getOrDefault(userName, null);
    }

    public void close() throws IOException {
        channel.close();
    }
}
