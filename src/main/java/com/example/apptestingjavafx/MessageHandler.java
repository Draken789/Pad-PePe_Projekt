package com.example.apptestingjavafx;

import java.util.ArrayList;

public class MessageHandler {
    private ArrayList<String> messages;

    public MessageHandler() {
        messages = new ArrayList<String>();
    }

    public void addMessage(String text) {
        messages.add(text);
    }

    public ArrayList<String> getMessages() {
        return messages;
    }
}
