package com.fitpay.android.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for listener. Uses in {@link NotificationManager}
 */
public abstract class Listener {

    private String filter;

    public Map<Class, Command> mCommands;

    public Listener() {
        mCommands = new HashMap<>();
    }

    public Listener(String filter) {
        this();
        this.filter = filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Map<Class, Command> getCommands() {
        if (!StringUtils.isEmpty(filter)) {
            for (Command command : mCommands.values()) {
                Command finalCommand = command;
                command = new FilterCommand() {
                    @Override
                    public String filter() {
                        return filter;
                    }

                    @Override
                    public void execute(Object data) {
                        finalCommand.execute(data);
                    }
                };
            }
        }

        return mCommands;
    }
}