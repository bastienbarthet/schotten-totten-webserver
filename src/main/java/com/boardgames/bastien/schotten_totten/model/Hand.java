package com.boardgames.bastien.schotten_totten.model;

import java.util.ArrayList;
import java.util.List;

import com.boardgames.bastien.schotten_totten.exceptions.HandFullException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
    
    public Hand(final List<Card> cards, final int drawnCardIndex) {
        this.cards = cards;
        this.drawnCardIndex = drawnCardIndex;
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

    @JsonIgnore
    public int getHandSize() {
        return cards.size();
    }

    public List<Card> getCards() {
        return cards;
    }
}
