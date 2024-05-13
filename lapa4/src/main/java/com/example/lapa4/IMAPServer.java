package com.example.lapa4;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class IMAPServer {
    static Map<String,String> configuration = new HashMap<>();
    public static String fileCredentials;
    public static ServerSocket serverSocket;

    public static void main(String[] args) throws IOException {
        for (String arg : args) {
            String[] kv = arg.split("=");
            if (kv.length != 2) {
                fatal("Invalid argument format, expected key=value: " + arg);
            }
            configuration.put(kv[0], kv[1]);
        }
        fileCredentials = config("credentials");

        System.out.println("IP: " + getListenerIP());
        System.out.println("Port: " + getListenerPort());

        System.out.println("Server started...");

        Listener listener = new Listener();
        listener.run();
    }

    static void fatal(String message) {
        System.err.println("FATAL: " + message);
        System.exit(1);
    }

    // Метод для получения значения конфигурации по имени
    private static String config(String name) {
        if (!configuration.containsKey(name)) {
            fatal("e1204142032 - Config " + name + " not defined as '" + name + "=<value>' in command line");
        }
        return configuration.get(name);
    }
    public static int getListenerPort() {
        int val;
        String strVal = config("listener-port");
        try {
            val = Integer.parseInt(strVal);
        } catch (NumberFormatException ex) {
            fatal("e1204142034 - Config listener-port defined as 'listener-port=" + strVal + "' in command line must be numeric");
            return -1; // не достигнет, так как fatal завершит программу
        }
        return val;
    }

    public static String getListenerIP() {
        return config("listener-ip");
    }
    public static String getUserPass() {
        return config("user-pass");
    }

    public static String getReceivedPath() {
        return config("received-path");
    }

    public static String getInboxPath() {
        return config("inbox-path");
    }

    public static String getCredentialsPath() {
        return config("credentials");
    }
}
