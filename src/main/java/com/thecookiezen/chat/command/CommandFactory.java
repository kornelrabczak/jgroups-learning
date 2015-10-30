package com.thecookiezen.chat.command;

import com.thecookiezen.chat.State;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {
    private final State state;
    private final ExitCommand exitCommand;
    private final HistoryCommand historyCommand;
    private final UsersListCommand usersListCommand;

    private final Map<String, Command> commandsMap = new HashMap<String, Command>() {{
        put(ExitCommand.QUIT, exitCommand);
        put(ExitCommand.EXIT, exitCommand);
        put(HistoryCommand.HISTORY, historyCommand);
        put(UsersListCommand.USERS_LIST, usersListCommand);
    }};

    public CommandFactory(State state) {
        this.state = state;
        this.exitCommand = new ExitCommand(state);
        this.historyCommand = new HistoryCommand(state);
        this.usersListCommand = new UsersListCommand(state);
    }

    public Command lookupCommand(String commandName) {
        return commandsMap.getOrDefault(commandName, new SendMessageCommand(state, commandName));
    }
}
