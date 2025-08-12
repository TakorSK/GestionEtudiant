package com.pack.uniflow.Models;

public class MockMessage {
    private String title;
    private String sender;
    private String body;

    public MockMessage(String title, String sender, String body) {
        this.title = title;
        this.sender = sender;
        this.body = body;
    }

    public String getTitle() { return title; }
    public String getSender() { return sender; }
    public String getBody() { return body; }
}
