package com.example.groupchat.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class LastMessage {
    public String chat;
    public String uid;

    public LastMessage() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public LastMessage(String chat, String uid) {
        this.chat = chat;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getChat() {
        return chat;
    }
}
