package com.thecookiezen.chat;

import lombok.Data;
import org.jgroups.Address;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class State implements Serializable{
    private final List<String> history = new LinkedList<>();
    private final ConcurrentHashMap<String, Address> users = new ConcurrentHashMap<>();

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
}
