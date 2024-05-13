package com.example.lapa4;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class IMAPMessage {
    public String From;
    public String Subject;
    public List<String> To = new ArrayList<>();
    public List<String> Data = new ArrayList<>();
    private final String tempFile = "tempFileMails.txt";

   /* int port = 993;
    ServerSocket serverSocket;
    public static int cmdId=0;
kngi;jare\\\srg5466

    public IMAPMessage() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("* OK IMAP Service Ready");

            start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/

    /*public void start() {
        while (true) {
            try {
                Socket socket=serverSocket.accept();

                Listener listener =new Listener(socket,cmdId);
                Thread thread=new Thread(listener);
                thread.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }*/
}
