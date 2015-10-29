package com.thecookiezen.chat;

import com.thecookiezen.chat.command.RegisterUserName;
import com.thecookiezen.chat.command.UnregisterUserName;
import com.thecookiezen.chat.command.UserNameCommand;
import lombok.extern.log4j.Log4j;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.util.Util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

@Log4j
public class SimpleChat extends ReceiverAdapter {

    public static final String UDP_XML = "udp.xml";
    private final JChannel channel;
    private final int nodeId;
    private final String clusterName;
    private final String userName;
    private final State state = new State();

    public SimpleChat(JChannel channel, int nodeId, String clusterName, String userName) {
        this.channel = channel;
        this.nodeId = nodeId;
        this.clusterName = clusterName;
        this.userName = userName;
    }

    public static void main(String[] args) throws Exception {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(UDP_XML);
        if (resource == null) {
            throw new RuntimeException("Configuration not found.");
        }
        new SimpleChat(new JChannel(resource), Integer.valueOf(args[0]), args[1], args[2]).run();
    }

    public void run() throws Exception {
        channel.receiver(this);
        channel.connect(clusterName);
        channel.getState(null, 10000);
        registerUser();
        eventLoop();
        channel.close();
    }

    private void registerUser() {
        try {
            channel.send(new Message(null, null, new RegisterUserName(userName)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void eventLoop() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print("~> ");
                System.out.flush();
                String line = in.readLine().toLowerCase();

                if (line.startsWith("quit") || line.startsWith("exit")) {
                    channel.send(null, new UnregisterUserName(userName));
                    break;
                }

                if (line.startsWith("history")) {
                    System.out.println(state.getHistory());
                    continue;
                }

                if (line.startsWith("users")) {
                    System.out.println(state.getUsers());
                    continue;
                }

                Address dest = null;
                if (line.contains(":")) {
                    String[] split = line.split(":");
                    dest = state.getAddress(split[0]);
                    line = split[1];
                }

                line = "[" + userName + "] " + line;
                Message msg = new Message(dest, line);
                channel.send(msg);
            }
        } catch (IOException e) {
            System.out.println("IO exception during reading from System.in");
        } catch (Exception e) {
            System.out.println("Channel problems");
        }
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
