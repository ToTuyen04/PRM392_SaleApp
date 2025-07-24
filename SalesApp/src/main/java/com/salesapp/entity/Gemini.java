package com.salesapp.entity;


import lombok.Getter;


@Getter
public class Gemini {
    private final String reply;
    private final boolean needHuman;

    public Gemini(String reply, boolean needHuman) {
        this.reply = reply;
        this.needHuman = needHuman;
    }

}
