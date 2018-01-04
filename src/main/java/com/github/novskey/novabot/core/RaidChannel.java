package com.github.novskey.novabot.core;

public class RaidChannel extends AlertChannel {

    String chatId = null;

    public RaidChannel(String channelId, String filterName) {
        super(channelId, filterName);
    }

    public RaidChannel(String channelId) {
        super(channelId);
    }
}
