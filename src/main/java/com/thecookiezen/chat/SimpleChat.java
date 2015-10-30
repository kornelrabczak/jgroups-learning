package com.thecookiezen.chat;

import com.thecookiezen.chat.command.CommandFactory;
import lombok.extern.log4j.Log4j;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@Log4j
public class SimpleChat implements Closeable {

    private static final String UDP_XML = "udp.xml";
    private final CommandFactory commandFactory;
    private final State state;

    public static void main(String[] args) throws Exception {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(UDP_XML);
        if (resource == null) {
            throw new RuntimeException("Configuration not found.");
        }

        try (SimpleChat simpleChat = new SimpleChat(resource, args[0], args[1])) {
            simpleChat.run();
        }
    }

    public SimpleChat(URL resource, String clusterName, String userName) throws Exception {
        this.state = new State(userName, clusterName, resource);
        this.commandFactory = new CommandFactory(state);
    }

    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print("~> ");
                System.out.flush();

                commandFactory.lookupCommand(in.readLine().toLowerCase()).execute();
            }
        } catch (Exception e) {
            System.out.println("Something bad....");
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        state.close();
    }
}
