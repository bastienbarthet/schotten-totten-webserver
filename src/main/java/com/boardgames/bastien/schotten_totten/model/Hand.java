package com.boardgames.bastien.schotten_totten.model;

import java.util.ArrayList;
import java.util.List;

import com.boardgames.bastien.schotten_totten.exceptions.HandFullException;

/**
 * Created by Bastien on 28/11/2016.
 */

public class Hand {

    private final List<Card> cards;

    public final int MAX_HAND_SIZE = 6;

    private int drawnCardIndex = -1;

    public Hand() {
        cards = new ArrayList<>();
    }

    public void addCard(final Card c) throws HandFullException {
        if (cards.size() < MAX_HAND_SIZE) {
            drawnCardIndex = 0;
            cards.add(drawnCardIndex, c);
        } else {
            throw new HandFullException(MAX_HAND_SIZE);
        }

    }

    public int getDrawnCardIndex() {
        return drawnCardIndex;
    }

    public Card playCard(final int i) {
        return cards.remove(i);
    }

    public int getHandSize() {
        return cards.size();
    }

    public List<Card> getCards() {
        return cards;
    }
}
