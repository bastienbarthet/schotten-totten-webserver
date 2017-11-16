package com.boardgames.bastien.schotten_totten.model;

/**
 * Created by Bastien on 29/11/2016.
 */

public class Player {

    private final String name;

    private final Hand hand;

    private final PlayingPlayerType playingPlayerType;

    public Player(final String name, final PlayingPlayerType playingPlayerType) {
        this.name = name;
        this.hand = new Hand();
        this.playingPlayerType = playingPlayerType;
    }
    
    public Player(final String name, final PlayingPlayerType playingPlayerType, final Hand hand) {
        this.name = name;
        this.hand = hand;
        this.playingPlayerType = playingPlayerType;
    }

    public String getName() {
        return name;
    }

    public Hand getHand() {
        return hand;
    }

    public PlayingPlayerType getPlayerType() {
        return playingPlayerType;
    }

}
