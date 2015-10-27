package com.thecookiezen.chat;

import lombok.extern.log4j.Log4j;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@Log4j
public class SimpleChat extends ReceiverAdapter {

    public static final String UDP_XML = "udp.xml";
    private final JChannel channel;
    private final int nodeId;
    private final String clusterName;
    private final String userName;

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
        eventLoop();
        channel.close();
    }

    private void eventLoop() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print("~> ");
                System.out.flush();
                String line = in.readLine().toLowerCase();
                if (line.startsWith("quit") || line.startsWith("exit"))
                    break;
                line = "[" + userName + "] " + line;
                Message msg = new Message(null, null, line);
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
        System.out.println(msg.getSrc() + " : " + msg.getObject());
    }

    @Override
    public void viewAccepted(View view) {
        System.out.println("new view : " + view);
    }
}
