package com.sinyuk.jianyi.ui.events;

import com.sinyuk.jianyi.data.player.Player;

/**
 * Created by Sinyuk on 16/9/19.
 */
public class LoginEvent {
    private final Player player;
    public LoginEvent(Player player) {
    this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
