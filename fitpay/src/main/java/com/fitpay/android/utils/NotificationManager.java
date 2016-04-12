package com.fitpay.android.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscription;

/**
 * Notification manager. Support any {@link Listener} object
 */
public final class NotificationManager {

    private static NotificationManager sInstance;

    private List<Listener> mListeners;

    private HashMap<Class, Subscription> mSubscriptions;
    private HashMap<Class, List<Command>> mCommands;

    public static NotificationManager getInstance() {
        if (sInstance == null) {
            synchronized (NotificationManager.class) {
                if (sInstance == null) {
                    sInstance = new NotificationManager();
                }
            }
        }

        return sInstance;
    }

    private NotificationManager() {
        mListeners = new ArrayList<>();
        mCommands = new HashMap<>();
        mSubscriptions = new HashMap<>();
    }

    private void subscribeTo(final Class clazz) {

        if (!mSubscriptions.containsKey(clazz)) {
            mSubscriptions.put(clazz, RxBus.getInstance().register(clazz, commit -> {
                for (Command command : mCommands.get(clazz)) {
                    command.execute(commit);
                }
            }));
        }
    }

    private void unsubscribeFrom(Class clazz) {
        if (mSubscriptions.containsKey(clazz)) {
            mSubscriptions.get(clazz).unsubscribe();
            mSubscriptions.remove(clazz);
        }
    }

    /**
     * Add current listener. !!! Don't forget to remove it
     *
     * @param listener listener
     */
    public void addListener(Listener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);

            Map<Class, Command> commands = listener.getCommands();

            for (Map.Entry<Class, Command> map : commands.entrySet()) {
                Class clazz = map.getKey();

                subscribeTo(clazz);

                if (!mCommands.containsKey(clazz)) {
                    mCommands.put(clazz, new ArrayList<>());
                }

                mCommands.get(clazz).add(map.getValue());
            }
        }
    }

    /**
     * Remove current listener
     *
     * @param listener listener
     */
    public void removeListener(Listener listener) {

        if (mListeners.contains(listener)) {
            Map<Class, Command> commands = listener.getCommands();
            for (Map.Entry<Class, Command> map : commands.entrySet()) {
                Class clazz = map.getKey();
                mCommands.get(clazz).remove(map.getValue());
                if (mCommands.get(clazz).size() == 0) {
                    mCommands.remove(clazz);
                    unsubscribeFrom(clazz);
                }
            }

            mListeners.remove(listener);
        }
    }
}
