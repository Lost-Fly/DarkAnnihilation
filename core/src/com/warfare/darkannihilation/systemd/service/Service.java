package com.warfare.darkannihilation.systemd.service;

import static com.badlogic.gdx.Gdx.app;
import static com.warfare.darkannihilation.Settings.VIBRATE;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.StringBuilder;

public final class Service {
    private static final StringBuilder stringBuilder = new StringBuilder();

    private static void print(String s) {
        app.error("DART", s);
    }

    public synchronized static void print(Object... objects) {
        try {
            if (objects.length == 1) {
                print(objects[0].toString());
                return;
            }
            for (Object o : objects) {
                stringBuilder.append(o).append(" ");
            }
            print(stringBuilder.deleteCharAt(stringBuilder.length - 1).toString());
            stringBuilder.clear();
        } catch (Exception exception) {
            printErr("Error When Log:", exception);
        }
    }

    public static void printErr(String comment, Exception e) {
        print(comment);
        e.printStackTrace();
    }

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            printErr("Error When Sleep:", e);
            sleep(millis);
        }
    }

    public static void makeALeak() {
        final byte[] leak = new byte[1024 * 1024 * 64];
    }

    public static void vibrate(int millis) {
        if (VIBRATE) Gdx.input.vibrate(millis);
    }
}
