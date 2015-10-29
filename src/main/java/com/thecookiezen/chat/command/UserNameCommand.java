package com.thecookiezen.chat.command;

import org.jgroups.Address;

import java.util.Map;

public interface UserNameCommand {
    void apply(Map<String, Address> users, Address src);
}
